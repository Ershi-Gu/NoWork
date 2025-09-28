package com.ershi.chat.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 查询用户在指定会话消息未读数的请求
 *
 * @author Ershi-Gu.
 * @since 2025-09-26
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UnReadMsgCountReq {

    private Long roomId;

    private Long readMsgId;

    private Long lastMsgId;
}
