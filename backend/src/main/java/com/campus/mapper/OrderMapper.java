package com.campus.mapper;

import com.campus.entity.Order;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface OrderMapper {
    List<Order> findPage(@Param("userId") Integer userId,
                         @Param("buyerId") Integer buyerId,
                         @Param("sellerId") Integer sellerId,
                         @Param("status") Integer status,
                         @Param("offset") int offset,
                         @Param("pageSize") int pageSize);

    long count(@Param("userId") Integer userId,
               @Param("buyerId") Integer buyerId,
               @Param("sellerId") Integer sellerId,
               @Param("status") Integer status);

    Order findById(@Param("id") Integer id);

    int insert(Order order);

    int updateStatus(@Param("id") Integer id, @Param("status") Integer status);

    long countByStatus(@Param("status") Integer status);

    java.math.BigDecimal sumAmount(@Param("status") Integer status);
}
