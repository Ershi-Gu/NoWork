package com.ershi.chat.domain.dto;

import lombok.Data;

/**
 * 好友申请请求
 *
 * @author Ershi_Gu
 * @since 2025/09/09
 */
@Data
public class FriendApplyReq {

    /**
     * 目标用户uid
     */
    private Long targetUid;

    /**
     * 目标用户账号-支持搜索账号添加好友
     */
    private String targetAccount;

    /**
     * 申请信息
     */
    private String applyMsg;
}
