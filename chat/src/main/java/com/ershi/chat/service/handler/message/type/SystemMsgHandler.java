package com.ershi.chat.service.handler.message.type;

import com.ershi.chat.domain.enums.MessageTypeEnum;
import com.ershi.chat.domain.message.*;
import com.ershi.chat.mapper.MessageMapper;
import com.ershi.chat.service.handler.message.AbstractMsgHandler;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.Optional;


/**
 * 系统消息处理器
 * @author Ershi
 * @date 2025/01/15
 */
@Component
public class SystemMsgHandler extends AbstractMsgHandler<TextMsgDTO> {

    @Resource
    private MessageMapper messageMapper;

    @Override
    protected MessageTypeEnum getMessageTypeEnum() {
        return MessageTypeEnum.SYSTEM;
    }

    @Override
    protected void checkMsg(TextMsgDTO messageBody, Long roomId, Long uid) {

    }

    @Override
    public MessageEntity fillExtra(MessageEntity msg, TextMsgDTO textMsgDTO) {
        MessageExtra extra = Optional.ofNullable(msg.getExtra()).orElse(new MessageExtra());
        msg.setExtra(extra);
        extra.setTextMsgDTO(textMsgDTO);
        return msg;
    }

    @Override
    public BaseMsgDTO showMsg(MessageEntity msg) {
        return msg.getExtra().getTextMsgDTO();
    }

    @Override
    public Object showReplyMsg(MessageEntity replyMessage) {
        return replyMessage.getExtra().getTextMsgDTO().getContent();
    }

    @Override
    public String showMsgOnContact(MessageEntity message) {
        return message.getExtra().getTextMsgDTO().getContent();
    }

}
