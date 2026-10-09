package com.campus.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Address {
    private Integer id;
    private Integer userId;
    /** 地址标签，如：宿舍、实验室 */
    private String label;
    /** 楼栋 */
    private String building;
    /** 宿舍号 */
    private String dorm;
    /** 是否默认地址 0否 1是 */
    private Integer isDefault;
    private LocalDateTime createTime;
}
