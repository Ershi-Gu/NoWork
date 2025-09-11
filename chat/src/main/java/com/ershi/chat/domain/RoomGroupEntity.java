package com.ershi.chat.domain;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;

import java.lang.Long;
import java.time.LocalDateTime;
import java.lang.Object;
import java.lang.String;
import java.lang.Integer;

/**
 * 群聊房间表 实体类。
 *
 * @author mybatis-flex-helper automatic generation
 * @since 1.0
 */
@Table(value = "room_group")
public class RoomGroupEntity {

    /**
     * 群聊房间id
     */
    @Id(keyType = KeyType.Auto)
    private Long id;

    /**
     * 房间id
     */
    @Column(value = "room_id")
    private Long roomId;

    /**
     * 群名
     */
    @Column(value = "name")
    private String name;

    /**
     * 群头像
     */
    @Column(value = "avatar_url")
    private String avatarUrl;

    /**
     * 群扩展信息
     */
    @Column(value = "ext_json")
    private Object extJson;

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
