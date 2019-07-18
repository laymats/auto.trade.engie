package com.laymat.core.engie.trade;

import cn.hutool.core.util.RandomUtil;
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
    private static volatile LinkedBlockingQueue<TradeOrder> tradeOrderCancelQueue = new LinkedBlockingQueue<>();
    private static volatile LinkedList<TradeOrder> buyerList = new LinkedList<>();
    private static volatile LinkedList<TradeOrder> sellerList = new LinkedList<>();
    private static volatile Object buyerLock = new Object();
    private static volatile Object sellerLock = new Object();

    /**
     * 输出数据
     */
    private static volatile LinkedList<TradeOrder> buyerOrders = new LinkedList<>();
    private static volatile LinkedList<TradeOrder> sellerOrder = new LinkedList<>();
    private static volatile TradeResult[] tradeResults = new TradeResult[50];
    private static volatile BigDecimal finalHighPrice = BigDecimal.ZERO;
    private static volatile BigDecimal finalLowestPrice = BigDecimal.ZERO;

    private TradeMarketSubscribe tradeMarketSubscribe;

    private TradeEngieService() {

    }

    public static TradeEngieService getService() {
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
            if (buyerList.get(i).getTradeAmount().compareTo(highAmount) == 1) {
                highAmount = buyerList.get(i).getTradeAmount();
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
            var compareResult = sellerList.get(i).getTradeAmount().compareTo(lowestAmount);
            if (compareResult == -1 || compareResult == 0) {
                lowestAmount = sellerList.get(i).getTradeAmount();
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

    void saveTradeResult(TradeResult tradeResult) {
        if (tradeResult != null) {
            tradeResult.setOrderTime(new Date());
            tradeResult.setOrderSN(RandomUtil.randomString(15));
            for (var i = tradeResults.length - 1; i > 0; i--) {
                //删除最老的，按顺序位移
                tradeResults[i] = tradeResults[i - 1];
            }
            tradeResults[0] = tradeResult;
        }
    }

    void updateBuyOrder(Integer index, TradeOrder order) {
        synchronized (buyerLock) {
            buyerList.set(index, order);
        }
    }

    void removeBuyOrder(TradeOrder order) {
        synchronized (buyerLock) {
            buyerList.remove(order);
        }
    }

    void updateSellOrder(Integer index, TradeOrder order) {
        synchronized (sellerLock) {
            sellerList.set(index, order);
        }
    }

    void removeSellOrder(TradeOrder order) {
        synchronized (sellerLock) {
            sellerList.remove(order);
        }
    }

    void doTradeOrder() {
        TRADE_RUN_STATUS.set(TRADE_RUNNING.get());
        //撮合双方交易
        var buyTrade = getHighTradeOrder();
        if (buyTrade == null) {
            TRADE_RUN_STATUS.set(TRADE_WAITING.get());
            logger.info("撮合完毕.");
            return;
        }

        var sellTrade = getlowestTradeOrder(buyTrade.getOrder().getTradeAmount());
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
            tradePrice = sellTrade.getOrder().getTradeAmount();
            tradeCount = sellTrade.getOrder().getTradeCount();
            //最终交易金额
            finalTradeTotalAmount = tradePrice.multiply(tradeCount);
            logger.info("T1:购买单价{}，购买数量{}，订单总额{}", tradePrice, tradeCount, finalTradeTotalAmount);

            this.removeSellOrder(sellTrade.getOrder());
            this.removeBuyOrder(buyTrade.getOrder());
        }

        //买单数量比卖单多
        if (compareResult == 1) {
            tradePrice = sellTrade.getOrder().getTradeAmount();
            tradeCount = sellTrade.getOrder().getTradeCount();

            //剩余数量
            var finalTradeCount = buyTrade.getOrder().getTradeCount().subtract(tradeCount);
            //最终交易金额
            finalTradeTotalAmount = tradeCount.multiply(sellTrade.getOrder().getTradeAmount());
            logger.info("T2:购买单价{}，购买数量{}，订单总额{}", tradePrice, tradeCount, finalTradeTotalAmount);

            buyTrade.getOrder().setTradeCount(finalTradeCount);
            this.removeSellOrder(sellTrade.getOrder());
            this.updateBuyOrder(buyTrade.getIndex(), buyTrade.getOrder());
        }

        //买单数量比卖单少
        if (compareResult == -1) {
            tradePrice = sellTrade.getOrder().getTradeAmount();
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

        tradeResult.setBuyerId(buyTrade.getOrder().getUserId());
        tradeResult.setSellerId(sellTrade.getOrder().getUserId());
        tradeResult.setTradePrice(tradePrice);
        tradeResult.setTradeCount(tradeCount);
        tradeResult.setOrderAmount(finalTradeTotalAmount);

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

    void addBuyer(TradeOrder order) {
        synchronized (buyerLock) {
            //检查用户是有存在相同下单信息
            var checkUserBuyOrderExist = false;
            for (var buyer : buyerList) {
                //如果出现新订单用户已下单且下单单价一致的情况下，对订单进行合并操作
                if (buyer.getUserId().equals(order.getUserId())
                        && buyer.getTradeAmount().equals(order.getTradeAmount())) {
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

    void addSeller(TradeOrder order) {
        synchronized (sellerLock) {
            //检查用户是有存在相同下单信息
            var checkUserSellOrderExist = false;
            for (var seller : sellerList) {
                //如果出现新订单用户已下单且下单单价一致的情况下，对订单进行合并操作
                if (seller.getUserId().equals(order.getUserId())
                        && seller.getTradeAmount().equals(order.getTradeAmount())) {
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

    void makeOrderHandle() {
        var size = tradeOrderMakeQueue.size();
        if (size == 0) {
            return;
        }

        /**
         * 获取等待处理的队列数据，处理思路示例
         * 1、根据算法模型，每次取出指定数据大小，列如100/1000/10000，再根据算法权重去计算哪一个id符合条件（适合控制抽奖活动）
         * 2、取出所有数据，并将id进行组合提交到后台查询，然后再将组合查询的结果拆分之后返回给客户端（适合海量请求处理）
         */
        var buyCount = 0;
        var sellCount = 0;
        for (var i = 0; i < size; i++) {
            var tradeOrder = tradeOrderMakeQueue.poll();
            if (tradeOrder.isBuyer()) {
                buyCount++;
                this.addBuyer(tradeOrder);
            } else {
                sellCount++;
                this.addSeller(tradeOrder);
            }
        }
        logger.info("获取新交易请求共{}个，买单{}，卖单{}", size, buyCount, sellCount);
        this.doTradeOrder();
    }

    boolean checkBuyerExsit(TradeOrder order) {
        for (var buy : buyerOrders) {
            if (order.getTradeId().equals(buy.getTradeId())) {
                return true;
            }
        }
        return false;
    }

    void makeOrderShow() {

        for (var buyer : buyerList) {
            var newUpdate = false;
            if (buyerOrders.size() < 50) {
                if (!checkBuyerExsit(buyer)) {
                    buyerOrders.add(buyer);
                    newUpdate = true;
                }
            } else {
                //添加前50
                for (var i = 0; i < buyerOrders.size(); i++) {
                    if (buyerOrders.get(i) != null) {
                        var compareResult = buyerOrders.get(i).getTradeAmount().compareTo(buyer.getTradeAmount());
                        if (compareResult == -1) {
                            buyerOrders.set(i - 1 < 0 ? 0 : i - 1, buyerOrders.get(i));
                            buyerOrders.set(i, buyer);
                            newUpdate = true;
                        }
                    }
                }
            }

            if(newUpdate) {
                //合并价格相同
                var tempList = new LinkedList<TradeOrder>();
                for (var tempBuyer : buyerOrders) {
                    tempList.add(tempBuyer);
                }
                for (var i = 0; i < buyerOrders.size(); i++) {
                    for (var j = 0; j < tempList.size(); j++) {
                        var compareResult = buyerOrders.get(i).getTradeAmount().compareTo(tempList.get(j).getTradeAmount());
                        if (compareResult == 0 && buyerOrders.get(i).getUserId().compareTo(tempList.get(j).getUserId()) != 0) {
                            var oldOrder = buyerOrders.get(i);
                            oldOrder.setTradeCount(oldOrder.getTradeCount().add(tempList.get(j).getTradeCount()));
                            buyerOrders.set(i, oldOrder);
                            tempList.remove(j);
                        }
                    }
                }
            }
        }
//        for (var seller : sellerList) {
//            for (var i = 0; i < sellerOrder.size(); i++) {
//                if (sellerOrder.get(i) != null) {
//                    if (sellerOrder.get(i).getTradeAmount().compareTo(seller.getTradeAmount()) == 1) {
//                        var index = i + 1 > 50 ? 49 : i + 1;
//                        if (sellerOrder.size() < index) {
//                            sellerOrder.add(seller);
//                        } else {
//                            sellerOrder.set(index, sellerOrder.get(i));
//                            sellerOrder.set(i, seller);
//                        }
//                    }
//                }
//            }
//        }
    }

    void makeTradeMarket() {
        if (tradeMarketSubscribe != null) {

            var status = new TradeStatus();
            status.setHighPrice(finalHighPrice);
            status.setLowPrice(finalLowestPrice);
            status.setBuyer(buyerOrders);
            status.setSeller(sellerOrder);
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
                    this.makeOrderShow();
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
            order.setTotalAmount(order.getTradeAmount().multiply(order.getTradeCount()));
            tradeOrderMakeQueue.add(order);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean cancelOrder(TradeOrder order) {
        if (RUN_STATUS.get() == SYSTEM_RUNNING.get()) {
            order.setTotalAmount(order.getTradeAmount().multiply(order.getTradeCount()));
            tradeOrderCancelQueue.add(order);
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
        return sellerOrder;
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
