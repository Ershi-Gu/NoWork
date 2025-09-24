package com.ershi.chat.websocket.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ws好友申请消息推送
 *
 * @author Ershi
 * @since 2025-09-24
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WSApplyFriendResp {

    /**
     * 申请人uid
     */
    private Long uid;

    /**
     * 未读申请数
     */
    private Long unReadCount;
}
