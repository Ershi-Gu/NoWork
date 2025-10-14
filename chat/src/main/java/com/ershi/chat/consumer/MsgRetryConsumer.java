package com.ershi.chat.consumer;

import com.ershi.chat.constants.MQConstant;
import com.ershi.chat.domain.dto.RetryMessage;
import com.ershi.chat.domain.message.MessageEntity;
import com.ershi.chat.domain.vo.ChatMessageResp;
import com.ershi.chat.service.adapter.MsgAdapter;
import com.ershi.chat.websocket.domain.enums.WSRespTypeEnum;
import com.ershi.chat.websocket.domain.vo.WSBaseResp;
import com.ershi.chat.websocket.service.ChatWebSocketService;
import jakarta.annotation.Resource;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 用于接收未ack消息重试的mq消息
 *
 * @author Ershi-Gu.
 * @since 2025-09-25
 */
@RocketMQMessageListener(consumerGroup = MQConstant.MSG_RETRY_CONSUMER_GROUP, topic = MQConstant.MSG_RETRY_TOPIC)
@Service
public class MsgRetryConsumer implements RocketMQListener<RetryMessage> {

    @Resource
    private ChatWebSocketService chatWebSocketService;

    @Override
    public void onMessage(RetryMessage retryMessage) {
        MessageEntity messageEntity = retryMessage.getMessageEntity();
        List<Long> receiverUids = retryMessage.getReceiverUids();

        // 转换消息格式
        ChatMessageResp chatMessageResp = MsgAdapter.buildChatMsgResp(messageEntity);
        WSBaseResp<ChatMessageResp> wsResp = WSBaseResp.build(WSRespTypeEnum.NEW_MESSAGE.getType(), chatMessageResp);

        // 过滤不在线用户
        List<Long> onlineUids = chatWebSocketService.getOnlineUids();
        receiverUids = receiverUids.stream()
                .filter(onlineUids::contains)
                .toList();

        chatWebSocketService.sendMsgToUser(receiverUids, wsResp);
    }
}
