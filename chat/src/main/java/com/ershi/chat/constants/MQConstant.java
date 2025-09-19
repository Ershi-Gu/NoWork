package com.ershi.chat.constants;


/**
 * Rocket MQ  常量
 *
 * @author Ershi
 * @since 2025-09-19
 */
public interface MQConstant {

    /**
     * 消息发送 topic
     */
    String MSG_TOPIC = "nowork_chat_msg";

    /**
     * 消息消费者 group
     */
    String MSG_CONSUMER_GROUP = "nowork_chat_msg_consumer_group";
}
