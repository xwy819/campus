package com.campus.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Category {
    private Integer id;
    private String name;
    private Integer sort;
    private LocalDateTime createTime;
}
