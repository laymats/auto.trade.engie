package com.laymat.core.engie.controller;


import com.laymat.core.db.dto.SaveUserOrder;
import com.laymat.core.db.entity.UserTradeOrder;
import com.laymat.core.db.service.UserTradeOrderService;
import com.laymat.core.db.utils.result.BaseRestfulResult;
import com.laymat.core.db.utils.result.impl.SimpleResult;
import com.laymat.core.engie.trade.TradeEngieService;
import com.laymat.core.engie.trade.order.TradeOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author dell
 */
@RestController
@RequestMapping("/trade")
public class TradeController {
    @Autowired
    UserTradeOrderService userTradeOrderService;

    private TradeEngieService tradeEngieService = TradeEngieService.getService();

    @PostMapping("/new")
    public BaseRestfulResult<Boolean> getUserOrderToPages(@RequestBody SaveUserOrder saveUserOrder) {
        if (userTradeOrderService.placeOrder(saveUserOrder)) {
            var userTradeOrder = new UserTradeOrder();

            var order = new TradeOrder();
            order.setTradeId(saveUserOrder.getTradeId());
            order.setBuyer(saveUserOrder.getBuyer() == 1);
            order.setTradePrice(saveUserOrder.getTradePrice());
            order.setTradeCount(saveUserOrder.getTradeCount());
            order.setUserId(saveUserOrder.getUserId());

            var placeResult = tradeEngieService.placeOrder(order);
            return SimpleResult.retMessageFromBoolean(placeResult);
        } else {
            return SimpleResult.retMessageFromBoolean(false);
        }
    }
}
