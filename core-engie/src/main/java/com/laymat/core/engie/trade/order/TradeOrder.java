package com.laymat.core.engie.trade.order;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 核心交易薄信息
 */
@Data
public class TradeOrder implements Serializable {
    /**
     * 下单号
     */
    private String tradeId;
    /**
     * 下单用户
     */
    private Integer userId;
    /**
     * 交易单价
     */
    private BigDecimal tradePrice;
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
     * 交易时间
     */
    private Date finishDate;
    /**
     * 是否市价单
     */
    private boolean marketOrder;
    /**
     * 订单总额
     */
    private BigDecimal totalAmount;
    /**
     * 是否撤销操作
     */
    private boolean cancel;
}
