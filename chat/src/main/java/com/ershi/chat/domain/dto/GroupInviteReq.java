package com.ershi.chat.domain.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 邀请好友入群请求
 *
 * @author Ershi_Gu
 * @since 2025/10/10
 */
@Data
public class GroupInviteReq {

    /**
     * 群聊房间id
     */
    @NotNull
    private Long roomId;

    /**
     * 被邀请的好友uid列表
     */
    @NotEmpty
    private List<Long> friendUidList;
}