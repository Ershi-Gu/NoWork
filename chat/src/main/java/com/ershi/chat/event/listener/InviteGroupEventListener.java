package com.ershi.chat.event.listener;

import com.ershi.chat.constants.SystemMsgConstant;
import com.ershi.chat.domain.GroupInviteEntity;
import com.ershi.chat.domain.RoomFriendEntity;
import com.ershi.chat.domain.RoomGroupEntity;
import com.ershi.chat.domain.enums.MessageTypeEnum;
import com.ershi.chat.domain.message.TextMsgDTO;
import com.ershi.chat.event.InviteGroupEvent;
import com.ershi.chat.service.IGroupInviteService;
import com.ershi.chat.service.IMessageService;
import com.ershi.chat.service.IRoomFriendService;
import com.ershi.chat.service.cache.RoomGroupCache;
import com.ershi.chat.websocket.domain.dto.ChatMsgReq;
import com.ershi.user.service.cache.UserInfoCache;
import jakarta.annotation.Resource;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 邀请入群事件监听器
 *
 * @author Ershi_Gu
 * @since 2025/10/10
 */
@Component
public class InviteGroupEventListener {

    @Resource
    private IMessageService messageService;

    @Resource
    private IRoomFriendService roomFriendService;

    @Resource
    private RoomGroupCache roomGroupCache;

    @Resource
    private UserInfoCache userInfoCache;

    @Resource
    private IGroupInviteService groupInviteService;

    /**
     * 异步保存群聊邀请记录
     *
     * @param event 邀请入群事件
     */
    @Async
    @EventListener(classes = InviteGroupEvent.class)
    @Transactional(rollbackFor = Exception.class)
    public void saveGroupInvites(InviteGroupEvent event) {
        List<GroupInviteEntity> inviteList = event.getInviteList();
        if (inviteList != null && !inviteList.isEmpty()) {
            groupInviteService.saveBatch(inviteList);
        }
    }

    /**
     * 发送邀请入群通知消息
     *
     * @param event 邀请入群事件
     */
    @Async
    @EventListener(classes = InviteGroupEvent.class)
    public void sendInviteNotification(InviteGroupEvent event) {
        List<GroupInviteEntity> inviteList = event.getInviteList();
        if (inviteList == null || inviteList.isEmpty()) {
            return;
        }

        // 遍历每个邀请记录，发送通知消息
        inviteList.forEach(groupInvite -> {
            Long inviterId = groupInvite.getInviterId();
            Long invitedId = groupInvite.getInvitedId();
            Long roomId = groupInvite.getRoomId();

            // 获取邀请人与被邀请人的单聊房间（好友关系已验证，房间一定存在）
            RoomFriendEntity roomFriendEntity = roomFriendService.getRoomFriend(inviterId, invitedId);

            // 从缓存获取群聊和邀请人信息
            RoomGroupEntity roomGroup = roomGroupCache.get(roomId);
            String inviterName = userInfoCache.get(inviterId).getName();

            // 构建邀请通知消息
            String notificationMsg = String.format("%s 邀请你加入群聊「%s」",
                    inviterName, roomGroup.getName());

            // 发送邀请通知消息到单聊房间
            ChatMsgReq chatMsgReq = ChatMsgReq.builder()
                    .clientMsgId(SystemMsgConstant.CLIENT_MSG_ID)
                    .roomId(roomFriendEntity.getRoomId())
                    .senderId(inviterId)
                    .msgType(MessageTypeEnum.TEXT.getType())
                    .messageBody(TextMsgDTO.builder().content(notificationMsg).build())
                    .build();

            messageService.sendMultiTypeMessage(chatMsgReq);
        });
    }
}