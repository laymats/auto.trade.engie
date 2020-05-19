package com.laymat.core.db.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.laymat.core.db.dto.SaveUserOrder;
import com.laymat.core.db.entity.TradeOrders;

import java.util.List;

/**
 * (TmUsertradeorder)表服务接口
 *
 * @author laymat
 * @since 2019-07-19 16:49:23
 */
public interface UserTradeOrderService {
    /**
     * 统一下单
     * @return
     */
    boolean placeOrder(SaveUserOrder userOrder);

    /**
     * 获取未交易的订单
     * @return
     */
    List<TradeOrders> getUserOrders();

    IPage<TradeOrders> getUserTradeOrders(Integer userId);

    void cancelOrder(String tradeId);
}