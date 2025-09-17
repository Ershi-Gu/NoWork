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
 * 视频消息处理器
 *
 * @author Ershi
 * @date 2025/01/15
 */
@Component
public class VideoMsgHandler extends AbstractMsgHandler<VideoMsgDTO> {

    @Resource
    private MessageMapper messageMapper;

    @Override
    protected MessageTypeEnum getMessageTypeEnum() {
        return MessageTypeEnum.VIDEO;
    }

    @Override
    protected void checkMsg(VideoMsgDTO messageBody, Long roomId, Long uid) {

    }

    @Override
    public MessageEntity fillExtra(MessageEntity msg, VideoMsgDTO videoMsgDTO) {
        MessageExtra extra = Optional.ofNullable(msg.getExtra()).orElse(new MessageExtra());
        msg.setExtra(extra);
        extra.setVideoMsgDTO(videoMsgDTO);
        return msg;
    }

    @Override
    public BaseMsgDTO showMsg(MessageEntity msg) {
        return msg.getExtra().getVideoMsgDTO();
    }

    @Override
    public Object showReplyMsg(MessageEntity replyMessage) {
        return MsgReplyContent.VIDEO_REPLY_CONTENT;
    }

    @Override
    public String showMsgOnContact(MessageEntity message) {
        return MsgOnContactContent.VIDEO_CONTACT_CONTENT;
    }
}
