package com.laymat.core.engie.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.laymat.core.db.dto.SaveUserOrder;
import com.laymat.core.db.entity.UserTradeOrder;
import com.laymat.core.db.service.UserTradeOrderService;
import com.laymat.core.db.utils.result.BaseRestfulResult;
import com.laymat.core.db.utils.result.impl.SimpleResult;
import com.laymat.core.engie.controller.base.BaseController;
import com.laymat.core.engie.trade.TradeEngieService;
import com.laymat.core.engie.trade.order.TradeOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * @author dell
 */
@RestController
@RequestMapping("/trade")
public class TradeController extends BaseController {
    @Autowired
    UserTradeOrderService userTradeOrderService;

    @PostMapping("/new")
    public BaseRestfulResult<Boolean> newTradeOrder(@Valid @RequestBody SaveUserOrder saveUserOrder) {
        saveUserOrder.setUserId(this.getSession().getUserId());
        if (userTradeOrderService.placeOrder(saveUserOrder)) {
            var order = new TradeOrder();
            order.setTradeId(saveUserOrder.getTradeId());
            order.setBuyer(saveUserOrder.getBuyer() == 1);
            order.setMarketOrder(saveUserOrder.getMarketOrder() == 1);
            order.setCancel(saveUserOrder.getCancel() == 1);
            order.setTradePrice(saveUserOrder.getTradePrice());
            order.setTradeCount(saveUserOrder.getTradeCount());
            order.setUserId(this.getSession().getUserId());

            var placeResult = tradeEngieService.placeOrder(order);
            return SimpleResult.retMessageFromBoolean(placeResult);
        } else {
            return SimpleResult.retMessageFromBoolean(false);
        }
    }

    @PostMapping("/user_order")
    public BaseRestfulResult<IPage<UserTradeOrder>> userTradeOrder() {
        var placeResult = userTradeOrderService.getUserTradeOrders(this.getSession().getUserId());
        return SimpleResult.retMessageFromData(placeResult);
    }

    @PostMapping("/cancel/{tradeId}")
    public BaseRestfulResult<Boolean> cancelTradeOrder(@PathVariable String tradeId) {
        var order = new TradeOrder();
        order.setTradeId(tradeId);
        order.setCancel(true);
        var placeResult = tradeEngieService.cancelOrder(order);
        return SimpleResult.retMessageFromBoolean(placeResult);
    }
}
