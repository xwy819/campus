package com.campus.mapper;

import com.campus.entity.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserMapper {
    User findByPhone(@Param("phone") String phone);

    User findById(@Param("id") Integer id);

    List<User> findAll(@Param("role") Integer role, @Param("keyword") String keyword);

    int insert(User user);

    int update(User user);

    int updatePassword(@Param("id") Integer id, @Param("password") String password);

    int updateStatus(@Param("id") Integer id, @Param("status") Integer status);

    int deleteById(@Param("id") Integer id);

    long count();
}
