package com.campus.mapper;

import com.campus.entity.Category;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CategoryMapper {
    List<Category> findAll();

    int insert(Category category);

    int update(Category category);

    int deleteById(@Param("id") Integer id);
}
