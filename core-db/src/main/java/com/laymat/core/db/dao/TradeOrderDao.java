package com.laymat.core.db.dao;

import com.laymat.core.db.entity.TradeOrder;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Repository;

/**
 * (TmTradeorder)表数据库访问层
 *
 * @author laymat
 * @since 2019-07-19 15:58:39
 */
@Repository
public interface TradeOrderDao extends BaseMapper<TradeOrder>{

}