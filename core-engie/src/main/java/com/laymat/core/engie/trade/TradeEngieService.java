package com.laymat.core.engie.trade;

import cn.hutool.core.util.RandomUtil;
import com.laymat.core.db.dto.SaveTradeTransaction;
import com.laymat.core.db.entity.TradeTransaction;
import com.laymat.core.db.service.TradeTransactionService;
import com.laymat.core.db.service.impl.TradeTransactionServiceImpl;
import com.laymat.core.engie.trade.order.TradeOrder;
import com.laymat.core.engie.trade.order.TradeResult;
import com.laymat.core.engie.trade.subscribe.TradeMarketSubscribe;
import com.laymat.core.engie.trade.subscribe.TradeStatus;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 核心交易引擎
 *
 * @author dell
 */
public class TradeEngieService implements TradeEngie {
    protected Logger logger = LoggerFactory.getLogger(getClass());
    private static volatile Object tradeEngieServiceLock = new Object();
    private static volatile TradeEngieService tradeEngieService = null;
    /**
     * 系统运行状态
     */
    private static volatile AtomicInteger RUN_STATUS = new AtomicInteger(0);
    /**
     * 系统运行中
     */
    private static final AtomicInteger SYSTEM_RUNNING = new AtomicInteger(1);
    /**
     * 系统停止中
     */
    private static final AtomicInteger SYSTEM_STOPING = new AtomicInteger(2);
    /**
     * 系统已停止
     */
    private static final AtomicInteger SYSTEM_STOP = new AtomicInteger(0);

    private static volatile AtomicInteger TRADE_RUN_STATUS = new AtomicInteger(0);
    private static final AtomicInteger TRADE_RUNNING = new AtomicInteger(10);
    private static final AtomicInteger TRADE_WAITING = new AtomicInteger(11);
    private static final AtomicInteger TRADE_STOP = new AtomicInteger(0);
    /**
     * 系统核心数据相关
     */
    private static volatile LinkedBlockingQueue<TradeOrder> tradeOrderMakeQueue = new LinkedBlockingQueue<>();
    private static volatile LinkedList<TradeOrder> buyerList = new LinkedList<>();
    private static volatile LinkedList<TradeOrder> sellerList = new LinkedList<>();
    private static volatile LinkedList<TradeOrder> cancelList = new LinkedList<>();
    private static volatile Object buyerLock = new Object();
    private static volatile Object sellerLock = new Object();
    private static volatile Object cancelLock = new Object();
    private static ThreadPoolExecutor scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(20);

    /**
     * 输出数据
     */
    private static volatile LinkedList<TradeOrder> buyerOrders = new LinkedList<>();
    private static volatile LinkedList<TradeOrder> sellerOrders = new LinkedList<>();
    private static volatile TradeResult[] tradeResults = new TradeResult[50];
    private static volatile BigDecimal finalHighPrice = BigDecimal.ZERO;
    private static volatile BigDecimal finalLowestPrice = BigDecimal.ZERO;

    /**
     * 数据库交互相关
     */
    private static TradeTransactionService tradeTransactionService = new TradeTransactionServiceImpl();

    private TradeMarketSubscribe tradeMarketSubscribe;

    private TradeEngieService() {

    }

    /**
     * 核心服务入口
     *
     * @return
     */
    public synchronized static TradeEngieService getService() {
        synchronized (tradeEngieServiceLock) {
            if (tradeEngieService == null) {
                tradeEngieService = new TradeEngieService();
            }
            return tradeEngieService;
        }
    }

    @Data
    class SimpleTradeOrder {
        private TradeOrder order;
        private Integer index;
    }

    /**
     * 获取购买报价最高的订单
     *
     * @return
     */
    SimpleTradeOrder getHighTradeOrder() {
        TradeOrder highBuyer = null;
        //先获取出买价最高的人
        var highAmount = BigDecimal.ZERO;
        var index = 0;
        for (var i = 0; i < buyerList.size(); i++) {
            if (buyerList.get(i).getTradePrice().compareTo(highAmount) == 1) {
                highAmount = buyerList.get(i).getTradePrice();
                highBuyer = buyerList.get(i);
                index = i;
            }
        }
        if (highBuyer == null) {
            return null;
        }
        var simpleOrder = new SimpleTradeOrder();
        simpleOrder.setOrder(highBuyer);
        simpleOrder.setIndex(index);
        return simpleOrder;
    }

