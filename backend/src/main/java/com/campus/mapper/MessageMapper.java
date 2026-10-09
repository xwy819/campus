package com.campus.mapper;

import com.campus.entity.Message;
import com.campus.vo.ConversationVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MessageMapper {
    List<Message> findPage(@Param("userId") Integer userId,
                           @Param("offset") int offset,
                           @Param("pageSize") int pageSize);

    long count(@Param("userId") Integer userId);

    long countUnread(@Param("userId") Integer userId);

    int insert(Message message);

    int markRead(@Param("id") Integer id);

    /** 私聊会话列表（按对方用户分组，取每条会话最后一条消息） */
    List<ConversationVO> findConversations(@Param("userId") Integer userId);

    /** 当前用户与指定对方之间的全部消息（按时间升序） */
    List<Message> findConversationMessages(@Param("userId") Integer userId,
                                           @Param("peerId") Integer peerId);

    /** 将当前用户与指定对方之间收到的消息全部标记为已读 */
    int markReadAll(@Param("userId") Integer userId,
                    @Param("peerId") Integer peerId);

    /** 清空当前用户与指定对方之间的全部消息（双向删除） */
    int clearConversation(@Param("userId") Integer userId,
                          @Param("peerId") Integer peerId);

    /** 删除单条消息（只能删当前用户收到的消息） */
    int deleteById(@Param("id") Integer id,
                   @Param("userId") Integer userId);
}
