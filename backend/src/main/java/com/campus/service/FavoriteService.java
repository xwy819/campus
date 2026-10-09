package com.campus.service;

import com.campus.common.AuthContext;
import com.campus.common.Result;
import com.campus.entity.Favorite;
import com.campus.entity.Product;
import com.campus.mapper.FavoriteMapper;
import com.campus.mapper.ProductMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 商品收藏业务服务。
 */
@Service
public class FavoriteService {

    @Autowired
    private FavoriteMapper favoriteMapper;
    @Autowired
    private ProductMapper productMapper;

    /** 切换收藏状态：未收藏则收藏，已收藏则取消 */
    public Result<?> toggle(Integer productId) {
        AuthContext ctx = AuthContext.get();
        Favorite exist = favoriteMapper.findByUserAndProduct(ctx.getUserId(), productId);
        if (exist == null) {
            Favorite f = new Favorite();
            f.setUserId(ctx.getUserId());
            f.setProductId(productId);
            favoriteMapper.insert(f);
            return Result.success("已收藏");
        } else {
            favoriteMapper.delete(ctx.getUserId(), productId);
            return Result.success("已取消收藏");
        }
    }

    /** 检查当前用户是否已收藏某商品 */
    public boolean isFavorited(Integer productId) {
        AuthContext ctx = AuthContext.get();
        if (ctx == null || ctx.getUserId() == null) {
            return false;
        }
        return favoriteMapper.findByUserAndProduct(ctx.getUserId(), productId) != null;
    }

    /** 我的收藏列表 */
    public List<Favorite> myFavorites() {
        AuthContext ctx = AuthContext.get();
        return favoriteMapper.findByUserId(ctx.getUserId());
    }
}
