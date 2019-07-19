package com.laymat.core.engie.controller;

import com.laymat.core.db.dto.SaveUserOrder;
import com.laymat.core.db.entity.User;
import com.laymat.core.db.entity.UserTradeOrder;
import com.laymat.core.db.service.UserService;
import com.laymat.core.db.service.UserTradeOrderService;
import com.laymat.core.db.utils.result.BaseRestfulResult;
import com.laymat.core.db.utils.result.impl.SimpleResult;
import com.laymat.core.engie.trade.TradeEngieService;
import com.laymat.core.engie.trade.order.TradeOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    UserService userService;

    @GetMapping("/account")
    public BaseRestfulResult<User> getUserOrderToPages(@RequestBody SaveUserOrder saveUserOrder) {
        return SimpleResult.retMessageFromData(null);
    }

}
