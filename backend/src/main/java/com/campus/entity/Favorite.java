package com.campus.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户商品收藏关系。
 */
@Data
public class Favorite {
    private Integer id;
    private Integer userId;
    private Integer productId;
    private LocalDateTime createTime;

    // 联查展示字段（数据库无此列，靠 SQL 别名注入）
    private String productTitle;
    private String productImageUrl;
    private Double productPrice;
    private Integer productStatus;
    private String sellerNickname;
}
