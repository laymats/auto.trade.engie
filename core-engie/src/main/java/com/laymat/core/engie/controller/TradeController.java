package com.laymat.core.engie.controller;


import com.laymat.core.db.dto.SaveUserOrder;
import com.laymat.core.db.entity.UserTradeOrder;
import com.laymat.core.db.service.UserTradeOrderService;
import com.laymat.core.db.utils.result.BaseRestfulResult;
import com.laymat.core.db.utils.result.impl.SimpleResult;
import com.laymat.core.engie.controller.base.BaseController;
import com.laymat.core.engie.trade.TradeEngieService;
import com.laymat.core.engie.trade.order.TradeOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * @author dell
 */
@RestController
@RequestMapping("/trade")
public class TradeController extends BaseController {
    @Autowired
    UserTradeOrderService userTradeOrderService;

    @PostMapping("/new")
    public BaseRestfulResult<Boolean> getUserOrderToPages(@Valid @RequestBody SaveUserOrder saveUserOrder) {
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
}
