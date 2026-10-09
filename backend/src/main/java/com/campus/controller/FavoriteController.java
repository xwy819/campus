package com.campus.controller;

import com.campus.common.Result;
import com.campus.entity.Favorite;
import com.campus.service.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 商品收藏接口。
 */
@RestController
@RequestMapping("/favorite")
public class FavoriteController {

    @Autowired
    private FavoriteService favoriteService;

    /** 切换收藏状态（需登录） */
    @PostMapping("/toggle")
    public Result<?> toggle(@RequestBody Map<String, Integer> body) {
        return favoriteService.toggle(body.get("productId"));
    }

    /** 检查当前用户是否已收藏某商品（需登录） */
    @GetMapping("/check")
    public Result<Boolean> check(@RequestParam Integer productId) {
        return Result.success(favoriteService.isFavorited(productId));
    }

    /** 我的收藏列表（需登录） */
    @GetMapping("/my")
    public Result<List<Favorite>> my() {
        return Result.success(favoriteService.myFavorites());
    }
}
