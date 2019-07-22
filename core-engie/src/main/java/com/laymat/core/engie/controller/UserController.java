package com.laymat.core.engie.controller;

import com.laymat.core.db.dto.UserLogin;
import com.laymat.core.db.entity.User;
import com.laymat.core.db.service.UserService;
import com.laymat.core.db.utils.result.BaseRestfulResult;
import com.laymat.core.db.utils.result.impl.SimpleResult;
import com.laymat.core.engie.config.interceptor.RedisSessionConfig;
import com.laymat.core.engie.controller.base.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController extends BaseController {
    @Autowired
    UserService userService;


    @PostMapping("/login")
    public BaseRestfulResult<User> login(@RequestBody UserLogin userLogin) {
        var user = userService.userLogin(userLogin);
        if (user != null) {
            new RedisSessionConfig(request).saveSession(user);
        }
        return SimpleResult.retMessageFromData(user);
    }

    @PostMapping("/account")
    public BaseRestfulResult<User> getUserOrderToPages() {
        return SimpleResult.retMessageFromData(userService.getUserInfo(this.getSession().getUserId()));
    }
}
