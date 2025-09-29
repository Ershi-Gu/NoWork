package com.ershi.chat.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 热点会话最新消息
 *
 * @author Ershi-Gu.
 * @since 2025-09-28
 */
@Data
@AllArgsConstructor
public class HotC10nLastMsg {

    private Long roomId;

    private Long lastMsgId;
}
