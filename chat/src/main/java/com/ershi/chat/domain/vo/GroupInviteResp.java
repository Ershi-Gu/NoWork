package com.ershi.chat.domain.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 群聊邀请信息返回
 *
 * @author Ershi_Gu
 * @since 2025/10/10
 */
@Data
public class GroupInviteResp {

    /**
     * 邀请ID
     */
    private Long id;

    /**
     * 群聊房间ID
     */
    private Long roomId;

    /**
     * 群聊名称
     */
    private String groupName;

    /**
     * 群聊头像
     */
    private String groupAvatar;

    /**
     * 邀请人ID
     */
    private Long inviterId;

    /**
     * 状态：0-待确认，1-已接受，2-已拒绝
     */
    private Integer status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}