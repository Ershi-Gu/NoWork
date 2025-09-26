package com.ershi.chat.domain.dto;

import lombok.Data;

/**
 * 会话未读数记录
 *
 * @author Ershi-Gu.
 * @since 2025-09-26
 */
@Data
public class ConversationUnreadCount {

    private Long roomId;

    private Integer unreadCount;
}
