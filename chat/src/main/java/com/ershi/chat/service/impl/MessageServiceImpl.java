package com.ershi.chat.service.impl;


import cn.hutool.extra.spring.SpringUtil;
import com.ershi.chat.constants.MQConstant;
import com.ershi.chat.domain.GroupMemberEntity;
import com.ershi.chat.domain.RoomEntity;
import com.ershi.chat.domain.RoomFriendEntity;
import com.ershi.chat.domain.RoomGroupEntity;
import com.ershi.chat.domain.enums.RoomFriendStatusEnum;
import com.ershi.chat.mapper.GroupMemberMapper;
import com.ershi.chat.mapper.RoomFriendMapper;
import com.ershi.chat.service.cache.RoomCache;
import com.ershi.chat.service.cache.RoomGroupCache;
import com.ershi.chat.service.handler.message.AbstractMsgHandler;
import com.ershi.chat.service.handler.message.MsgHandlerFactory;
import com.ershi.chat.websocket.domain.dto.ChatMsgReq;
import com.ershi.common.exception.BusinessErrorEnum;
import com.ershi.common.manager.MQProducer;
import com.ershi.common.utils.AssertUtil;
import com.ershi.transaction.annotation.SecureInvoke;
import com.mybatisflex.core.query.QueryWrapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import com.ershi.chat.service.IMessageService;
import com.ershi.chat.domain.message.MessageEntity;
import com.ershi.chat.mapper.MessageMapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import static com.ershi.chat.domain.table.GroupMemberEntityTableDef.GROUP_MEMBER_ENTITY;
import static com.ershi.chat.domain.table.RoomFriendEntityTableDef.ROOM_FRIEND_ENTITY;

/**
 * 消息表 服务层实现。
 *
 * @author mybatis-flex-helper automatic generation
 * @since 1.0
 */
@Service
public class MessageServiceImpl extends ServiceImpl<MessageMapper, MessageEntity> implements IMessageService {

    @Resource
    private RoomCache roomCache;

    @Resource
    private RoomGroupCache roomGroupCache;

    @Resource
    private RoomFriendMapper roomFriendMapper;

    @Resource
    private GroupMemberMapper groupMemberMapper;

    @Resource
    private MQProducer mqProducer;

    @Transactional
    @Override
    public void sendMultiTypeMessage(ChatMsgReq chatMsgReq) {
        // check发送权限
        check(chatMsgReq);

        // 获取多类型消息处理器，进行消息体转换
        AbstractMsgHandler<?> msgHandler = MsgHandlerFactory.getMsgHandlerNoNull(chatMsgReq.getMsgType());
        MessageEntity messageEntity = msgHandler.checkAndBuildEntity(chatMsgReq);

        // 异步持久化消息数据，该方法由本地事务表保证可靠性，并在内部进行异步调用
        SpringUtil.getBean(MessageServiceImpl.class).saveMessage(messageEntity);

        // 发送可靠消息到mq，该方法由本地事务表保证可靠性，并在内部进行异步调用
        mqProducer.sendSecureMsg(MQConstant.MSG_TOPIC, messageEntity, chatMsgReq.getClientMsgId());
    }

    /**
     * 检查目标用户是否有在目标房间聊天的权限
     *
     * @param chatMsgReq
     */
    private void check(ChatMsgReq chatMsgReq) {
        Long senderId = chatMsgReq.getSenderId();
        RoomEntity roomEntity = roomCache.get(chatMsgReq.getRoomId());
        AssertUtil.nonNull(roomEntity, BusinessErrorEnum.ROOM_NOT_EXIST_ERROR);

        // 单聊
        if (roomEntity.isRoomFriend()) {
            RoomFriendEntity roomFriend = roomFriendMapper.selectOneByQuery(QueryWrapper.create()
                    .where(ROOM_FRIEND_ENTITY.ROOM_ID.eq(chatMsgReq.getRoomId())));
            // 房间状态检查
            AssertUtil.equal(RoomFriendStatusEnum.NORMAL.getStatus(), roomFriend.getStatus(), BusinessErrorEnum.FRIEND_BLACK_ERROR);
            // 用户状态检查
            AssertUtil.isTrue(senderId.equals(roomFriend.getUid1()) ||
                    senderId.equals(roomFriend.getUid2()), BusinessErrorEnum.FRIEND_NOT_EXIST_ERROR);

        }
        // 群聊
        else {
            RoomGroupEntity roomGroup = roomGroupCache.get(chatMsgReq.getRoomId());
            GroupMemberEntity groupMember = groupMemberMapper.selectOneByQuery(QueryWrapper.create()
                    .where(GROUP_MEMBER_ENTITY.GROUP_ID.eq(roomGroup.getId())
                            .and(GROUP_MEMBER_ENTITY.UID.eq(senderId))));
            // 群成员状态检查
            AssertUtil.isNotEmpty(groupMember, BusinessErrorEnum.MEMBER_NOT_EXIST_ERROR);
        }
    }

    /**
     * 异步持久化消息数据，该方法由本地事务表保证可靠性，并在内部进行异步调用。（需要调用该方法者本身存在事务中）
     *
     * @param messageEntity
     * @return {@link Long } 持久化消息主键
     */
    @SecureInvoke(maxRetryTimes = 5, async = true)
    @Override
    public Long saveMessage(MessageEntity messageEntity) {
        boolean inTransaction = TransactionSynchronizationManager.isActualTransactionActive();
        this.save(messageEntity);
        return messageEntity.getId();
    }
}