    /**
     * 获取卖出报价最低的订单
     *
     * @param highAmount 购买价格
     * @return
     */
    SimpleTradeOrder getlowestTradeOrder(BigDecimal highAmount) {
        TradeOrder lowestSeller = null;
        var lowestAmount = highAmount;
        var index = 0;
        for (var i = 0; i < sellerList.size(); i++) {
            var compareResult = sellerList.get(i).getTradePrice().compareTo(lowestAmount);
            if (compareResult == -1 || compareResult == 0) {
                lowestAmount = sellerList.get(i).getTradePrice();
                lowestSeller = sellerList.get(i);
                index = i;
            }
        }
        if (lowestSeller == null) {
            return null;
        }
        var simpleOrder = new SimpleTradeOrder();
        simpleOrder.setOrder(lowestSeller);
        simpleOrder.setIndex(index);
        return simpleOrder;
    }

    /**
     * 保存交易结果
     *
     * @param tradeResult
     */
    void saveTradeResult(TradeResult tradeResult) {
        if (tradeResult != null) {
            for (var i = tradeResults.length - 1; i > 0; i--) {
                //删除最老的，按顺序位移
                tradeResults[i] = tradeResults[i - 1];
            }
            tradeResults[0] = tradeResult;

            //更新至数据库
            scheduledThreadPoolExecutor.execute(() -> {
                var tradeTransaction = new SaveTradeTransaction();
                tradeTransaction.setBuyerId(tradeResult.getBuyerId());
                tradeTransaction.setSellerId(tradeResult.getSellerId());
                tradeTransaction.setTradePrice(tradeResult.getTradePrice());
                tradeTransaction.setTradeCount(tradeResult.getTradeCount());
                tradeTransaction.setTradeAmount(tradeResult.getTradeAmount());
                tradeTransaction.setTradeTime(tradeResult.getTradeTime());
                tradeTransaction.setBuyerId(tradeResult.getBuyerId());
                tradeTransaction.setSellerId(tradeResult.getSellerId());
                tradeTransactionService.saveTradeTransaction(tradeTransaction);
            });
        }
    }

    /**
     * 更新买单信息
     *
     * @param index
     * @param order
     */
    void updateBuyOrder(Integer index, TradeOrder order) {
        synchronized (buyerLock) {
            buyerList.set(index, order);
        }
    }

    /**
     * 删除买单
     *
     * @param order
     */
    void removeBuyOrder(TradeOrder order) {
        synchronized (buyerLock) {
            buyerList.remove(order);
        }
    }

    /**
     * 更新卖单信息
     *
     * @param index
     * @param order
     */
    void updateSellOrder(Integer index, TradeOrder order) {
        synchronized (sellerLock) {
            sellerList.set(index, order);
        }
    }

    /**
     * 删除卖单
     *
     * @param order
     */
    void removeSellOrder(TradeOrder order) {
        synchronized (sellerLock) {
            sellerList.remove(order);
        }
    }

