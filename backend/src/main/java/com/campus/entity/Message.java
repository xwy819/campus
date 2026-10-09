package com.campus.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Message {
    private Integer id;
    private Integer orderId;
    private Integer senderId;
    private Integer receiverId;
    private String content;
    /** 0 未读，1 已读 */
    private Integer isRead;
    private LocalDateTime createTime;
}
