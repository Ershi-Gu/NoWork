package com.ershi.chat.event.listener;

import com.ershi.chat.constants.SystemMsgConstant;
import com.ershi.chat.domain.RoomFriendEntity;
import com.ershi.chat.domain.enums.MessageTypeEnum;
import com.ershi.chat.domain.message.TextMsgDTO;
import com.ershi.chat.event.FriendAddedEvent;
import com.ershi.chat.service.IMessageService;
import com.ershi.chat.websocket.domain.dto.ChatMsgReq;
import jakarta.annotation.Resource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * 好友添加成功事件监听器
 *
 * @author Ershi-Gu.
 * @since 2025-09-24
 */
@Component
public class FriendAddedEventListener {

    @Resource
    private IMessageService messageService;

    @Async
    @TransactionalEventListener(classes = FriendAddedEvent.class, fallbackExecution = true)
    public void sendCreateFriendMsg(FriendAddedEvent event) {
        Long uid = event.getUid();
        RoomFriendEntity roomFriendEntity = event.getRoomFriendEntity();

        // 发送一条打招呼信息给好友
        ChatMsgReq chatMsgReq = ChatMsgReq.builder()
                .clientMsgId(SystemMsgConstant.CLIENT_MSG_ID)
                .roomId(roomFriendEntity.getRoomId())
                .senderId(uid)
                .msgType(MessageTypeEnum.TEXT.getType())
                .messageBody(TextMsgDTO.builder().content("我们已经成为好友了，开始聊天吧。").build())
                .build();

        messageService.sendMultiTypeMessage(chatMsgReq);
    }
}
