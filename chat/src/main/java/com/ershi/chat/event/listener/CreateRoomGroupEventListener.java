package com.ershi.chat.event.listener;

import com.ershi.chat.constants.SystemMsgConstant;
import com.ershi.chat.domain.GroupMemberEntity;
import com.ershi.chat.domain.RoomGroupEntity;
import com.ershi.chat.domain.enums.MessageTypeEnum;
import com.ershi.chat.domain.message.TextMsgDTO;
import com.ershi.chat.event.CreateRoomGroupEvent;
import com.ershi.chat.service.IMessageService;
import com.ershi.chat.websocket.domain.dto.ChatMsgReq;
import com.ershi.user.domain.entity.UserEntity;
import com.ershi.user.service.cache.UserInfoCache;
import jakarta.annotation.Resource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 群聊创建事件监听器
 *
 * @author Ershi-Gu.
 * @since 2025-09-24
 */
@Component
public class CreateRoomGroupEventListener {

    @Resource
    private UserInfoCache userInfoCache;

    @Resource
    private IMessageService messageService;

    @Async
    @TransactionalEventListener(classes = CreateRoomGroupEvent.class, fallbackExecution = true)
    public void sendCreateRoomGroupMsg(CreateRoomGroupEvent event) {
        RoomGroupEntity roomGroupEntity = event.getRoomGroupEntity();
        List<GroupMemberEntity> groupMemberList = event.getGroupMemberList();
        Long inviteUid = event.getInviteUid();

        // 构建一条系统消息通知已入群
        UserEntity inviteUser = userInfoCache.get(inviteUid);
        List<Long> receiverUids = groupMemberList.stream().map(GroupMemberEntity::getUid).toList();

        // 消息设置为空内容，只作会话提醒作用
        ChatMsgReq chatMsgReq = ChatMsgReq.builder()
                .clientMsgId(SystemMsgConstant.CLIENT_MSG_ID)
                .roomId(roomGroupEntity.getRoomId())
                .senderId(SystemMsgConstant.SENDER_ID)
                .msgType(MessageTypeEnum.SYSTEM.getType())
                .messageBody(TextMsgDTO.builder().content("").build())
                .build();

        messageService.sendMultiTypeMessage(chatMsgReq);
    }
}
