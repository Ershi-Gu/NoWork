package com.ershi.chat.websocket.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ws连接额外信息
 *
 * @author Ershi
 * @since 2025-09-09
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WSChannelExtraDTO {

    /**
     * 前端如果登录了，记录当前用户uid关联到channel
     */
    private Long uid;
}
