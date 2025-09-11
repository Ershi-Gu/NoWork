package com.ershi.chat.domain;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 单聊房间表 实体类。
 *
 * @author mybatis-flex-helper automatic generation
 * @since 1.0
 */
@Data
@Table(value = "room_friend")
public class RoomFriendEntity {

    /**
     * 单聊房间id
     */
    @Id(keyType = KeyType.Auto)
    private Long id;

    /**
     * 房间id
     */
    @Column(value = "room_id")
    private Long roomId;

    /**
     * 用户1id
     */
    @Column(value = "uid1")
    private Long uid1;

    /**
     * 用户2id
     */
    @Column(value = "uid2")
    private Long uid2;

    /**
     * 房间key，由两个uid拼接，uid1_uid2（uid1小）
     */
    @Column(value = "room_key")
    private String roomKey;

    /**
     * 0-正常，1-禁用
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

    /**
     * 创建单聊房间实体类
     *
     * @param roomId
     * @param uid
     * @param fid
     * @return {@link RoomFriendEntity }
     */
    public static RoomFriendEntity build(Long roomId, String RFkey, Long uid, Long fid) {
        RoomFriendEntity roomFriendEntity = new RoomFriendEntity();
        roomFriendEntity.setRoomId(roomId);
        roomFriendEntity.setRoomKey(RFkey);
        roomFriendEntity.setUid1(Math.min(uid, fid));
        roomFriendEntity.setUid2(Math.max(uid, fid));
        return roomFriendEntity;
    }
}
