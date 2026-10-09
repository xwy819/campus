package com.campus.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OperationLog {
    private Integer id;
    private Integer userId;
    private String operation;
    private LocalDateTime createTime;
}
