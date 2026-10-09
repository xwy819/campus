package com.campus.controller;

import com.campus.common.PageResult;
import com.campus.common.Result;
import com.campus.entity.Product;
import com.campus.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 商品接口。
 */
@RestController
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    /** 前台公开分页（在售） */
    @GetMapping("/page")
    public Result<PageResult<Product>> page(@RequestParam(required = false) String keyword,
                                            @RequestParam(required = false) Integer categoryId,
                                            @RequestParam(required = false) Double minPrice,
                                            @RequestParam(required = false) Double maxPrice,
                                            @RequestParam(required = false) Integer conditionLevel,
                                            @RequestParam(defaultValue = "1") int pageNum,
                                            @RequestParam(defaultValue = "12") int pageSize) {
        return Result.success(productService.publicPage(keyword, categoryId, minPrice, maxPrice, conditionLevel, pageNum, pageSize));
    }

    @GetMapping("/detail")
    public Result<Product> detail(@RequestParam Integer id) {
        return Result.success(productService.detail(id));
    }

    /** 相似商品推荐（同分类在售，排除当前商品） */
    @GetMapping("/similar")
    public Result<List<Product>> similar(@RequestParam(required = false) Integer categoryId,
                                         @RequestParam(required = false) Integer excludeId,
                                         @RequestParam(defaultValue = "8") int limit) {
        return Result.success(productService.similar(categoryId, excludeId, limit));
    }

    /**
     * 当前登录用户对某商品的标识位（isSelf、favorited），需登录。
     * 用于详情页判断是否本人商品、是否已收藏。
     */
    @GetMapping("/myFlags")
    public Result<java.util.Map<String, Object>> myFlags(@RequestParam Integer productId) {
        return Result.success(productService.myFlags(productId));
    }

    /** 用户发布商品 */
    @PostMapping("/publish")
    public Result<?> publish(@RequestBody Product product) {
        return productService.publish(product);
    }

    /** 用户查看自己发布的商品 */
    @GetMapping("/my")
    public Result<List<Product>> my() {
        return Result.success(productService.myProducts());
    }

    /** 用户编辑自己的商品 */
    @PutMapping("/update")
    public Result<?> update(@RequestBody Product product) {
        return productService.update(product);
    }

    /** 用户下架自己的商品 */
    @PostMapping("/offShelf")
    public Result<?> offShelf(@RequestBody Map<String, Integer> body) {
        return productService.offShelf(body.get("id"));
    }

    /** 用户删除自己的商品 */
    @DeleteMapping("/{id}")
    public Result<?> deleteMine(@PathVariable Integer id) {
        return productService.deleteMine(id);
    }

    /** 管理员：后台分页（可按状态） */
    @GetMapping("/admin/page")
    public Result<PageResult<Product>> adminPage(@RequestParam(required = false) String keyword,
                                                 @RequestParam(required = false) Integer categoryId,
                                                 @RequestParam(required = false) Integer status,
                                                 @RequestParam(defaultValue = "1") int pageNum,
                                                 @RequestParam(defaultValue = "12") int pageSize) {
        return Result.success(productService.adminPage(keyword, categoryId, status, pageNum, pageSize));
    }

    /** 管理员：批量审核通过 */
    @PostMapping("/admin/batchApprove")
    public Result<?> batchApprove(@RequestBody Map<String, List<Integer>> body) {
        return productService.batchApprove(body.get("ids"));
    }

    /** 管理员：批量下架 */
    @PostMapping("/admin/batchOffShelf")
    public Result<?> batchOffShelf(@RequestBody Map<String, List<Integer>> body) {
        return productService.batchOffShelf(body.get("ids"));
    }

    /** 管理员：删除商品 */
    @DeleteMapping("/admin/{id}")
    public Result<?> delete(@PathVariable Integer id) {
        return productService.delete(id);
    }
}
