package com.ershi.chat.domain.vo;

import lombok.Builder;
import lombok.Data;

/**
 * 好友返回
 *
 * @author Ershi-Gu.
 * @since 2025-09-11
 */
@Data
@Builder
public class FriendResp {

    /**
     * 用户id
     */
    private Long uid;

    /**
     * 在线状态，0-离线，1-在线
     */
    private Integer activeStatus;
}
