package com.laymat.core.engie.robot.entity;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author: laymat
 * @desc:
 * @date: 2020/5/20
 */
@Data
public class MakeTradeBean implements Serializable {
    BigDecimal price;
    BigDecimal qty;
    String side;
    String timestamp;
    String trade_id;
}
