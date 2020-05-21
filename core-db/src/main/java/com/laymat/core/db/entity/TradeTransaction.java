package com.laymat.core.db.entity;

import java.util.Date;
import java.math.BigDecimal;
import java.util.Date;
import java.io.Serializable;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;


/**
 * (TmTradetransaction)实体类
 *
 * @author laymat
 * @since 2019-07-19 15:58:39
 */
@Data
@TableName("tm_trade_transaction")
public class TradeTransaction implements Serializable {

    private static final long serialVersionUID = -60653277281661227L;
        
    @TableId(type = IdType.AUTO,value = "id")
    private Integer id;
    /**
     * 交易单号
     */    
    @TableField("TransactionSN")
    private String TransactionSN;
        
    @TableField("SellerId")
    private Integer SellerId;
        
    @TableField("BuyerId")
    private Integer BuyerId;
        
    @TableField("TradePrice")
    private BigDecimal TradePrice;
        
    @TableField("TradeCount")
    private BigDecimal TradeCount;
        
    @TableField("TradeAmount")
    private BigDecimal TradeAmount;

    @TableField("IsBuyer")
    private Boolean isBuyer;
        
    @TableField("TradeTime")
    private Date TradeTime;
}