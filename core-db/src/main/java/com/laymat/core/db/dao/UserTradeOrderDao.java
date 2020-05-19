package com.laymat.core.db.dao;

import com.laymat.core.db.entity.TradeOrders;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * (TmUsertradeorder)表数据库访问层
 *
 * @author laymat
 * @since 2019-07-19 16:49:23
 */
@Repository
public interface UserTradeOrderDao extends BaseMapper<TradeOrders>{
    @Select("select * from tm_trade_order where Buyer = 1 and finishDate is null and Cancel = 0 order by TradePrice desc")
    List<TradeOrders> selectBuyerList();

    @Select("select * from tm_trade_order where Buyer = 0 and finishDate is null and Cancel = 0 order by TradePrice asc")
    List<TradeOrders> selectSellerList();
}