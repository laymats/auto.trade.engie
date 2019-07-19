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
 * (TmTradeorder)实体类
 *
 * @author laymat
 * @since 2019-07-19 15:58:39
 */
@Data
@TableName("tm_TradeOrder")
public class TradeOrder implements Serializable {

    private static final long serialVersionUID = -24837414843898019L;
        
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
     * 交易时间
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
}