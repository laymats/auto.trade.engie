package com.laymat.core.engie.controller;


import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import com.laymat.core.engie.trade.TradeEngieService;
import com.laymat.core.engie.trade.order.TradeOrder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * @author dell
 */
@RestController
@RequestMapping("/trade")
public class TradeController {
    private TradeEngieService tradeEngieService = TradeEngieService.getService();

    @GetMapping("/new")
    public boolean getUserOrderToPages(BigDecimal price, BigDecimal count, Boolean buy, Long userId) {
        var order = new TradeOrder();
        order.setBuyer(buy);
        order.setTradeAmount(price);
        order.setTradeCount(count);
        order.setTradeId(IdUtil.fastUUID());
        order.setUserId(userId);

        return tradeEngieService.placeOrder(order);
    }

    @GetMapping("/order_list")
    public Map getOrderList() {
        var hashMap = new HashMap<>();
        hashMap.put("buyer", tradeEngieService.getBuyers());
        hashMap.put("seller", tradeEngieService.getSellers());
        hashMap.put("trades", tradeEngieService.getTradeResults());
        hashMap.put("status",tradeEngieService.getTradeStatus());
        return hashMap;
    }
}
