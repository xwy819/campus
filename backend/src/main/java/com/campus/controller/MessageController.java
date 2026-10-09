package com.campus.controller;

import com.campus.common.PageResult;
import com.campus.common.Result;
import com.campus.entity.Message;
import com.campus.service.MessageService;
import com.campus.vo.ConversationVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 站内消息接口。
 */
@RestController
@RequestMapping("/message")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @GetMapping("/page")
    public Result<PageResult<Message>> page(@RequestParam(defaultValue = "1") int pageNum,
                                            @RequestParam(defaultValue = "10") int pageSize) {
        return Result.success(messageService.myPage(pageNum, pageSize));
    }

    @PostMapping("/send")
    public Result<?> send(@RequestBody Map<String, Object> body) {
        Object oid = body.get("orderId");
        Integer orderId = oid == null ? null : ((Number) oid).intValue();
        Object rid = body.get("receiverId");
        Integer receiverId = rid == null ? null : ((Number) rid).intValue();
        Object content = body.get("content");
        return messageService.send(orderId, content == null ? null : content.toString(), receiverId);
    }

    /** 私信发送（不依赖订单，用于商品详情页私信卖家） */
    @PostMapping("/sendDirect")
    public Result<?> sendDirect(@RequestBody Map<String, Object> body) {
        // Jackson 可能将 JSON 整数解析为 Integer 或 Long，统一用 Number.intValue() 安全取值
        Object rid = body.get("receiverId");
        Integer receiverId = rid == null ? null : ((Number) rid).intValue();
        Object content = body.get("content");
        return messageService.sendDirect(receiverId, content == null ? null : content.toString());
    }

    @PostMapping("/read")
    public Result<?> read(@RequestBody Map<String, Integer> body) {
        return messageService.markRead(body.get("id"));
    }

    /** 未读消息数量 */
    @GetMapping("/unread")
    public Result<Long> unread() {
        return Result.success(messageService.unreadCount());
    }

    /** 私聊会话列表 */
    @GetMapping("/conversations")
    public Result<List<ConversationVO>> conversations() {
        return Result.success(messageService.conversations());
    }

    /** 与指定对方的会话消息 */
    @GetMapping("/conversation/{peerId}")
    public Result<List<Message>> conversation(@PathVariable Integer peerId) {
        return Result.success(messageService.conversationMessages(peerId));
    }

    /** 标记与指定对方的会话全部已读 */
    @PostMapping("/readAll")
    public Result<?> readAll(@RequestBody Map<String, Integer> body) {
        return messageService.readAll(body.get("peerId"));
    }

    /** 清空与指定对方的全部聊天消息（保留会话条目） */
    @DeleteMapping("/conversation/{peerId}")
    public Result<?> clearConversation(@PathVariable Integer peerId) {
        return messageService.clearConversation(peerId);
    }

    /** 删除单条站内消息 */
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Integer id) {
        return messageService.delete(id);
    }
}
