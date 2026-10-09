package com.campus.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class Product {
    private Integer id;
    private Integer userId;
    private Integer categoryId;
    private String title;
    private String description;
    private BigDecimal price;
    /** 新旧程度 1-4 */
    private Integer conditionLevel;
    private String imageUrl;
    /** 多图数组（JSON 存储，首图自动同步到 imageUrl） */
    private String imagesJson;
    /** 前端友好：图片 URL 列表（DB 不存此字段，靠 Service 层转换） */
    private List<String> images;
    /** 0 待审，1 在售，2 已售，3 下架 */
    private Integer status;
    /** 浏览量（数据库列 view_count） */
    private Integer viewCount;
    /** 交易方式位掩码：1 当面交易，2 校内送货上门，3 两者都支持 */
    private Integer deliveryType;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    // 联查展示字段（数据库无此列，靠 SQL 别名注入）
    private String categoryName;
    private String sellerNickname;
    private String sellerPhone;
    private String sellerAvatar;

    // 非持久化字段：用于详情页前端展示
    /** 当前登录用户是否为该商品卖家 */
    private Boolean isSelf;
    /** 当前登录用户是否已收藏 */
    private Boolean favorited;
}
