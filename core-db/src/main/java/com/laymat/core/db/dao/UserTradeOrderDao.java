package com.laymat.core.db.dao;

import com.laymat.core.db.entity.UserTradeOrder;
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
public interface UserTradeOrderDao extends BaseMapper<UserTradeOrder>{
    @Select("select * from tm_UserTradeOrder where Buyer = 1 order by TradePrice desc")
    List<UserTradeOrder> selectBuyerList();

    @Select("select * from tm_UserTradeOrder where Buyer = 0 order by TradePrice asc")
    List<UserTradeOrder> selectSellerList();
}