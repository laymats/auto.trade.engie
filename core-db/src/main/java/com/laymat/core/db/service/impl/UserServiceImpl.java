package com.laymat.core.db.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.laymat.core.db.dto.UserLogin;
import com.laymat.core.db.entity.User;
import com.laymat.core.db.dao.UserDao;
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

    @Override
    public User userLogin(UserLogin userLogin) {
        return userDao.selectOne(new QueryWrapper<User>()
                .eq("UserName", userLogin.getUname())
                .eq("UserPass", userLogin.getUpass()));
    }

    @Override
    public User getUserInfo(Integer userId) {
        return userDao.selectById(userId);
    }
}