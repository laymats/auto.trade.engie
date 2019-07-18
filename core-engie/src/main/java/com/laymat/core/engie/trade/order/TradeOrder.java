package com.laymat.core.engie.trade.order;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class TradeOrder implements Comparable {
    /**
     * 下单号
     */
    private String tradeId;
    /**
     * 下单用户
     */
    private Long userId;
    /**
     * 交易单价
     */
    private BigDecimal tradeAmount;
    /**
     * 交易数量
     */
    private BigDecimal tradeCount;
    /**
     * 是否买单
     */
    private boolean buyer;
    /**
     * 下单时间
     */
    private Date tradeDate;
    /**
     * 是否市价单
     */
    private boolean marketOrder;
    /**
     * 订单总额
     */
    private BigDecimal totalAmount;

    @Override
    public int compareTo(Object o) {
        if (o instanceof TradeOrder) {
            TradeOrder tradeOrder = (TradeOrder) o;
            return this.tradeAmount.compareTo(tradeOrder.getTradeAmount());
        }
        return 0;
    }
}
