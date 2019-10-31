package com.laymat.core.db.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.laymat.core.db.dao.TradeTransactionDao;
import com.laymat.core.db.dao.UserDao;
import com.laymat.core.db.dao.UserGoodDao;
import com.laymat.core.db.dao.UserTradeOrderDao;
import com.laymat.core.db.dto.SaveTradeTransaction;
import com.laymat.core.db.entity.UserGood;
import com.laymat.core.db.entity.UserTradeOrder;
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
        var seller = userDao.selectById(tradeTransaction.getSellerId());
        var sellerGood = userGoodDao.selectOne(new QueryWrapper<UserGood>().eq("UserId", seller.getUserId()));

        /**
         * 10元买9元卖，出现冻结不恢复
         */
        if (buyer != null && buyerGood != null && seller != null && sellerGood != null) {

            /**
             * 买方交易订单更新
             * 1、判断交易后的余额
             * 2、如果余额大于0则表示未交易完，只更新交易时间
             * 3、如果余额小于0，设置为0，更新完成时间
             */
            var buyerOrder = userTradeOrderDao.selectOne(new QueryWrapper<UserTradeOrder>()
                    .eq("TradeId", tradeTransaction.getBuyerTradeId()));
            var buyerSurplusCount = buyerOrder.getSurplusCount();

            /**
             * 1、订单如果是第一次交易，使用订单数量减去交易数量，得到剩余数量
             * 2、订单如果是第二次交易，使用剩余数量减去交易数量，得到剩余数量
             */
            if (buyerSurplusCount.compareTo(BigDecimal.ZERO) != 0) {
                buyerSurplusCount = buyerSurplusCount.subtract(tradeTransaction.getTradeCount());
            }else{
                buyerSurplusCount =  buyerOrder.getTradeCount().subtract(tradeTransaction.getTradeCount());
            }

            var buyerCompareResult = buyerSurplusCount.compareTo(BigDecimal.ZERO);

            if (buyerCompareResult == 1) {
                buyerOrder.setSurplusCount(buyerSurplusCount);
            }
            if (buyerCompareResult == -1) {
                buyerOrder.setSurplusAmount(BigDecimal.ZERO);
            }

            if (buyerCompareResult == 0 || buyerCompareResult == -1) {
                buyerOrder.setFinishDate(tradeTransaction.getTradeTime());
            } else {
                buyerOrder.setTradeDate(tradeTransaction.getTradeTime());
            }
            userTradeOrderDao.updateById(buyerOrder);

            /**
             * 卖方交易订单更新
             */
            var sellerOrder = userTradeOrderDao.selectOne(new QueryWrapper<UserTradeOrder>().eq("TradeId", tradeTransaction.getSellerTradeId()));
            var sellerSurplusCount = sellerOrder.getSurplusCount();

            /**
             * 1、订单如果是第一次交易，使用订单数量减去交易数量，得到剩余数量
             * 2、订单如果是第二次交易，使用剩余数量减去交易数量，得到剩余数量
             */
            if (sellerSurplusCount.compareTo(BigDecimal.ZERO) != 0) {
                sellerSurplusCount = sellerSurplusCount.subtract(tradeTransaction.getTradeCount());
            }else{
                sellerSurplusCount =  sellerOrder.getTradeCount().subtract(tradeTransaction.getTradeCount());
            }

            var sellerCompareResult = sellerSurplusCount.compareTo(BigDecimal.ZERO);

            if (sellerCompareResult == 1) {
                sellerOrder.setSurplusCount(sellerSurplusCount);
            }
            if (sellerCompareResult == -1) {
                sellerOrder.setSurplusCount(BigDecimal.ZERO);
            }

            if (sellerCompareResult == 0 || sellerCompareResult == -1) {
                sellerOrder.setFinishDate(tradeTransaction.getTradeTime());
            } else {
                sellerOrder.setTradeDate(tradeTransaction.getTradeTime());
            }
            userTradeOrderDao.updateById(sellerOrder);

            /**
             * 交易处理流程
             * 买方扣减冻结金额（买方下单价格*当前交易数量），增加购买的商品
             * 卖方扣减冻结商品，增加可用金额
             */

            //扣减冻结金额
            var deductionFreezeAmount = buyerOrder.getTradePrice().multiply(tradeTransaction.getTradeCount());
            //解冻金额
            var unfreezeAmount = deductionFreezeAmount.subtract(tradeTransaction.getTradeAmount());
            buyer.setFreezeMoney(buyer.getFreezeMoney().subtract(deductionFreezeAmount));
            buyer.setUserMoney(buyer.getUserMoney().add(unfreezeAmount));
            //增加可用牛币
            buyerGood.setNiuCoin(buyerGood.getNiuCoin().add(tradeTransaction.getTradeCount()));
            userDao.updateById(buyer);
            userGoodDao.updateById(buyerGood);

            //扣减冻结牛币
            var deductionFreezeCoin = sellerGood.getFreezeNiuCoin().subtract(tradeTransaction.getTradeCount());
            sellerGood.setFreezeNiuCoin(deductionFreezeCoin);
            seller.setUserMoney(seller.getUserMoney().add(tradeTransaction.getTradeAmount()));
            userDao.updateById(seller);
            userGoodDao.updateById(sellerGood);

            tradeTransaction.setTradeTime(new Date());
            return tradeTransactionDao.insert(tradeTransaction) > 0;
        }
        return false;
    }
}