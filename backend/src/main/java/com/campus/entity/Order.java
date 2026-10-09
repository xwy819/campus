package com.campus.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Order {
    private Integer id;
    private Integer productId;
    private Integer buyerId;
    private Integer sellerId;
    /** 0 待确认，1 已确认，2 完成，3 取消 */
    private Integer status;
    private BigDecimal finalPrice;
    /** 交易方式：1 当面交易，2 校内送货上门 */
    private Integer deliveryType;
    /** 收货地址ID（送货上门时必填） */
    private Integer addressId;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    // 联查展示字段
    private String productTitle;
    private String productImageUrl;
    private String buyerNickname;
    private String sellerNickname;
}
