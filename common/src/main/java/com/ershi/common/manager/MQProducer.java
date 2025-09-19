package com.ershi.common.manager;

import com.ershi.transaction.annotation.SecureInvoke;
import jakarta.annotation.Resource;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

/**
 * MQ 生产者发送消息工具
 * @author Ershi
 * @date 2025/02/05
 */
@Component
public class MQProducer {

    @Resource
    private RocketMQTemplate rocketMQTemplate;

    /**
     * 发送消息到mq
     * @param topic 消息topic
     * @param body 消息体
     */
    public void sendMsg(String topic, Object body) {
        Message<Object> build = MessageBuilder.withPayload(body).build();
        rocketMQTemplate.send(topic, build);
    }

    /**
     * 发送可靠消息，在事务提交后保证发送成功，通过本地事务表保证可靠。
     *
     * @param topic
     * @param body
     */
    @SecureInvoke()
    public void sendSecureMsg(String topic, Object body, Object key) {
        Message<Object> build = MessageBuilder
                .withPayload(body)
                .setHeader("KEYS", key)
                .build();
        rocketMQTemplate.send(topic, build);
    }
}
