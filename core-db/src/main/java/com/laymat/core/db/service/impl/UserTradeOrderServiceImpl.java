package com.laymat.core.db.service.impl;

import com.laymat.core.db.dao.UserDao;
import com.laymat.core.db.dto.SaveUserOrder;
import com.laymat.core.db.entity.UserTradeOrder;
import com.laymat.core.db.dao.UserTradeOrderDao;
import com.laymat.core.db.service.UserTradeOrderService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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
    private UserDao userDao;

    @Override
    public boolean placeOrder(SaveUserOrder userOrder) {
        var userInfo = userDao.selectById(userOrder.getUserId());

        return false;
    }
}