package com.laymat.core.db.service;

import com.laymat.core.db.dto.UserLogin;
import com.laymat.core.db.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * (TmUser)表服务接口
 *
 * @author laymat
 * @since 2019-07-19 16:00:23
 */
public interface UserService {
    User userLogin(UserLogin userLogin);
    User getUserInfo(Integer userId);
}