package com.laymat.core.engie.controller;

import com.laymat.core.db.dto.GetUserAccount;
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
@RequestMapping("/admin")
public class AdminController extends BaseController {
    @Autowired
    UserService userService;

    @GetMapping("/start_engie")
    public BaseRestfulResult<Boolean> startEngie() {
        var startResult = tradeEngieService.startEngie();
        return SimpleResult.retMessageFromBoolean(startResult);
    }

    @GetMapping("/stop_engie")
    public BaseRestfulResult<Boolean> stopEngie() {
        var stopResult = tradeEngieService.stopEngie();
        return SimpleResult.retMessageFromBoolean(stopResult);
    }
}
