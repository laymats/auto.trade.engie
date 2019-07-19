package com.laymat.core.engie.controller;

import com.laymat.core.db.entity.User;
import com.laymat.core.db.service.UserService;
import com.laymat.core.db.utils.result.BaseRestfulResult;
import com.laymat.core.db.utils.result.impl.SimpleResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    UserService userService;

    @GetMapping("/account/{id}")
    public BaseRestfulResult<User> getUserOrderToPages(@PathVariable Integer id) {
        return SimpleResult.retMessageFromData(userService.getUserInfo(id));
    }

}
