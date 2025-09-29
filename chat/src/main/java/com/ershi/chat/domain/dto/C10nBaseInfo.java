package com.ershi.chat.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


/**
 * 会话基本信息
 *
 * @author Ershi
 * @since 2025-09-28
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class C10nBaseInfo {

    /**
     * 房间id
     */
    private Long roomId;

    /**
     * 会话名称
     */
    private String name;

    /**
     * 会话头像url
     */
    private String avatarUrl;

    /**
     * 会话类型，0-全员群，1-群聊，2-单聊
     */
    private Integer type;

    /**
     * 热点标识，0-非热点，1-热点
     */
    private Integer hotFlag;

    /**
     * 群最后活跃时间
     */
    private LocalDateTime activeTime;

    /**
     * 群最后一条消息id
     */
    private Long lastMsgId;
}
