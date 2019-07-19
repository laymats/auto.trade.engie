package com.laymat.core.db.dao;

import com.laymat.core.db.entity.UserTradeOrder;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Repository;

/**
 * (TmUsertradeorder)表数据库访问层
 *
 * @author laymat
 * @since 2019-07-19 16:49:23
 */
@Repository
public interface UserTradeOrderDao extends BaseMapper<UserTradeOrder>{

}