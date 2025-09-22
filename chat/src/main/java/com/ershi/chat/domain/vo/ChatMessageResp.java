package com.ershi.chat.domain.vo;

import com.ershi.chat.domain.message.BaseMsgDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;


/**
 * 服务端聊天消息返回体
 *
 * @author Ershi
 * @since 2025-09-23
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageResp {

    /**
     * 发送者uid-前端懒加载查询用户信息
     */
    private Long senderUid;

    /**
     * 服务点返回消息详情，包括消息内容
     */
    private MessageInfo messageInfo;


    @Data
    public static class MessageInfo {

        /**
         * 消息id
         */
        private Long id;

        /**
         * 会话(room)id
         */
        private Long roomId;

        /**
         * 发送时间
         */
        private LocalDateTime sendTime;

        /**
         * 1-正常消息，2-撤回消息
         */
        private Integer type;

        /**
         * 多类型消息体
         */
        private BaseMsgDTO body;
    }
}
