package com.laymat.core.db.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class SaveUserOrder implements Serializable {
    /**
     * 用户id
     */
    @JsonIgnore
    private Integer UserId;
    @JsonIgnore
    private String TradeId;
    /**
     * 交易时间
     */
    private BigDecimal TradePrice;
    /**
     * 交易数量
     */
    private BigDecimal TradeCount;
    /**
     * 总交易金额
     */
    private BigDecimal TradeAmount;
    /**
     * 是否买单
     */
    private Integer Buyer;
    /**
     * 是否市价单
     */
    @NotNull(message = "[MarketOrder]不能为空")
    private Integer MarketOrder;
    /**
     * 是否取消
     */
    private Integer Cancel;
}
