package com.ershi.chat.domain.dto;

import lombok.Getter;

/**
 * @author Ershi-Gu.
 * @since 2025-09-10
 */
@Getter
public class FriendApproveReq {

    /**
     * 申请id
     */
    private Long applyId;

    /**
     * 审批状态
     */
    private Integer status;
}
