package com.laymat.core.engie.trade.order;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class TradeResult {
    /**
     * 交易单号
     */
    private String transactionSN;
    /**
     * 买方
     */
    private Long buyerId;
    /**
     * 卖方
     */
    private Long sellerId;
    /**
     * 交易单价
     */
    private BigDecimal tradePrice;
    /**
     * 交易数量
     */
    private BigDecimal tradeCount;
    /**
     * 交易金额
     */
    private BigDecimal tradeAmount;
    /**
     * 交易时间
     */
    private Date tradeTime;
}
