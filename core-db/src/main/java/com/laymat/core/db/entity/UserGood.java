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
 * (TmUsergood)实体类
 *
 * @author laymat
 * @since 2019-07-19 16:11:19
 */
@Data
@TableName("TM_UserGood")
public class UserGood implements Serializable {

    private static final long serialVersionUID = -99135334772458348L;
        
    @TableId(type = IdType.AUTO,value = "id")
    private Integer id;
        
    @TableField("UserId")
    private Integer UserId;
        
    @TableField("NiuCoin")
    private BigDecimal NiuCoin;
}