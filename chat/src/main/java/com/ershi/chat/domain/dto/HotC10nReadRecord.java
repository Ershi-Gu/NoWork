package com.ershi.chat.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 热点会话用户已读消息游标记录
 *
 * @author Ershi-Gu.
 * @since 2025-09-28
 */
@Data
@AllArgsConstructor
public class HotC10nReadRecord {

    private Long roomId;

    private Long readMsgId;
}
