package com.campus.service;

import com.campus.common.Result;
import com.campus.entity.Category;
import com.campus.mapper.CategoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 商品分类业务服务。
 */
@Service
public class CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    public List<Category> findAll() {
        return categoryMapper.findAll();
    }

    public Result<?> add(Category category) {
        if (category.getName() == null || category.getName().trim().isEmpty()) {
            return Result.error("分类名称不能为空");
        }
        categoryMapper.insert(category);
        return Result.success();
    }

    public Result<?> update(Category category) {
        categoryMapper.update(category);
        return Result.success();
    }

    public Result<?> delete(Integer id) {
        categoryMapper.deleteById(id);
        return Result.success();
    }
}
