package com.laymat.core.db.service.impl;

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

}