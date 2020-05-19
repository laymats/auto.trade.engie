package com.laymat.core.db.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.laymat.core.db.dao.UserDao;
import com.laymat.core.db.dao.UserGoodDao;
import com.laymat.core.db.dto.SaveUserOrder;
import com.laymat.core.db.entity.UserGood;
import com.laymat.core.db.entity.TradeOrders;
import com.laymat.core.db.dao.UserTradeOrderDao;
import com.laymat.core.db.service.UserTradeOrderService;

import com.laymat.core.db.utils.exception.impl.SimpleException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * (TmUsertradeorder)表服务实现类
 *
 * @author laymat
 * @since 2019-07-19 16:49:23
 */
@Service("userTradeOrderService")
public class UserTradeOrderServiceImpl implements UserTradeOrderService {
    @Autowired
    private UserTradeOrderDao userTradeOrderDao;
    @Autowired
    private UserGoodDao userGoodDao;
    @Autowired
    private UserDao userDao;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean placeOrder(SaveUserOrder saveUserOrder) {
        var userOrder = new TradeOrders();
        BeanUtil.copyProperties(saveUserOrder, userOrder);

        var checkPrice = saveUserOrder.getTradePrice().compareTo(BigDecimal.ZERO);
        if (checkPrice == -1 || checkPrice == 0) {
            throw new SimpleException("价格异常[%s]", saveUserOrder.getTradePrice());
        }

        var checkCountResult = saveUserOrder.getTradeCount().compareTo(BigDecimal.ZERO);
        if (checkCountResult == -1 || checkCountResult == 0) {
            throw new SimpleException("数量异常[%s]", saveUserOrder.getTradeCount());
        }

        userOrder.setTradeId(IdUtil.fastUUID());
        userOrder.setTradeDate(new Date());
        var userInfo = userDao.selectById(userOrder.getUserId());

        if (userInfo == null) {
            throw new SimpleException("用户不存在[%s]", userOrder.getUserId());
        }
        userOrder.setTradeAmount(userOrder.getTradePrice().multiply(userOrder.getTradeCount()));
        if (userOrder.getBuyer() == 1) {
            var money = userOrder.getTradeCount().multiply(userOrder.getTradePrice());
            var checkMoney = userInfo.getUserMoney().compareTo(money);
            if (checkMoney == -1) {
                throw new SimpleException("余额不足[%s]", userInfo.getUserMoney());
            } else {
                userInfo.setFreezeMoney(userInfo.getFreezeMoney().add(money));
                userInfo.setUserMoney(userInfo.getUserMoney().subtract(money));
                userDao.updateById(userInfo);
            }
        } else {
            var userGood = userGoodDao.selectOne(new QueryWrapper<UserGood>().eq("UserId", userOrder.getUserId()));
            var checkCount = userGood.getNiuCoin().compareTo(userOrder.getTradeCount());
            if (checkCount == -1) {
                throw new SimpleException("余币不足[%s]", userGood.getNiuCoin());
            } else {
                userGood.setNiuCoin(userGood.getNiuCoin().subtract(userOrder.getTradeCount()));
                userGood.setFreezeNiuCoin(userGood.getFreezeNiuCoin().add(userOrder.getTradeCount()));
                userGoodDao.updateById(userGood);
            }
        }

        saveUserOrder.setTradeId(userOrder.getTradeId());
        userOrder.setSurplusCount(saveUserOrder.getTradeCount());
        userOrder.setSurplusAmount(saveUserOrder.getTradeCount().multiply(saveUserOrder.getTradePrice()));
        return userTradeOrderDao.insert(userOrder) > 0;
    }

    @Override
    public List<TradeOrders> getUserOrders() {
        var buyerList = userTradeOrderDao.selectBuyerList();
        var sellerList = userTradeOrderDao.selectSellerList();
        buyerList.addAll(sellerList);

        return buyerList;
    }

    @Override
    public IPage<TradeOrders> getUserTradeOrders(Integer userId) {
        var warpper = new QueryWrapper<TradeOrders>()
                .eq("UserId", userId)
                .eq("Cancel", 0)
                .isNull("FinishDate")
                .orderByDesc("Id");
        var page = new Page<>();
        page.setSize(100);
        return userTradeOrderDao.selectPage(new Page<>(), warpper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelOrder(String tradeId) {
        var tradeOrder = userTradeOrderDao.selectOne(new QueryWrapper<TradeOrders>().eq("TradeId", tradeId));
        if (tradeOrder.getBuyer() == 1) {
            //撤销订单，买单解冻购买金额（减去已成交的）
            var userInfo = userDao.selectById(tradeOrder.getUserId());
            userInfo.setFreezeMoney(userInfo.getFreezeMoney().subtract(tradeOrder.getSurplusAmount()));
            userInfo.setUserMoney(userInfo.getUserMoney().add(tradeOrder.getSurplusAmount()));
            userDao.updateById(userInfo);
        } else {
            //撤销订单，卖单解冻牛币（减去已成交的）
            var userGoodInfo = userGoodDao.selectOne(new QueryWrapper<UserGood>().eq("UserId", tradeOrder.getUserId()));
            userGoodInfo.setFreezeNiuCoin(userGoodInfo.getFreezeNiuCoin().subtract(tradeOrder.getSurplusCount()));
            userGoodInfo.setNiuCoin(userGoodInfo.getNiuCoin().add(tradeOrder.getSurplusCount()));
            userGoodDao.updateById(userGoodInfo);
        }

        var updateOrder = new TradeOrders();
        updateOrder.setCancel(1);
        updateOrder.setCancelTime(new Date());
        updateOrder.setSurplusCount(BigDecimal.ZERO);
        updateOrder.setSurplusAmount(BigDecimal.ZERO);
        userTradeOrderDao.update(updateOrder, new UpdateWrapper<TradeOrders>()
                .eq("TradeId", tradeId));
    }
}