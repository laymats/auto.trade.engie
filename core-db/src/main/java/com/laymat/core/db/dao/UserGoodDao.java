package com.laymat.core.db.dao;

import com.laymat.core.db.entity.User;
import com.laymat.core.db.entity.UserGood;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

/**
 * (TmUsergood)表数据库访问层
 *
 * @author laymat
 * @since 2019-07-19 16:11:19
 */
@Repository
public interface UserGoodDao extends BaseMapper<UserGood>{

    @Select("SELECT * FROM tm_user_good WHERE UserId = #{UserId} FOR UPDATE")
    UserGood selectUserCoinAccountForUpdate(@Param("UserId") Integer userId);
}