    /**
     * 订单撮合
     */
    void doTradeOrder() {
        TRADE_RUN_STATUS.set(TRADE_RUNNING.get());
        //撮合双方交易
        var buyTrade = getHighTradeOrder();
        if (buyTrade == null) {
            TRADE_RUN_STATUS.set(TRADE_WAITING.get());
            logger.info("撮合完毕.");
            return;
        }

        var sellTrade = getlowestTradeOrder(buyTrade.getOrder().getTradePrice());
        if (sellTrade == null) {
            TRADE_RUN_STATUS.set(TRADE_WAITING.get());
            logger.info("撮合完毕.");
            return;
        }

        var tradeResult = new TradeResult();
        var compareResult = buyTrade.getOrder().getTradeCount().compareTo(sellTrade.getOrder().getTradeCount());
        var finalTradeTotalAmount = BigDecimal.ZERO;
        var tradeCount = BigDecimal.ZERO;
        var tradePrice = BigDecimal.ZERO;

        //买单数量和卖单一致
        if (compareResult == 0) {
            tradePrice = sellTrade.getOrder().getTradePrice();
            tradeCount = sellTrade.getOrder().getTradeCount();
            //最终交易金额
            finalTradeTotalAmount = tradePrice.multiply(tradeCount);
            logger.info("T1:购买单价{}，购买数量{}，订单总额{}", tradePrice, tradeCount, finalTradeTotalAmount);

            this.removeSellOrder(sellTrade.getOrder());
            this.removeBuyOrder(buyTrade.getOrder());
        }

        //买单数量比卖单多
        if (compareResult == 1) {
            tradePrice = sellTrade.getOrder().getTradePrice();
            tradeCount = sellTrade.getOrder().getTradeCount();

            //剩余数量
            var finalTradeCount = buyTrade.getOrder().getTradeCount().subtract(tradeCount);
            //最终交易金额
            finalTradeTotalAmount = tradeCount.multiply(sellTrade.getOrder().getTradePrice());
            logger.info("T2:购买单价{}，购买数量{}，订单总额{}", tradePrice, tradeCount, finalTradeTotalAmount);

            buyTrade.getOrder().setTradeCount(finalTradeCount);
            this.removeSellOrder(sellTrade.getOrder());
            this.updateBuyOrder(buyTrade.getIndex(), buyTrade.getOrder());
        }

        //买单数量比卖单少
        if (compareResult == -1) {
            tradePrice = sellTrade.getOrder().getTradePrice();
            tradeCount = buyTrade.getOrder().getTradeCount();
            //剩余数量
            var finalTradeCount = sellTrade.getOrder().getTradeCount().subtract(tradeCount);
            //最终交易金额
            finalTradeTotalAmount = tradeCount.multiply(tradePrice);
            logger.info("T3:购买单价{}，购买数量{}，订单总额{}", tradePrice, tradeCount, finalTradeTotalAmount);

            sellTrade.getOrder().setTradeCount(finalTradeCount);
            this.removeBuyOrder(buyTrade.getOrder());
            this.updateSellOrder(sellTrade.getIndex(), sellTrade.getOrder());
        }

        tradeResult.setBuyerTradeId(buyTrade.getOrder().getTradeId());
        tradeResult.setBuyerId(buyTrade.getOrder().getUserId());
        tradeResult.setSellerTradeId(sellTrade.getOrder().getTradeId());
        tradeResult.setSellerId(sellTrade.getOrder().getUserId());
        tradeResult.setTradePrice(tradePrice);
        tradeResult.setTradeCount(tradeCount);
        tradeResult.setTradeAmount(finalTradeTotalAmount);

        //初始化
        if (finalHighPrice.compareTo(BigDecimal.ZERO) == 0) {
            finalHighPrice = tradePrice;
        }
        if (finalLowestPrice.compareTo(BigDecimal.ZERO) == 0) {
            finalLowestPrice = tradePrice;
        }

        //设置最高/最低
        if (tradePrice.compareTo(finalHighPrice) == 1) {
            finalHighPrice = tradePrice;
        }
        if (tradePrice.compareTo(finalLowestPrice) == -1) {
            finalLowestPrice = tradePrice;
        }

        logger.info("持续撮合中...");
        this.saveTradeResult(tradeResult);
        this.doTradeOrder();
    }

    /**
     * 撤销订单
     */
    void doCancleOrder() {
        var buyerRemoves = new ArrayList<TradeOrder>();
        var sellerRemoves = new ArrayList<TradeOrder>();
        for (var cancelOrder : cancelList) {
            for (var buyer : buyerList) {
                if (cancelOrder.getUserId().compareTo(buyer.getUserId()) == 0 && cancelOrder.getTradePrice().compareTo(buyer.getTradePrice()) == 0) {
                    buyerRemoves.add(buyer);
                }
            }
            for (var seller : sellerList) {
                if (cancelOrder.getUserId().compareTo(seller.getUserId()) == 0 && cancelOrder.getTradePrice().compareTo(seller.getTradePrice()) == 0) {
                    sellerRemoves.add(seller);
                }
            }
        }
        for (var buyer : buyerRemoves) {
            this.removeBuyOrder(buyer);
        }
        for (var seller : sellerRemoves) {
            this.removeSellOrder(seller);
        }
        cancelList.clear();
    }

    /**
     * 添加买单
     *
     * @param order
     */
    void addBuyer(TradeOrder order) {
        synchronized (buyerLock) {
            //检查用户是有存在相同下单信息
            var checkUserBuyOrderExist = false;
            for (var buyer : buyerList) {
                //如果出现新订单用户已下单且下单单价一致的情况下，对订单进行合并操作
                if (buyer.getUserId().equals(order.getUserId())
                        && buyer.getTradePrice().equals(order.getTradePrice())) {
                    var newBuyCount = order.getTradeCount().add(buyer.getTradeCount());
                    buyer.setTradeCount(newBuyCount);
                    checkUserBuyOrderExist = true;
                }
            }

            if (!checkUserBuyOrderExist) {
                buyerList.add(order);
            }
        }
    }

