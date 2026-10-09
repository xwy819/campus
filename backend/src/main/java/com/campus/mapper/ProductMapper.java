package com.campus.mapper;

import com.campus.entity.Product;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ProductMapper {
    List<Product> findPage(@Param("keyword") String keyword,
                           @Param("categoryId") Integer categoryId,
                           @Param("minPrice") Double minPrice,
                           @Param("maxPrice") Double maxPrice,
                           @Param("conditionLevel") Integer conditionLevel,
                           @Param("status") Integer status,
                           @Param("offset") int offset,
                           @Param("pageSize") int pageSize);

    long count(@Param("keyword") String keyword,
               @Param("categoryId") Integer categoryId,
               @Param("minPrice") Double minPrice,
               @Param("maxPrice") Double maxPrice,
               @Param("conditionLevel") Integer conditionLevel,
               @Param("status") Integer status);

    Product findById(@Param("id") Integer id);

    List<Product> findByUserId(@Param("userId") Integer userId);

    Product findByTitleAndUserId(@Param("title") String title, @Param("userId") Integer userId);

    int insert(Product product);

    int update(Product product);

    int updateStatus(@Param("id") Integer id, @Param("status") Integer status);

    int deleteById(@Param("id") Integer id);

    long countByStatus(@Param("status") Integer status);

    /** 增加浏览量 */
    int incrementViewCount(@Param("id") Integer id);

    /** 查询同分类下的相似商品（排除当前商品，仅取在售） */
    List<Product> findSimilar(@Param("categoryId") Integer categoryId,
                              @Param("excludeId") Integer excludeId,
                              @Param("limit") int limit);
}
