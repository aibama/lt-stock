package com.lt.mapper;

import com.lt.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
//@CacheNamespace  开启二级缓存
public interface UserMapper {

    @Select("SELECT * FROM lt_user WHERE id=#{id}")
    User getUserById(int id);

    @Insert("INSERT INTO lt_user (username,password,phone)" +
            " VALUES (#{username},#{password},#{phone})")
    void insertUser(User user);
}
