package com.ershi.chat.domain.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 回复消息时支持文本消息类型，该类为被回复消息内容体
 *
 *
 * @author Ershi-Gu.
 * @since 2025-09-18
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReplyTextMsgDTO {

    /**
     * 消息id
     */
    private Long id;
    /**
     * 用户uid
     */
    private Long uid;
    /**
     * 用户名称
     */
    private String userName;
    /**
     * 消息类型 0-正常文本 1-撤回消息
     */
    private Integer type;
    /**
     * 消息内容不同的消息类型，见父消息内容体
     */
    private java.lang.Object body;
    /**
     * 是否可消息跳转 0否 1是
     */
    private Integer canCallback;
    /**
     * 跳转间隔的消息条数
     */
    private Long gapCount;
}