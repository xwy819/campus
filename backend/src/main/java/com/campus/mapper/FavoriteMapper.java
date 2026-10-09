package com.campus.mapper;

import com.campus.entity.Favorite;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 商品收藏 Mapper。
 */
public interface FavoriteMapper {

    /** 查询用户是否已收藏某商品 */
    Favorite findByUserAndProduct(@Param("userId") Integer userId,
                                  @Param("productId") Integer productId);

    /** 新增收藏 */
    int insert(Favorite favorite);

    /** 删除收藏 */
    int delete(@Param("userId") Integer userId,
               @Param("productId") Integer productId);

    /** 我的收藏列表 */
    List<Favorite> findByUserId(@Param("userId") Integer userId);
}
