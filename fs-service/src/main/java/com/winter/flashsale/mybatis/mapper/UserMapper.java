package com.winter.flashsale.mybatis.mapper;

import com.winter.common.entity.FlashSaleUser;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.type.JdbcType;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface UserMapper {

    @Insert("INSERT INTO fs_user(id, name, password, salt, avatar, register_date, last_login_date)" +
            "VALUES(#{id, nickname, password, salt, avatar, registerDate, lastLoginDate})"
    )
    void insertUser(FlashSaleUser user);

    @Select("SELECT * FROM fs_user")
    @Results(
            id = "flashsale_user",
            value = {
                    @Result(column = "name", property = "nickname", javaType = String.class, jdbcType = JdbcType.VARCHAR),
                    @Result(column = "password", property = "password", javaType = String.class, jdbcType = JdbcType.VARCHAR),
                    @Result(column = "salt", property = "salt", javaType = String.class, jdbcType = JdbcType.VARCHAR),
                    @Result(column = "avatar", property = "avatar", javaType = String.class, jdbcType = JdbcType.VARCHAR),
                    @Result(column = "register_date", property = "registerDate", javaType = Date.class, jdbcType = JdbcType.DATE),
                    @Result(column = "last_login_date", property = "lastLoginDate", javaType = Date.class, jdbcType = JdbcType.DATE),
                    @Result(column = "login_count", property = "loginCount", javaType = Integer.class, jdbcType = JdbcType.INTEGER)
            }
    )
    List<FlashSaleUser> getAllUsers();

    @Select("SELECT * FROM fs_user WHERE name=#{name}")
    @ResultMap("flashsale_user")
    FlashSaleUser retrieveUserByName(@Param("name") String name);

    @Select("SELECT * FROM fs_user")
    @MapKey("id")
    Map<Integer, FlashSaleUser> getFlashSaleMap();

    @Select("SELECT * FROM fs_user WHERE id=#{id}")
    FlashSaleUser retrieveUserById(int id);

    @Update("UPDATE fs_user SET password=#{password} WHERE id=#{id}")
    void updatePassword(FlashSaleUser user);

    @Select("SELECT count(*) FROM fs_user WHERE name=#{userName}")
    int countByUserName(@Param("userName") String userName);
}
