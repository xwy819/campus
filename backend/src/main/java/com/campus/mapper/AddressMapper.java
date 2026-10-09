package com.campus.mapper;

import com.campus.entity.Address;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AddressMapper {
    List<Address> findByUserId(@Param("userId") Integer userId);

    Address findById(@Param("id") Integer id);

    int insert(Address address);

    int update(Address address);

    int delete(@Param("id") Integer id);

    int clearDefault(@Param("userId") Integer userId);

    int setDefault(@Param("id") Integer id);
}
