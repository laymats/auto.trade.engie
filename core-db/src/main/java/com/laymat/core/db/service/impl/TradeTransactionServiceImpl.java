package com.laymat.core.db.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.laymat.core.db.dao.TradeTransactionDao;
import com.laymat.core.db.dao.UserDao;
import com.laymat.core.db.dao.UserGoodDao;
import com.laymat.core.db.dao.UserTradeOrderDao;
import com.laymat.core.db.dto.SaveTradeTransaction;
import com.laymat.core.db.entity.UserGood;
import com.laymat.core.db.entity.TradeOrders;
import com.laymat.core.db.service.TradeTransactionService;
import com.laymat.core.db.utils.SimpleSNBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;

/**
 * (TmTradetransaction)表服务实现类
 *
 * @author laymat
 * @since 2019-07-19 16:00:23
 */
@Service("tradeTransactionService")
public class TradeTransactionServiceImpl implements TradeTransactionService {
    @Autowired
    private TradeTransactionDao tradeTransactionDao;
    @Autowired
    private UserDao userDao;
    @Autowired
    private UserTradeOrderDao userTradeOrderDao;
    @Autowired
    private UserGoodDao userGoodDao;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveTradeTransaction(SaveTradeTransaction tradeTransaction) {
        //创建交易单号
        tradeTransaction.setTransactionSN(SimpleSNBuilder.createrSn("0755"));

        //获取买方/卖方信息
        var buyer = userDao.selectById(tradeTransaction.getBuyerId());
        var buyerGood = userGoodDao.selectOne(new QueryWrapper<UserGood>().eq("UserId", buyer.getUserId()));

        /**
         * 10元买9元卖，出现冻结不恢复
         */
        if (buyer != null && buyerGood != null) {

            /**
             * 买方交易订单更新
             * 1、判断交易后的余额
             * 2、如果余额大于0则表示未交易完，只更新交易时间
             * 3、如果余额小于0，设置为0，更新完成时间
             */
            var buyerOrder = userTradeOrderDao.selectOne(new QueryWrapper<TradeOrders>()
                    .eq("TradeId", tradeTransaction.getBuyerTradeId()));
            var buyerSurplusCount = buyerOrder.getSurplusCount();
            var buyerSurplusAmount = buyerOrder.getSurplusAmount();

            /**
             * 1、订单如果是第一次交易，使用订单数量减去交易数量，得到剩余数量
             * 2、订单如果是第二次交易，使用剩余数量减去交易数量，得到剩余数量
             */
            if (buyerSurplusCount.compareTo(BigDecimal.ZERO) != 0) {
                buyerSurplusCount = buyerSurplusCount.subtract(tradeTransaction.getTradeCount());
            } else {
                buyerSurplusCount = buyerOrder.getTradeCount().subtract(tradeTransaction.getTradeCount());
            }

            buyerSurplusAmount =
                    buyerSurplusAmount.subtract(tradeTransaction.getTradeCount().multiply(buyerOrder.getTradePrice()));

            buyerOrder.setSurplusCount(buyerSurplusCount);
            buyerOrder.setSurplusAmount(buyerSurplusAmount);

            var buyerCompareResult = buyerSurplusCount.compareTo(BigDecimal.ZERO);
            if (buyerCompareResult == 0 || buyerCompareResult == -1) {
                buyerOrder.setFinishDate(tradeTransaction.getTradeTime());
            } else {
                buyerOrder.setTradeDate(tradeTransaction.getTradeTime());
            }
            userTradeOrderDao.updateById(buyerOrder);

            /**
             * 卖方交易订单更新
             */
            var sellerOrder = userTradeOrderDao.selectOne(new QueryWrapper<TradeOrders>().eq("TradeId",
                    tradeTransaction.getSellerTradeId()));
            var sellerSurplusCount = sellerOrder.getSurplusCount();

            /**
             * 1、订单如果是第一次交易，使用订单数量减去交易数量，得到剩余数量
             * 2、订单如果是第二次交易，使用剩余数量减去交易数量，得到剩余数量
             */
            if (sellerSurplusCount.compareTo(BigDecimal.ZERO) != 0) {
                sellerSurplusCount = sellerSurplusCount.subtract(tradeTransaction.getTradeCount());
            } else {
                sellerSurplusCount = sellerOrder.getTradeCount().subtract(tradeTransaction.getTradeCount());
            }


            sellerOrder.setSurplusAmount(sellerSurplusCount.multiply(sellerOrder.getTradePrice()));
            sellerOrder.setSurplusCount(sellerSurplusCount);

            var sellerCompareResult = sellerSurplusCount.compareTo(BigDecimal.ZERO);
            if (sellerCompareResult == 0 || sellerCompareResult == -1) {
                sellerOrder.setFinishDate(tradeTransaction.getTradeTime());
            } else {
                sellerOrder.setTradeDate(tradeTransaction.getTradeTime());
            }
            userTradeOrderDao.updateById(sellerOrder);

            /**
             * 交易处理流程
             * 买方扣减冻结金额（买方价格*交易数量），增加购买的商品
             * 卖方扣减冻结牛币，增加可用金额（成交金额）
             */

            //扣减冻结金额
            var deductionFreezeMoney =
                    buyer.getFreezeMoney().subtract(buyerOrder.getTradePrice().multiply(tradeTransaction.getTradeCount()));
            buyer.setFreezeMoney(deductionFreezeMoney);

            //核心逻辑，（买方价格-卖方价格)*交易金额即为解冻金额
            var addMoney = buyerOrder.getTradePrice().subtract(tradeTransaction.getTradePrice()).multiply(tradeTransaction.getTradeCount());
            //恢复多余金额
            buyer.setUserMoney(buyer.getUserMoney().add(addMoney));
            //增加可用牛币
            buyerGood.setNiuCoin(buyerGood.getNiuCoin().add(tradeTransaction.getTradeCount()));
            userDao.updateById(buyer);
            userGoodDao.updateById(buyerGood);

            var seller = userDao.selectById(tradeTransaction.getSellerId());
            var sellerGood = userGoodDao.selectOne(new QueryWrapper<UserGood>().eq("UserId", seller.getUserId()));

            //扣减冻结牛币
            var deductionFreezeCoin = sellerGood.getFreezeNiuCoin().subtract(tradeTransaction.getTradeCount());
            sellerGood.setFreezeNiuCoin(deductionFreezeCoin);
            //增加可用金额
            seller.setUserMoney(seller.getUserMoney().add(tradeTransaction.getTradeAmount()));
            userDao.updateById(seller);
            userGoodDao.updateById(sellerGood);

            tradeTransaction.setTradeTime(new Date());
            return tradeTransactionDao.insert(tradeTransaction) > 0;
        }
        return false;
    }
}