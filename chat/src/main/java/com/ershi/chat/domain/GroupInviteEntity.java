package com.ershi.chat.domain;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.Data;

import java.lang.Long;
import java.time.LocalDateTime;
import java.lang.Integer;

/**
 * 群聊邀请表 实体类。
 *
 * @author mybatis-flex-helper automatic generation
 * @since 1.0
 */
@Data
@Table(value = "group_invite")
public class GroupInviteEntity {

    /**
     * 邀请ID
     */
    @Id(keyType = KeyType.Auto)
    private Long id;

    /**
     * 群聊会话ID
     */
    @Column(value = "room_id")
    private Long roomId;

    /**
     * 邀请人ID
     */
    @Column(value = "inviter_id")
    private Long inviterId;

    /**
     * 被邀请人ID
     */
    @Column(value = "invited_id")
    private Long invitedId;

    /**
     * 状态：0-待确认，1-已接受，2-已拒绝
     */
    @Column(value = "status")
    private Integer status;

    /**
     * 创建时间
     */
    @Column(value = "create_time")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @Column(value = "update_time")
    private LocalDateTime updateTime;

    /**
     * 是否删除：0-否，1-是
     */
    @Column(value = "is_delete")
    private Integer isDelete;
}