    /**
     * 添加卖单
     *
     * @param order
     */
    void addSeller(TradeOrder order) {
        synchronized (sellerLock) {
            //检查用户是有存在相同下单信息
            var checkUserSellOrderExist = false;
            for (var seller : sellerList) {
                //如果出现新订单用户已下单且下单单价一致的情况下，对订单进行合并操作
                if (seller.getUserId().equals(order.getUserId())
                        && seller.getTradePrice().equals(order.getTradePrice())) {
                    var newBuyCount = order.getTradeCount().add(seller.getTradeCount());
                    seller.setTradeCount(newBuyCount);
                    checkUserSellOrderExist = true;
                }
            }

            if (!checkUserSellOrderExist) {
                sellerList.add(order);
            }
        }
    }

    /**
     * 添加等待取消订单
     *
     * @param order
     */
    void addCancelOrder(TradeOrder order) {
        synchronized (cancelLock) {
            cancelList.add(order);
        }
    }

    void makeOrderHandle() {
        var size = tradeOrderMakeQueue.size();
        if (size == 0) {
            return;
        }

        /**
         * 获取等待处理的队列数据，处理思路示例
         * 1、根据下单类型将订单数据划分到具体买/卖集合
         * 2、交易撮合
         */
        var buyCount = 0;
        var sellCount = 0;
        for (var i = 0; i < size; i++) {
            var tradeOrder = tradeOrderMakeQueue.poll();
            if (tradeOrder.isCancel()) {
                this.addCancelOrder(tradeOrder);
            } else {
                if (tradeOrder.isBuyer()) {
                    buyCount++;
                    this.addBuyer(tradeOrder);
                } else {
                    sellCount++;
                    this.addSeller(tradeOrder);
                }
            }
        }
        logger.info("获取新交易请求共{}个，买单{}，卖单{}，累计买单{}，卖单{}", size, buyCount, sellCount, buyerList.size(), sellerList.size());
        this.doCancleOrder();
        this.doTradeOrder();
    }

    LinkedList<TradeOrder> getBuyerList() {
        var tradeOrders = new LinkedList<TradeOrder>();
        for (var buyer : buyerList) {
            var tradeOrder = new TradeOrder();
            tradeOrder.setTradePrice(buyer.getTradePrice());
            tradeOrder.setTradeCount(buyer.getTradeCount());
            tradeOrder.setTotalAmount(buyer.getTotalAmount());
            tradeOrders.add(tradeOrder);
        }
        return tradeOrders;
    }

    LinkedList<TradeOrder> getSellerList() {
        var tradeOrders = new LinkedList<TradeOrder>();
        for (var buyer : sellerList) {
            var tradeOrder = new TradeOrder();
            tradeOrder.setTradePrice(buyer.getTradePrice());
            tradeOrder.setTradeCount(buyer.getTradeCount());
            tradeOrder.setTotalAmount(buyer.getTotalAmount());
            tradeOrders.add(tradeOrder);
        }
        return tradeOrders;
    }

    HashMap<BigDecimal, TradeOrder> mergeOrderShow(LinkedList<TradeOrder> tradeOrders) {
        var tempBuyerMaps = new HashMap<BigDecimal, TradeOrder>();
        for (var orders : tradeOrders) {
            if (tempBuyerMaps.containsKey(orders.getTradePrice())) {
                var tempData = tempBuyerMaps.get(orders.getTradePrice());
                tempData.setTradeCount(tempData.getTradeCount().add(orders.getTradeCount()));
                tempBuyerMaps.put(tempData.getTradePrice(), tempData);
            } else {
                tempBuyerMaps.put(orders.getTradePrice(), orders);
            }
        }
        return tempBuyerMaps;
    }

