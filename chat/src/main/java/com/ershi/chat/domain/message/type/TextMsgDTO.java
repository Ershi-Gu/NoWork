package com.ershi.chat.domain.message.type;

import com.ershi.chat.domain.message.BaseMsgDTO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 文本消息参数
 * @author Ershi
 * @date 2025/01/15
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TextMsgDTO implements BaseMsgDTO, Serializable {

    private static final long serialVersionUID = 3598080947382565713L;

    /**
     * 文本消息内容
     */
    @Size(max = 1024, message = "消息内容过长啊兄嘚")
    @NotBlank
    private String content;

    /**
     * 回复的消息id，没有就不传
     */
    private Long replyMsgId;

    /**
     * 与回复消息之间相差的条数
     */
    private Long gapCountToReply;

    /**
     * 艾特的uid，如果是全体艾特传 0
     */
    @Size(max = 10, message = "一次最多艾特10人哦")
    private List<Long> atUidList;
}
