package com.campus.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 私聊会话视图对象。
 */
@Data
public class ConversationVO {
    /** 对方用户ID */
    private Integer peerId;
    /** 对方昵称 */
    private String peerNickname;
    /** 对方头像 */
    private String peerAvatar;
    /** 最后一条消息内容 */
    private String lastContent;
    /** 最后一条消息时间 */
    private LocalDateTime lastTime;
    /** 未读消息数 */
    private Long unreadCount;
}
