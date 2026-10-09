package com.campus.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Notice {
    private Integer id;
    private Integer adminId;
    private String title;
    private String content;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
