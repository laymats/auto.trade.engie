package com.laymat.core.db.entity;

import java.math.BigDecimal;
import java.util.Date;
import java.io.Serializable;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;


/**
 * (TmUser)实体类
 *
 * @author laymat
 * @since 2019-07-19 15:58:39
 */
@Data
@TableName("TM_User")
public class User implements Serializable {

    private static final long serialVersionUID = -84648751153964382L;
        
    @TableId(type = IdType.AUTO,value = "UserId")
    private Integer UserId;
        
    @TableField("UserName")
    private String UserName;
        
    @TableField("UserPass")
    private String UserPass;
        
    @TableField("UserMoney")
    private BigDecimal UserMoney;

    /**
     * 冻结金额
     */
    @TableField("FreezeMoney")
    private BigDecimal FreezeMoney;
}