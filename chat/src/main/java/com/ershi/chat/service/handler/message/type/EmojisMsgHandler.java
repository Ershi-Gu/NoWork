package com.ershi.chat.service.handler.message.type;
import com.ershi.chat.constants.MsgOnContactContent;
import com.ershi.chat.constants.MsgReplyContent;
import com.ershi.chat.domain.message.MessageEntity;
import com.ershi.chat.domain.message.BaseMsgDTO;
import com.ershi.chat.domain.message.MessageExtra;
import com.ershi.chat.domain.message.enums.MessageTypeEnum;
import com.ershi.chat.domain.message.type.EmojisMsgDTO;
import com.ershi.chat.mapper.MessageMapper;
import com.ershi.chat.service.handler.message.AbstractMsgHandler;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * 表情消息处理器
 *
 * @author Ershi
 * @date 2025/01/15
 */
@Component
public class EmojisMsgHandler extends AbstractMsgHandler<EmojisMsgDTO> {

    @Resource
    private MessageMapper messageMapper;

    @Override
    protected MessageTypeEnum getMessageTypeEnum() {
        return MessageTypeEnum.EMOJI;
    }

    @Override
    protected void checkMsg(EmojisMsgDTO messageBody, Long roomId, Long uid) {

    }

    /**
     * emoji額外信息保存
     *
     * @param msg
     * @param emojisMsgDTO
     */
    @Override
    public void saveMsg(MessageEntity msg, EmojisMsgDTO emojisMsgDTO) {
        MessageExtra extra = Optional.ofNullable(msg.getExtra()).orElse(new MessageExtra());
        msg.setExtra(extra);
        extra.setEmojisMsgDTO(emojisMsgDTO);
        messageMapper.update(msg);
    }

    @Override
    public BaseMsgDTO showMsg(MessageEntity msg) {
        return msg.getExtra().getEmojisMsgDTO();
    }

    @Override
    public Object showReplyMsg(MessageEntity replyMessage) {
        return MsgReplyContent.EMOJIS_REPLY_CONTENT;
    }

    @Override
    public String showMsgOnContact(MessageEntity message) {
        return MsgOnContactContent.EMOJIS_CONTACT_CONTENT;
    }
}
