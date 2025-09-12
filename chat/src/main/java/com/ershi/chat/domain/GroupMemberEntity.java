package com.ershi.chat.domain;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.Builder;
import lombok.Data;

import java.lang.Long;
import java.time.LocalDateTime;
import java.lang.Integer;

/**
 * 群成员表 实体类。
 *
 * @author mybatis-flex-helper automatic generation
 * @since 1.0
 */
@Data
@Builder
@Table(value = "group_member")
public class GroupMemberEntity {

    /**
     * 群成员id
     */
    @Id(keyType = KeyType.Auto)
    private Long id;

    /**
     * 群组id
     */
    @Column(value = "group_id")
    private Long groupId;

    /**
     * 用户id
     */
    @Column(value = "uid")
    private Long uid;

    /**
     * 在群聊中所属角色，0-普通成员，1-管理员，2-群主
     */
    @Column(value = "role")
    private Integer role;

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
