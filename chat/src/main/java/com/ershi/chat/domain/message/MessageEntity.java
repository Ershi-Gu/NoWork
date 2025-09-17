package com.ershi.chat.domain.message;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import com.mybatisflex.core.handler.Fastjson2TypeHandler;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.Long;
import java.time.LocalDateTime;
import java.lang.Integer;

/**
 * 消息表 实体类。
 *
 * @author mybatis-flex-helper automatic generation
 * @since 1.0
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(value = "message")
public class MessageEntity {

    /**
     * 消息id
     */
    @Id(keyType = KeyType.Auto)
    private Long id;

    /**
     * 房间id
     */
    @Column(value = "room_id")
    private Long roomId;

    /**
     * 发送者id
     */
    @Column(value = "sender_id")
    private Long senderId;

    /**
     * 消息类型
     */
    @Column(value = "type")
    private Integer type;

    /**
     * 消息内容-支持多类型消息
     */
    @Column(value = "extra", typeHandler = Fastjson2TypeHandler.class)
    private MessageExtra extra;

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
