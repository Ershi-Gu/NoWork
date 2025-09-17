package com.ershi.chat.domain.message.vo;

import com.ershi.chat.domain.message.BaseMsgDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 文本消息特殊回复体
 *
 * @author Ershi
 * @date 2025/02/08
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TextMsgResp implements BaseMsgDTO {
    /**
     * 消息内容
     */
    private String content;

    /**
     * 艾特的uid
     */
    private List<Long> atUidList;

    /**
     * 回复的消息，如果没有回复的消息，返回的是null
     */
    private ReplyMsg reply;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ReplyMsg {

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
        private Object body;
        /**
         * 是否可消息跳转 0否 1是
         */
        private Integer canCallback;
        /**
         * 跳转间隔的消息条数
         */
        private Long gapCount;
    }
}
