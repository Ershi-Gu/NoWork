package com.ershi.chat.domain.vo;

import lombok.Builder;
import lombok.Data;

/**
 * 群组创建返回信息
 *
 * @author Ershi-Gu.
 * @since 2025-09-12
 */
@Data
@Builder
public class GroupCreateResp {

    /**
     * 群聊房间id
     */
    private Long id;

    /**
     * 房间id
     */
    private Long roomId;

    /**
     * 群名
     */
    private String name;

    /**
     * 群头像
     */
    private String avatarUrl;


}
