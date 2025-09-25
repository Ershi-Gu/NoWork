package com.ershi.chat.domain.dto;

import com.ershi.chat.domain.message.MessageEntity;
import lombok.Data;

import java.util.List;

/**
 * 未ack消息重试
 *
 * @author Ershi-Gu.
 * @since 2025-09-25
 */
@Data
public class RetryMessage {

    private MessageEntity messageEntity;

    private List<Long> receiverUids;
}
