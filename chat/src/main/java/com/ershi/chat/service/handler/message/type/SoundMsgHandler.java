package com.ershi.chat.service.handler.message.type;

import com.ershi.chat.constants.MsgOnContactContent;
import com.ershi.chat.constants.MsgReplyContent;
import com.ershi.chat.domain.enums.MessageTypeEnum;
import com.ershi.chat.domain.message.*;
import com.ershi.chat.mapper.MessageMapper;
import com.ershi.chat.service.handler.message.AbstractMsgHandler;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * 语音消息处理器
 *
 * @author Ershi
 * @date 2025/01/15
 */
@Component
public class SoundMsgHandler extends AbstractMsgHandler<SoundMsgDTO> {

    @Resource
    private MessageMapper messageMapper;

    @Override
    protected MessageTypeEnum getMessageTypeEnum() {
        return MessageTypeEnum.SOUND;
    }

    @Override
    protected void checkMsg(SoundMsgDTO soundMsgDTO, Long roomId, Long uid) {
    }

    @Override
    public MessageEntity fillExtra(MessageEntity msg, SoundMsgDTO soundMsgDTO) {
        MessageExtra extra = Optional.ofNullable(msg.getExtra()).orElse(new MessageExtra());
        msg.setExtra(extra);
        extra.setSoundMsgDTO(soundMsgDTO);
        return msg;
    }

    @Override
    public BaseMsgDTO showMsg(MessageEntity msg) {
        return msg.getExtra().getSoundMsgDTO();
    }

    @Override
    public Object showReplyMsg(MessageEntity replyMessage) {
        return MsgReplyContent.SOUND_REPLY_CONTENT;
    }

    @Override
    public String showMsgOnContact(MessageEntity message) {
        return MsgOnContactContent.SOUND_CONTACT_CONTENT;
    }
}
