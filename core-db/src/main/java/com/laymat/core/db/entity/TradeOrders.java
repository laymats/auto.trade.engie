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
 * (TmUsertradeorder)实体类
 *
 * @author laymat
 * @since 2019-07-19 16:49:23
 */
@Data
@TableName("tm_trade_order")
public class TradeOrders implements Serializable {

    private static final long serialVersionUID = 710467493094263557L;
        
    @TableId(type = IdType.AUTO,value = "Id")
    private Integer Id;
    /**
     * 订单id
     */    
    @TableField("TradeId")
    private String TradeId;
    /**
     * 用户id
     */    
    @TableField("UserId")
    private Integer UserId;
    /**
     * 交易价格
     */    
    @TableField("TradePrice")
    private BigDecimal TradePrice;
    /**
     * 交易数量
     */    
    @TableField("TradeCount")
    private BigDecimal TradeCount;
    /**
     * 总交易金额
     */    
    @TableField("TradeAmount")
    private BigDecimal TradeAmount;
    /**
     * 剩余数量
     */    
    @TableField("SurplusCount")
    private BigDecimal SurplusCount;
    /**
     * 剩余金额
     */    
    @TableField("SurplusAmount")
    private BigDecimal SurplusAmount;
    /**
     * 是否买单
     */    
    @TableField("Buyer")
    private Integer Buyer;
    /**
     * 下单时间
     */    
    @TableField("TradeDate")
    private Date TradeDate;
    /**
     * 交易时间
     */    
    @TableField("FinishDate")
    private Date FinishDate;
    /**
     * 是否市价单
     */    
    @TableField("MarketOrder")
    private Integer MarketOrder;
    /**
     * 是否取消
     */    
    @TableField("Cancel")
    private Integer Cancel;
    /**
     * 撤销时间
     */
    @TableField("CancelTime")
    private Date CancelTime;
}