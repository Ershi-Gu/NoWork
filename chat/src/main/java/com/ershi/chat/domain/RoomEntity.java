package com.ershi.chat.domain;

import com.ershi.chat.domain.enums.RoomHotFlagEnum;
import com.ershi.chat.domain.enums.RoomTypeEnum;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.Data;

import java.lang.Long;
import java.time.LocalDateTime;
import java.lang.Integer;

/**
 * 房间表 实体类。
 *
 * @author mybatis-flex-helper automatic generation
 * @since 1.0
 */
@Data
@Table(value = "room")
public class RoomEntity {

    /**
     * 房间id
     */
    @Id(keyType = KeyType.Auto)
    private Long id;

    /**
     * 房间类型，0-全员群，1-单聊，2-群聊
     */
    @Column(value = "type")
    private Integer type;

    /**
     * 是否是热点群聊，0-非热点，1-热点
     */
    @Column(value = "hot_flag")
    private Integer hotFlag;

    /**
     * 会话最后活跃时间
     */
    @Column(value = "active_time")
    private LocalDateTime activeTime;

    /**
     * 最后一条消息id
     */
    @Column(value = "last_msg_id")
    private Long lastMsgId;

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
     * 判断是否是单聊
     * @return boolean
     */
    @JsonIgnore
    public boolean isRoomFriend() {
        return RoomTypeEnum.of(this.type) == RoomTypeEnum.FRIEND;
    }

    /**
     * 判断是否是热点会话
     *
     * @return boolean
     */
    public boolean isHotRoom() {
        return RoomHotFlagEnum.of(this.hotFlag) == RoomHotFlagEnum.HOT;
    }

    /**
     * 判断是否是全员群
     *
     * @return boolean
     */
    public boolean isAllRoom() {
        return RoomTypeEnum.of(this.type) == RoomTypeEnum.ALL;
    }
}
