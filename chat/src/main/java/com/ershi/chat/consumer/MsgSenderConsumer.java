package com.ershi.chat.consumer;

import com.ershi.chat.constants.MQConstant;
import com.ershi.chat.domain.RoomEntity;
import com.ershi.chat.domain.RoomFriendEntity;
import com.ershi.chat.domain.enums.RoomTypeEnum;
import com.ershi.chat.domain.message.MessageEntity;
import com.ershi.chat.domain.vo.ChatMessageResp;
import com.ershi.chat.mapper.RoomMapper;
import com.ershi.chat.mapper.UserMsgInboxMapper;
import com.ershi.chat.service.adapter.MsgAdapter;
import com.ershi.chat.service.cache.*;
import com.ershi.chat.websocket.domain.enums.WSRespTypeEnum;
import com.ershi.chat.websocket.domain.vo.WSBaseResp;
import com.ershi.chat.websocket.service.ChatWebSocketService;
import jakarta.annotation.Resource;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 聊天室消息MQ消费者，负责更新用户收件箱以及websocket推送
 *
 * @author Ershi-Gu.
 * @since 2025-09-22
 */
@RocketMQMessageListener(consumerGroup = MQConstant.MSG_CONSUMER_GROUP, topic = MQConstant.MSG_TOPIC)
@Service
public class MsgSenderConsumer implements RocketMQListener<MessageEntity> {

    @Resource
    private RoomCache roomCache;

    @Resource
    private RoomMapper roomMapper;

    @Resource
    private RoomFriendCache roomFriendCache;

    @Resource
    private GroupMemberCache groupMemberCache;

    @Resource
    private HotRoomCache hotRoomCache;

    @Resource
    private MsgAckCache msgAckCache;

    @Resource
    private MessageCache messageCache;

    @Resource
    private UserMsgInboxMapper userMsgInboxMapper;

    @Resource
    private ChatWebSocketService chatWebSocketService;

    @Override
    public void onMessage(MessageEntity messageEntity) {
        // 获取会话(room)信息
        RoomEntity roomEntity = roomCache.get(messageEntity.getRoomId());

        // 获取所有接收消息的用户uid
        List<Long> receiverUids = getReceiverUids(roomEntity, messageEntity);

        // 更新收件箱
        refreshReceiveBox(roomEntity, messageEntity, receiverUids);

        // 确定推送范围 -> 过滤不在线用户
        List<Long> onlineReceiverUids = confirmPushUids(receiverUids);

        // 推送前写入ack缓存，作投递表（redis）
        msgAckCache.addUnAckMsg(onlineReceiverUids, messageEntity.getId());

        // 存储消息简表（redis） -> 用于ack超时重试
        messageCache.addMsgToCache(messageEntity);

        // ws推送消息
        pushMessage(RoomTypeEnum.of(roomEntity.getType()), onlineReceiverUids, messageEntity);
    }

    /**
     * 确定推送范围，获取接收者列表，若为全员推送，则返回空数组
     *
     * @param roomEntity
     * @param messageEntity
     * @return {@link List }<{@link Long }>
     */
    private List<Long> getReceiverUids(RoomEntity roomEntity, MessageEntity messageEntity) {
        List<Long> receiverUids = new ArrayList<>();

        // 若是全员群则直接返回空列表
        if (roomEntity.isAllRoom()) {
            return receiverUids;
        }

        if (roomEntity.isRoomFriend()) {
            // 单聊
            RoomFriendEntity roomFriendEntity = roomFriendCache.getRoomFriendByRoomId(roomEntity.getId());
            Long senderId = messageEntity.getSenderId();
            receiverUids.add(roomFriendEntity.getUid1().equals(senderId)
                    ? roomFriendEntity.getUid2()
                    : roomFriendEntity.getUid1());
        } else {
            // 群聊
            receiverUids = groupMemberCache.getRoomMemberUidList(roomEntity.getId());
        }

        return receiverUids;
    }

    /**
     * 确定推送范围 -> 过滤不在线用户
     *
     * @param receiverUids
     * @return {@link List }<{@link Long }>
     */
    private List<Long> confirmPushUids(List<Long> receiverUids) {
        // 过滤不在线用户
        List<Long> onlineUids = chatWebSocketService.getOnlineUids();
        return receiverUids.stream()
                .filter(onlineUids::contains)
                .toList();
    }


    /**
     * 更新会话最后活跃时间和最后消息，同时更新用户收件箱。<br>
     * 若为热点群聊则只更新前者，后续通过热点消息聚合刷新用户会话列表。
     *
     * @param roomEntity
     * @param messageEntity
     * @param memberUidList
     */
    private void refreshReceiveBox(RoomEntity roomEntity,
                                   MessageEntity messageEntity, List<Long> memberUidList) {
        boolean isHot = roomEntity.isHotRoom();

        // 刷新会话最后活跃时间和最后消息记录
        roomEntity.setActiveTime(messageEntity.getCreateTime());
        roomEntity.setLastMsgId(messageEntity.getId());
        roomMapper.update(roomEntity);

        if (isHot) {
            // 热点会话-使用redis存储最新消息id，聚合时只需要从redis获取即可
            hotRoomCache.refreshLastMsgId(roomEntity.getId(), messageEntity.getId());
        } else {
            // 非热点会话-更新用户收件箱
            userMsgInboxMapper.refreshInBox(memberUidList, roomEntity.getId(),
                    messageEntity.getId(), messageEntity.getCreateTime());
        }
    }

    /**
     * ws推送消息
     *
     * @param memberUidList
     * @param messageEntity
     */
    private void pushMessage(RoomTypeEnum allFlag, List<Long> memberUidList, MessageEntity messageEntity) {
        // 转换消息格式
        ChatMessageResp chatMessageResp = MsgAdapter.buildChatMsgResp(messageEntity);
        WSBaseResp<ChatMessageResp> wsResp = WSBaseResp.build(WSRespTypeEnum.SEND_CHAT_MESSAGE.getType(), chatMessageResp);

        if (allFlag.equals(RoomTypeEnum.ALL)) {
            // 发送全体成员
            chatWebSocketService.sendMsgToAllUser(wsResp);
        } else {
            // 发送指定用户
            chatWebSocketService.sendMsgToUser(memberUidList, wsResp);
        }
    }
}