    /**
     * 创建买单列表
     */
    void makeBuyOrderShow() {
        var tempMaps = this.mergeOrderShow(this.getBuyerList());

        var priceKeyArray = tempMaps.keySet().toArray();
        Arrays.sort(priceKeyArray);

        //取前五十条
        buyerOrders = new LinkedList<>();
        for (var buyerKey : priceKeyArray) {
            if (buyerOrders.size() < 50) {
                var buyer = tempMaps.get(buyerKey);
                if (buyer != null) {
                    buyerOrders.add(buyer);
                }
            } else {
                break;
            }
        }
    }

    /**
     * 创建卖单列表
     */
    void makeSellOrderShow() {
        var tempMaps = this.mergeOrderShow(this.getSellerList());

        var priceKeyArray = tempMaps.keySet().toArray();
        Arrays.sort(priceKeyArray);

        //取前五十条
        sellerOrders = new LinkedList<>();
        for (var buyerKey : priceKeyArray) {
            if (sellerOrders.size() < 50) {
                var buyer = tempMaps.get(buyerKey);
                if (buyer != null) {
                    sellerOrders.add(buyer);
                }
            } else {
                break;
            }
        }
    }

    void makeTradeMarket() {
        if (tradeMarketSubscribe != null) {

            var status = new TradeStatus();
            status.setHighPrice(finalHighPrice);
            status.setLowPrice(finalLowestPrice);
            status.setBuyer(buyerOrders);
            status.setSeller(sellerOrders);
            status.setTrades(getTradeResults());

            tradeMarketSubscribe.doData(status);
        }
    }

    void start() {
        new Thread(() -> {
            logger.info("核心交易服务已启动.");
            while (true) {
                try {
                    TimeUnit.MILLISECONDS.sleep(10);
                    this.makeOrderHandle();
                } catch (Exception e) {
                    logger.error("TS1:{}", e.getMessage());
                    continue;
                }
            }
        }).start();
        new Thread(() -> {
            logger.info("交易市场信息处理服务已启动.");
            while (true) {
                try {
                    TimeUnit.MILLISECONDS.sleep(10);
                    this.makeBuyOrderShow();
                    this.makeSellOrderShow();
                } catch (Exception e) {
                    logger.error("TS2:{}", e.getMessage());
                    continue;
                }
            }
        }).start();
        new Thread(() -> {
            logger.info("交易市场信息推送已启动.");
            while (true) {
                try {
                    TimeUnit.MILLISECONDS.sleep(300);
                    this.makeTradeMarket();
                } catch (Exception e) {
                    logger.error("TS3:{}", e.getMessage());
                    continue;
                }
            }
        }).start();
    }

    @Override
    public boolean startEngie() {
        if (RUN_STATUS.get() == SYSTEM_STOP.get()) {
            logger.info("撮合引擎启动中...");
            RUN_STATUS.set(SYSTEM_RUNNING.get());
            this.start();
            logger.info("撮合引擎启动成功.");
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean stopEngie() {
        if (RUN_STATUS.get() == SYSTEM_RUNNING.get()) {
            RUN_STATUS.set(SYSTEM_STOPING.get());
            logger.info("撮合引擎停止中...");

            while (TRADE_RUN_STATUS.get() != TRADE_STOP.get()) {
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            logger.info("撮合引擎停止成功.");
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean placeOrder(TradeOrder order) {
        if (RUN_STATUS.get() == SYSTEM_RUNNING.get()) {
            order.setTotalAmount(order.getTradePrice().multiply(order.getTradeCount()));
            tradeOrderMakeQueue.add(order);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean cancelOrder(TradeOrder order) {
        if (RUN_STATUS.get() == SYSTEM_RUNNING.get()) {
            tradeOrderMakeQueue.add(order);
            return false;
        } else {
            return true;
        }
    }

    @Override
    public List<TradeOrder> getBuyers() {
        return buyerOrders;
    }

    @Override
    public List<TradeOrder> getSellers() {
        return sellerOrders;
    }

    @Override
    public TradeResult[] getTradeResults() {
        var newList = new ArrayList<>();
        for (var order : tradeResults) {
            if (order != null) {
                newList.add(order);
            }
        }
        return newList.toArray(new TradeResult[newList.size()]);
    }

    @Override
    public TradeStatus getTradeStatus() {
        var status = new TradeStatus();
        status.setHighPrice(finalHighPrice);
        status.setLowPrice(finalLowestPrice);
        return status;
    }

    @Override
    public void addStatusEvent(TradeMarketSubscribe subscribe) {
        this.tradeMarketSubscribe = subscribe;
    }
}
