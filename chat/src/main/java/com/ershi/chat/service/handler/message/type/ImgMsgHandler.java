package com.ershi.chat.service.handler.message.type;

import com.ershi.chat.constants.MsgOnContactContent;
import com.ershi.chat.constants.MsgReplyContent;
import com.ershi.chat.domain.message.*;
import com.ershi.chat.domain.enums.MessageTypeEnum;
import com.ershi.chat.mapper.MessageMapper;
import com.ershi.chat.service.handler.message.AbstractMsgHandler;
import com.ershi.common.exception.BusinessErrorEnum;
import com.ershi.common.exception.BusinessException;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * 图片消息处理器
 *
 * @author Ershi
 * @date 2025/01/15
 */
@Component
public class ImgMsgHandler extends AbstractMsgHandler<ImgMsgDTO> {

    @Resource
    private MessageMapper messageMapper;

    @Override
    protected MessageTypeEnum getMessageTypeEnum() {
        return MessageTypeEnum.IMG;
    }

    @Override
    protected void checkMsg(ImgMsgDTO imgMsgDTO, Long roomId, Long uid) {
        // 校验图片宽度、长度不能为负数
        if (imgMsgDTO.getWidth() < 0 || imgMsgDTO.getHeight() < 0) {
            throw new BusinessException(BusinessErrorEnum.API_PARAM_ERROR.getErrorCode(), "图片宽度、长度不能为负数！");
        }
    }

    @Override
    public MessageEntity fillExtra(MessageEntity msg, ImgMsgDTO imgMsgDTO) {
        MessageExtra extra = Optional.ofNullable(msg.getExtra()).orElse(new MessageExtra());
        msg.setExtra(extra);
        extra.setImgMsgDTO(imgMsgDTO);
        return msg;
    }

    @Override
    public BaseMsgDTO showMsg(MessageEntity msg) {
        return msg.getExtra().getImgMsgDTO();
    }

    @Override
    public Object showReplyMsg(MessageEntity replyMessage) {
        return MsgReplyContent.IMG_REPLY_CONTENT;
    }

    @Override
    public String showMsgOnContact(MessageEntity message) {
        return MsgOnContactContent.IMG_CONTACT_CONTENT;
    }
}
