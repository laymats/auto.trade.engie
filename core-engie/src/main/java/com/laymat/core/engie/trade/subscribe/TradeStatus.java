package com.laymat.core.engie.trade.subscribe;

import com.laymat.core.db.dto.GetUserAccount;
import com.laymat.core.engie.trade.order.TradeOrder;
import com.laymat.core.engie.trade.order.TradeResult;
import lombok.Data;

import java.math.BigDecimal;
import java.util.LinkedList;

/**
 * Redwood Engie
 *
 * @author: Laymat
 * @date: 2019/7/17 0017
 * @time: 21:55
 * @description: TradeStatus
 */
@Data
public class TradeStatus {
    private BigDecimal highPrice;
    private BigDecimal lowPrice;
    private LinkedList<TradeOrder> buyer;
    private LinkedList<TradeOrder> seller;
    private TradeResult[] trades;
    private GetUserAccount account;
}