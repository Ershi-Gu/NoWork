package com.ershi.chat.domain;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户申请表 实体类。
 *
 * @author mybatis-flex-helper automatic generation
 * @since 1.0
 */
@Data
@Builder
@Table(value = "user_apply")
public class UserApplyEntity {

    /**
     * 申请id
     */
    @Id(keyType = KeyType.Auto)
    private Long id;

    /**
     * 用户id
     */
    @Column(value = "uid")
    private Long uid;

    /**
     * 目标用户id/目标群聊id
     */
    @Column(value = "target_id")
    private Long targetId;

    /**
     * 申请类型 1加好友 2加群聊
     */
    @Column(value = "type")
    private Integer type;

    /**
     * 申请信息
     */
    @Column(value = "msg")
    private String msg;

    /**
     * 申请状态，0-待审批，1-已同意，2-已拒绝
     */
    @Column(value = "status")
    private Integer status;

    /**
     * 0-已读，1-未读
     */
    @Column(value = "read_status")
    private Integer readStatus;

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
