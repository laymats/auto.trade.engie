package com.laymat.core.db.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.laymat.core.db.dao.UserDao;
import com.laymat.core.db.dao.UserGoodDao;
import com.laymat.core.db.entity.TradeTransaction;
import com.laymat.core.db.dao.TradeTransactionDao;
import com.laymat.core.db.entity.UserGood;
import com.laymat.core.db.service.TradeTransactionService;

import com.laymat.core.db.utils.SimpleSNBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
    private UserGoodDao userGoodDao;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveTradeTransaction(TradeTransaction tradeTransaction) {
        //创建交易单号
        tradeTransaction.setTransactionSN(SimpleSNBuilder.createrSn("0755"));

        //获取买方/卖方信息
        var buyer = userDao.selectById(tradeTransaction.getBuyerId());
        var buyerGood = userGoodDao.selectOne(new QueryWrapper<UserGood>().eq("UserId", buyer.getUserId()));
        var seller = userDao.selectById(tradeTransaction.getSellerId());
        var sellerGood = userGoodDao.selectOne(new QueryWrapper<UserGood>().eq("UserId", seller.getUserId()));

        /**
         * 交易处理流程
         * 买方扣减冻结金额，增加相应购买的商品
         * 卖方增加可用金额，扣减相应出售的商品
         */
        buyer.setFreezeMoney(buyer.getFreezeMoney().subtract(tradeTransaction.getTradeAmount()));
        buyerGood.setNiuCoin(buyerGood.getNiuCoin().add(tradeTransaction.getTradeCount()));
        userDao.updateById(buyer);
        userGoodDao.updateById(buyerGood);

        seller.setFreezeMoney(seller.getFreezeMoney().add(tradeTransaction.getTradeAmount()));
        sellerGood.setNiuCoin(sellerGood.getNiuCoin().subtract(tradeTransaction.getTradeCount()));
        userDao.updateById(seller);
        userGoodDao.updateById(sellerGood);

        return tradeTransactionDao.insert(tradeTransaction) > 0;
    }
}