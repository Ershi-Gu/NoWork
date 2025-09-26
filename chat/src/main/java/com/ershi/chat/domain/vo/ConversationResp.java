package com.ershi.chat.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;


/**
 * 会话列表返回
 *
 * @author Ershi
 * @date 2025/02/13
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ConversationResp implements Serializable {

    private static final long serialVersionUID = 5379841954399922782L;

    /**
     * 会话房间id
     */
    private Long roomId;

    /**
     * 房间类型，0-全员群，1-单聊，2-群聊
     */
    private Integer type;

    /**
     * 热点群聊标识，0-非热点群聊，1-热点群聊
     */
    private Integer hotFlag;

    /**
     * 会话名称
     */
    private String name;

    /**
     * 会话头像地址
     */
    private String avatarUrl;

    /**
     * 最新消息内容
     */
    private String lastMsgText;

    /**
     * 会话最后活跃时间（用于排序）
     */
    private LocalDateTime activeTime;

    /**
     * 消息未读数
     */
    private Integer unreadCount;
}
