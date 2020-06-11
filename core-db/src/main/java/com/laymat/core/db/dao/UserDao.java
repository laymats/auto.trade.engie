package com.laymat.core.db.dao;

import com.laymat.core.db.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

/**
 * (TmUser)表数据库访问层
 *
 * @author laymat
 * @since 2019-07-19 15:58:39
 */
@Repository
public interface UserDao extends BaseMapper<User>{
    @Select("SELECT * FROM tm_user WHERE UserId = #{UserId} FOR UPDATE")
    User selectUserAccountForUpdate(@Param("UserId") Integer userId);
}