package com.campus.service;

import com.campus.common.AuthContext;
import com.campus.common.PageResult;
import com.campus.common.Result;
import com.campus.entity.Product;
import com.campus.mapper.FavoriteMapper;
import com.campus.mapper.OperationLogMapper;
import com.campus.entity.OperationLog;
import com.campus.mapper.ProductMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.util.*;

/**
 * 商品业务服务：分页查询、发布、批量审核、上下架、删除。
 */
@Service
public class ProductService {

    @Autowired
    private ProductMapper productMapper;
    @Autowired private ObjectMapper om;
    @Autowired
    private OperationLogMapper operationLogMapper;
    @Autowired
    private FavoriteMapper favoriteMapper;

    /** 前台公开分页查询（仅显示在售商品） */
    public PageResult<Product> publicPage(String keyword, Integer categoryId,
                                          Double minPrice, Double maxPrice,
                                          Integer conditionLevel, int pageNum, int pageSize) {
        int offset = (pageNum - 1) * pageSize;
        long total = productMapper.count(keyword, categoryId, minPrice, maxPrice, conditionLevel, 1);
        List<Product> records = productMapper.findPage(keyword, categoryId, minPrice, maxPrice, conditionLevel, 1, offset, pageSize);
        return PageResult.of(total, records, pageNum, pageSize);
    }

    /** 后台分页查询（可按状态筛选） */
    public PageResult<Product> adminPage(String keyword, Integer categoryId, Integer status,
                                         int pageNum, int pageSize) {
        int offset = (pageNum - 1) * pageSize;
        long total = productMapper.count(keyword, categoryId, null, null, null, status);
        List<Product> records = productMapper.findPage(keyword, categoryId, null, null, null, status, offset, pageSize);
        return PageResult.of(total, records, pageNum, pageSize);
    }

    private void fillImages(Product p) { if (p == null) return; try { String j = p.getImagesJson(); p.setImages(j == null || j.trim().isEmpty() ? new ArrayList<>() : om.readValue(j, new TypeReference<List<String>>(){})); } catch (Exception e) { p.setImages(new ArrayList<>()); } if ((p.getImages() == null || p.getImages().isEmpty()) && p.getImageUrl() != null) { p.setImages(new ArrayList<>(java.util.Collections.singletonList(p.getImageUrl()))); } }
    public Product detail(Integer id) {
        Product product = productMapper.findById(id);
        if (product != null) {
            // 增加浏览量
            productMapper.incrementViewCount(id);
            // 设置当前登录用户是否为该商品卖家、是否已收藏
            AuthContext ctx = AuthContext.get();
            boolean isSelf = ctx != null && ctx.getUserId() != null
                    && ctx.getUserId().equals(product.getUserId());
            product.setIsSelf(isSelf);
            boolean fav = ctx != null && ctx.getUserId() != null
                    && favoriteMapper.findByUserAndProduct(ctx.getUserId(), id) != null;
            product.setFavorited(fav);
          fillImages(product);
        }
        return product;
    }

    /** 相似商品推荐（同分类在售，排除当前商品） */
    public List<Product> similar(Integer categoryId, Integer excludeId, int limit) {
        if (limit <= 0 || limit > 20) {
            limit = 8;
        }
        return productMapper.findSimilar(categoryId, excludeId, limit);
    }

    /**
     * 获取当前登录用户对某商品的标识位（isSelf、favorited）。
     * 由于 /product/detail 为公开接口（不走 AuthInterceptor），
     * 单独提供此鉴权接口供前端在登录态下补充查询。
     */
    public java.util.Map<String, Object> myFlags(Integer productId) {
        AuthContext ctx = AuthContext.get();
        java.util.Map<String, Object> flags = new java.util.HashMap<>();
        if (ctx == null || ctx.getUserId() == null) {
            flags.put("isSelf", false);
            flags.put("favorited", false);
            return flags;
        }
        Product p = productMapper.findById(productId);
        boolean isSelf = p != null && ctx.getUserId().equals(p.getUserId());
        boolean fav = favoriteMapper.findByUserAndProduct(ctx.getUserId(), productId) != null;
        flags.put("isSelf", isSelf);
        flags.put("favorited", fav);
        return flags;
    }

