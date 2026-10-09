package com.campus.controller;

import com.campus.common.Result;
import com.campus.entity.Category;
import com.campus.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 商品分类接口。
 */
@RestController
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/list")
    public Result<List<Category>> list() {
        return Result.success(categoryService.findAll());
    }

    @PostMapping("/admin")
    public Result<?> add(@RequestBody Category category) {
        return categoryService.add(category);
    }

    @PutMapping("/admin")
    public Result<?> update(@RequestBody Category category) {
        return categoryService.update(category);
    }

    @DeleteMapping("/admin/{id}")
    public Result<?> delete(@PathVariable Integer id) {
        return categoryService.delete(id);
    }
}
