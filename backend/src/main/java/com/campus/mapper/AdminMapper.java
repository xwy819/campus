package com.campus.mapper;

import com.campus.entity.Admin;
import org.apache.ibatis.annotations.Param;

public interface AdminMapper {
    Admin findByUsername(@Param("username") String username);
}
