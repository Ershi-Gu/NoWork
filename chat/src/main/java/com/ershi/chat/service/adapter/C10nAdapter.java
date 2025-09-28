package com.ershi.chat.service.adapter;

import com.ershi.chat.domain.RoomEntity;
import com.ershi.chat.domain.RoomFriendEntity;
import com.ershi.chat.domain.RoomGroupEntity;
import com.ershi.chat.domain.dto.C10nBaseInfo;
import com.ershi.chat.domain.dto.C10nUnreadCount;
import com.ershi.chat.domain.enums.RoomTypeEnum;
import com.ershi.chat.domain.message.MessageEntity;
import com.ershi.chat.domain.vo.ConversationResp;
import com.ershi.chat.service.handler.message.AbstractMsgHandler;
import com.ershi.chat.service.handler.message.MsgHandlerFactory;
import com.ershi.user.domain.entity.UserEntity;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 会话列表 展示构造器
 *
 * @author Ershi-Gu.
 * @since 2025-09-28
 */
public class C10nAdapter {

    /**
     * 批量构造会话列表返回
     *
     * @param c10nBaseInfoMap
     * @param lastMsgs
     * @param senderInfoMap
     * @param c10nUnreadMsgCount
     * @return {@link List }<{@link ConversationResp }>
     */
    public static List<ConversationResp> batchBuildC10nResp(Map<Long, C10nBaseInfo> c10nBaseInfoMap,
                                                            List<MessageEntity> lastMsgs,
                                                            Map<Long, UserEntity> senderInfoMap,
                                                            List<C10nUnreadCount> c10nUnreadMsgCount) {
        // msgId -> message
        Map<Long, MessageEntity> messageInfoMap = lastMsgs.stream()
                .collect(Collectors.toMap(MessageEntity::getId, Function.identity()));

        return c10nBaseInfoMap.values().stream().map(c10nBaseInfo -> {
            // 组装基本信息
            ConversationResp resp = ConversationResp.builder()
                    .roomId(c10nBaseInfo.getRoomId())
                    .type(c10nBaseInfo.getType())
                    .hotFlag(c10nBaseInfo.getHotFlag())
                    .name(c10nBaseInfo.getName())
                    .avatarUrl(c10nBaseInfo.getAvatarUrl())
                    .build();

            // 获取会话最后消息内容
            MessageEntity message = messageInfoMap.get(c10nBaseInfo.getLastMsgId());
            if (Objects.nonNull(message)) {
                AbstractMsgHandler msgHandler = MsgHandlerFactory.getMsgHandlerNoNull(message.getType());
                resp.setLastMsgText(buildLastMsgSenderName(senderInfoMap, message) + msgHandler.showMsgOnContact(message));
                resp.setActiveTime(message.getCreateTime());
            }

            // 组装会话未读数
            resp.setUnreadCount(c10nUnreadMsgCount.stream()
                    .filter(unread -> unread.getRoomId().equals(c10nBaseInfo.getRoomId()))
                    .map(C10nUnreadCount::getUnreadCount)
                    .findFirst()
                    .orElse(0));

            return resp;
        }).collect(Collectors.toList());
    }

    /**
     * 构造会话列表消息发送人信息
     *
     * @param senderInfoMap
     * @param message
     * @return {@link String }
     */
    private static String buildLastMsgSenderName(Map<Long, UserEntity> senderInfoMap, MessageEntity message) {
        UserEntity senderInfo = senderInfoMap.get(message.getSenderId());
        if (senderInfo == null) {
            // 系统消息，不显示发送人
            return "";
        }

        return senderInfo.getName() + ":";
    }

    /**
     * 构造会话列表基本信息
     *
     * @param roomEntityMap
     * @param groupRoomInfoMap
     * @param singleRoomUserInfoMap
     * @return {@link Map }<{@link Long }, {@link C10nBaseInfo }> roomId -> C10nBaseInfo
     */
    public static Map<Long, C10nBaseInfo> buildC10nBaseInfo(Map<Long, RoomEntity> roomEntityMap,
                                                            Map<Long, RoomGroupEntity> groupRoomInfoMap,
                                                            Map<Long, UserEntity> singleRoomUserInfoMap) {
        return roomEntityMap.values().stream()
                .map(room -> {
                    C10nBaseInfo c10nBaseInfo = C10nBaseInfo.builder()
                            .roomId(room.getId())
                            .type(room.getType())
                            .hotFlag(room.getHotFlag())
                            .activeTime(room.getActiveTime())
                            .lastMsgId(room.getLastMsgId())
                            .build();

                    if (RoomTypeEnum.of(room.getType()) == RoomTypeEnum.GROUP) {
                        // 群聊
                        RoomGroupEntity roomGroup = groupRoomInfoMap.get(room.getId());
                        c10nBaseInfo.setName(roomGroup.getName());
                        c10nBaseInfo.setAvatarUrl(roomGroup.getAvatarUrl());
                    } else if (RoomTypeEnum.of(room.getType()) == RoomTypeEnum.FRIEND) {
                        // 单聊房间显示对方的信息
                        UserEntity user = singleRoomUserInfoMap.get(room.getId());
                        c10nBaseInfo.setName(user.getName());
                        c10nBaseInfo.setAvatarUrl(user.getAvatarUrl());
                    }

                    return c10nBaseInfo;
                }).collect(Collectors.toMap(C10nBaseInfo::getRoomId, Function.identity()));
    }

    /**
     * 批量从单聊房间获取还有id
     *
     * @param roomFriends
     * @param uid
     * @return {@link Set }<{@link Long }>
     */
    public static Set<Long> batchGetFriendUids(Collection<RoomFriendEntity> roomFriends, Long uid) {
        return roomFriends.stream()
                .map(roomFriend -> getFriendUid(roomFriend, uid))
                .collect(Collectors.toSet());
    }

    /**
     * 从单聊会话中获取好友uid
     */
    public static Long getFriendUid(RoomFriendEntity roomFriend, Long uid) {
        return Objects.equals(uid, roomFriend.getUid1()) ? roomFriend.getUid2() : roomFriend.getUid1();
    }
}
