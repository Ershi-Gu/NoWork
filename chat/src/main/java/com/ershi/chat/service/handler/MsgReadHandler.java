package com.ershi.chat.service.handler;

import com.ershi.chat.domain.UserMsgInboxEntity;
import com.ershi.chat.domain.dto.ConversationUnreadCount;
import com.ershi.chat.domain.dto.UnReadMsgCountReq;
import com.ershi.chat.mapper.MessageMapper;
import com.ershi.chat.mapper.UserMsgInboxMapper;
import com.ershi.chat.service.cache.HotMsgReadCache;
import com.ershi.chat.service.cache.HotRoomCache;
import com.mybatisflex.core.query.QueryWrapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import static com.ershi.chat.domain.table.UserMsgInboxEntityTableDef.USER_MSG_INBOX_ENTITY;

/**
 * 用户会话已读未读处理器，用于提供侧边栏会话列表未读角标。<br>
 * 通过热点群聊游标+用户已读游标 和 用户收件箱聚合获取所有会话未读数
 *
 * @author Ershi-Gu.
 * @since 2025-09-26
 */
@Component
public class MsgReadHandler {

    @Resource
    private HotRoomCache hotRoomCache;

    @Resource
    private UserMsgInboxMapper userMsgInboxMapper;

    @Resource
    private MessageMapper messageMapper;

    @Resource
    private HotMsgReadCache hotMsgReadCache;

    public List<ConversationUnreadCount> getConversationUnreadMsgCount(List<Long> roomIds, Long uid) {
        // TODO 开发ing开发ing，当前开发到这
        // 非热点会话 -
        // 查收件箱，获取用户非热点会话信息
        List<UserMsgInboxEntity> userMsgInboxEntities =
                userMsgInboxMapper.selectListByQuery(QueryWrapper.create()
                        .where(USER_MSG_INBOX_ENTITY.UID.eq(uid))
                        .and(USER_MSG_INBOX_ENTITY.ROOM_ID.in(roomIds)));
        List<UnReadMsgCountReq> unReadMsgCountReqs = userMsgInboxEntities.stream().map(userMsgInboxEntity -> {
            return UnReadMsgCountReq.builder()
                    .roomId(userMsgInboxEntity.getRoomId())
                    .readMsgId(userMsgInboxEntity.getReadMsgId())
                    .lastMsgId(userMsgInboxEntity.getLastMsgId())
                    .build();
        }).toList();

        // 热点会话 -
        // 获取用户会话列表中，热点会话的最新消息id。由roomIds包括非热点会话id，因此该方法不是热点会话的会返回msgId为null。
        Map<Long, Long> hotConversationLastMsgIds = hotRoomCache.getHotConversationLastMsgIds(roomIds);
        // 剔除空值
        hotConversationLastMsgIds.entrySet().removeIf(entry -> entry.getValue() == null);

        // 获取用户热点会话已读消息id
        Map<Long, Long> userReadMsg = hotMsgReadCache.getUserReadMsg(uid);

        // 查询计算未读数
        return null;
    }

}
