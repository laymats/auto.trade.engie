package com.laymat.core.db.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.laymat.core.db.dao.UserGoodDao;
import com.laymat.core.db.dto.GetUserAccount;
import com.laymat.core.db.dto.UserLogin;
import com.laymat.core.db.entity.User;
import com.laymat.core.db.dao.UserDao;
import com.laymat.core.db.entity.UserGood;
import com.laymat.core.db.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * (TmUser)表服务实现类
 *
 * @author laymat
 * @since 2019-07-19 16:00:23
 */
@Service("userService")
public class UserServiceImpl implements UserService {
    @Autowired
    private UserDao userDao;
    @Autowired
    private UserGoodDao userGoodDao;

    @Override
    public User userLogin(UserLogin userLogin) {
        return userDao.selectOne(new QueryWrapper<User>()
                .eq("UserName", userLogin.getUname())
                .eq("UserPass", userLogin.getUpass()));
    }

    @Override
    public GetUserAccount getUserInfo(Integer userId) {
        var userAccount = new GetUserAccount();
        userAccount.setUser(userDao.selectById(userId));
        userAccount.setUserGood(userGoodDao.selectOne(new QueryWrapper<UserGood>().eq("UserId", userId)));
        return userAccount;
    }
}