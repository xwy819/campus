package com.campus.service;

import com.campus.common.AuthContext;
import com.campus.common.PageResult;
import com.campus.common.Result;
import com.campus.entity.Message;
import com.campus.entity.Order;
import com.campus.mapper.MessageMapper;
import com.campus.mapper.OrderMapper;
import com.campus.vo.ConversationVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 站内消息业务服务。
 */
@Service
public class MessageService {

    @Autowired
    private MessageMapper messageMapper;
    @Autowired
    private OrderMapper orderMapper;

    public PageResult<Message> myPage(int pageNum, int pageSize) {
        AuthContext ctx = AuthContext.get();
        int offset = (pageNum - 1) * pageSize;
        long total = messageMapper.count(ctx.getUserId());
        List<Message> records = messageMapper.findPage(ctx.getUserId(), offset, pageSize);
        return PageResult.of(total, records, pageNum, pageSize);
    }

    public Result<?> send(Integer orderId, String content, Integer receiverId) {
        if (content == null || content.trim().isEmpty()) {
            return Result.error("消息内容不能为空");
        }
        AuthContext ctx = AuthContext.get();
        // 接收方：优先用传入的 receiverId；否则从订单推导（买家↔卖家）
        if (receiverId == null && orderId != null) {
            Order order = orderMapper.findById(orderId);
            if (order == null) {
                return Result.error("订单不存在");
            }
            receiverId = order.getBuyerId().equals(ctx.getUserId())
                    ? order.getSellerId() : order.getBuyerId();
        }
        if (receiverId == null) {
            return Result.error("接收方不能为空");
        }

        Message message = new Message();
        message.setOrderId(orderId);
        message.setSenderId(ctx.getUserId());
        message.setReceiverId(receiverId);
        message.setContent(content);
        messageMapper.insert(message);
        return Result.success();
    }

    public Result<?> markRead(Integer id) {
        messageMapper.markRead(id);
        return Result.success();
    }

    /** 私信发送（无需关联订单，orderId 留空） */
    public Result<?> sendDirect(Integer receiverId, String content) {
        if (content == null || content.trim().isEmpty()) {
            return Result.error("消息内容不能为空");
        }
        AuthContext ctx = AuthContext.get();
        if (ctx == null || ctx.getUserId() == null) {
            return Result.error(401, "请先登录后再发送私信");
        }
        if (receiverId == null) {
            return Result.error("接收方不能为空");
        }
        // 后端防护：发送人不能等于接收人，避免给自己发私信
        if (receiverId.equals(ctx.getUserId())) {
            return Result.error("不允许给自己发送消息");
        }
        Message message = new Message();
        message.setSenderId(ctx.getUserId());
        message.setReceiverId(receiverId);
        message.setContent(content.trim());
        messageMapper.insert(message);
        return Result.success();
    }

    /** 未读消息数（供导航红点） */
    public long unreadCount() {
        AuthContext ctx = AuthContext.get();
        return messageMapper.countUnread(ctx.getUserId());
    }

    /** 私聊会话列表 */
    public List<ConversationVO> conversations() {
        AuthContext ctx = AuthContext.get();
        return messageMapper.findConversations(ctx.getUserId());
    }

    /** 与指定对方的会话消息 */
    public List<Message> conversationMessages(Integer peerId) {
        AuthContext ctx = AuthContext.get();
        return messageMapper.findConversationMessages(ctx.getUserId(), peerId);
    }

    /** 标记与指定对方的会话全部已读 */
    public Result<?> readAll(Integer peerId) {
        AuthContext ctx = AuthContext.get();
        messageMapper.markReadAll(ctx.getUserId(), peerId);
        return Result.success();
    }

    /** 清空与指定对方的全部消息，保留会话条目（插入一条系统占位消息） */
    public Result<?> clearConversation(Integer peerId) {
        AuthContext ctx = AuthContext.get();
        messageMapper.clearConversation(ctx.getUserId(), peerId);
        // 插入一条系统占位消息，保证会话条目仍存在于左侧列表
        Message placeholder = new Message();
        placeholder.setSenderId(ctx.getUserId());
        placeholder.setReceiverId(peerId);
        placeholder.setContent("[系统]消息记录已清空");
        placeholder.setIsRead(1);
        messageMapper.insert(placeholder);
        return Result.success();
    }

    /** 删除单条消息（站内消息用，校验当前用户是接收方） */
    public Result<?> delete(Integer id) {
        AuthContext ctx = AuthContext.get();
        messageMapper.deleteById(id, ctx.getUserId());
        return Result.success();
    }
}
