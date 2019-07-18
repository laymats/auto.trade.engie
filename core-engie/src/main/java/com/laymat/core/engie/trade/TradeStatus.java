package com.laymat.core.engie.trade;

import lombok.Data;

import java.math.BigDecimal;

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
}