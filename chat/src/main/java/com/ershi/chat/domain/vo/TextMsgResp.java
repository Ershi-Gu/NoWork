package com.ershi.chat.domain.vo;

import com.ershi.chat.domain.message.BaseMsgDTO;
import com.ershi.chat.domain.message.ReplyTextMsgDTO;
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
    private ReplyTextMsgDTO reply;
}