    /** 用户发布商品 */
    private void syncImagesForWrite(Product p) { try { if (p.getImages() != null && !p.getImages().isEmpty()) { p.setImagesJson(om.writeValueAsString(p.getImages())); p.setImageUrl(p.getImages().get(0)); } else if (p.getImageUrl() != null && !p.getImageUrl().trim().isEmpty()) { p.setImagesJson(om.writeValueAsString(new ArrayList<>(java.util.Collections.singletonList(p.getImageUrl())))); } } catch (Exception ignored) {} }
    public Result<?> publish(Product product) {
        AuthContext ctx = AuthContext.get();
        if (ctx == null) {
            return Result.error(401, "未登录");
        }
        if (product.getTitle() == null || product.getTitle().trim().isEmpty()) {
            return Result.error("商品标题不能为空");
        }
        if (product.getPrice() == null || product.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            return Result.error("商品价格必须大于 0");
        }
        if (product.getCategoryId() == null) {
            return Result.error("请选择商品分类");
        }
        // 同一用户不能发布相同标题的商品
        Product exist = productMapper.findByTitleAndUserId(product.getTitle().trim(), ctx.getUserId());
        if (exist != null) {
            return Result.error("您已发布过同名商品");
        }
        product.setUserId(ctx.getUserId());
        product.setStatus(0); // 待审核
        syncImagesForWrite(product);
          productMapper.insert(product);
        return Result.success();
    }

    /** 批量审核通过（存在待审状态则改为在售） */
    @Transactional
    public Result<?> batchApprove(List<Integer> ids) {
        return batchChangeStatus(ids, 0, 1, "批量审核通过");
    }

    /** 批量下架（在售 -> 下架） */
    @Transactional
    public Result<?> batchOffShelf(List<Integer> ids) {
        return batchChangeStatus(ids, 1, 3, "批量下架");
    }

    private Result<?> batchChangeStatus(List<Integer> ids, int fromStatus, int toStatus, String logAction) {
        if (ids == null || ids.isEmpty()) {
            return Result.error("请选择商品");
        }
        int ok = 0;
        for (Integer id : ids) {
            Product p = productMapper.findById(id);
            if (p != null && p.getStatus() == fromStatus) {
                productMapper.updateStatus(id, toStatus);
                ok++;
            }
        }
        operationLogMapper.insert(newLog(logAction + "，共 " + ok + " 件"));
        return Result.success("成功处理 " + ok + " 件商品");
    }

    /** 删除商品（管理员） */
    public Result<?> delete(Integer id) {
        productMapper.deleteById(id);
        operationLogMapper.insert(newLog("删除商品 id=" + id));
        return Result.success();
    }

    /** 用户查看自己发布的商品 */
    private void fillImagesBatch(List<Product> list) { if (list == null) return; list.forEach(this::fillImages); }
    public List<Product> myProducts() {
        AuthContext ctx = AuthContext.get();
        List<Product> list = productMapper.findByUserId(ctx.getUserId()); fillImagesBatch(list); return list;
    }

    /** 用户编辑自己的商品（重置为待审核） */
    public Result<?> update(Product product) {
        AuthContext ctx = AuthContext.get();
        Product exist = productMapper.findById(product.getId());
        if (exist == null || !exist.getUserId().equals(ctx.getUserId())) {
            return Result.error(403, "无权操作该商品");
        }
        if (product.getTitle() == null || product.getTitle().trim().isEmpty()) {
            return Result.error("商品标题不能为空");
        }
        if (product.getPrice() == null || product.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            return Result.error("商品价格必须大于 0");
        }
        product.setUserId(ctx.getUserId());
        product.setStatus(0); // 编辑后重新审核
        syncImagesForWrite(product);
          productMapper.update(product);
        operationLogMapper.insert(newLog("编辑商品 id=" + product.getId()));
        return Result.success();
    }

    /** 用户下架自己的商品（在售 -> 下架） */
    public Result<?> offShelf(Integer id) {
        AuthContext ctx = AuthContext.get();
        Product exist = productMapper.findById(id);
        if (exist == null || !exist.getUserId().equals(ctx.getUserId())) {
            return Result.error(403, "无权操作该商品");
        }
        productMapper.updateStatus(id, 3);
        operationLogMapper.insert(newLog("下架商品 id=" + id));
        return Result.success();
    }

    /** 用户删除自己的商品 */
    public Result<?> deleteMine(Integer id) {
        AuthContext ctx = AuthContext.get();
        Product exist = productMapper.findById(id);
        if (exist == null || !exist.getUserId().equals(ctx.getUserId())) {
            return Result.error(403, "无权操作该商品");
        }
        productMapper.deleteById(id);
        operationLogMapper.insert(newLog("删除商品 id=" + id));
        return Result.success();
    }

    private OperationLog newLog(String operation) {
        OperationLog log = new OperationLog();
        AuthContext ctx = AuthContext.get();
        log.setUserId(ctx == null ? null : ctx.getUserId());
        log.setOperation(operation);
        return log;
    }
}
