package com.ershi.chat.service.handler.message.type;

import com.ershi.chat.constants.MsgOnContactContent;
import com.ershi.chat.constants.MsgReplyContent;
import com.ershi.chat.domain.enums.MessageTypeEnum;
import com.ershi.chat.domain.message.*;
import com.ershi.chat.mapper.MessageMapper;
import com.ershi.chat.service.handler.message.AbstractMsgHandler;
import com.ershi.common.exception.BusinessErrorEnum;
import com.ershi.common.exception.BusinessException;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * 文件消息处理器
 *
 * @author Ershi
 * @date 2025/01/15
 */
@Component
public class FileMsgHandler extends AbstractMsgHandler<FileMsgDTO> {

    @Resource
    private MessageMapper messageDao;

    @Override
    protected MessageTypeEnum getMessageTypeEnum() {
        return MessageTypeEnum.FILE;
    }

    /**
     * 文件扩展检查
     *
     * @param messageBody
     * @param roomId
     * @param uid
     */
    @Override
    protected void checkMsg(FileMsgDTO messageBody, Long roomId, Long uid) {
        // 获取文件名，查看是否带后缀
        if (!messageBody.getFileName().contains(".")) {
            throw new BusinessException(BusinessErrorEnum.API_PARAM_ERROR.getErrorCode(), "文件要带后缀名！");
        }
    }

    /**
     * 文件參數額外信息保存
     *
     * @param msg
     * @param fileMsgDTO
     */
    @Override
    public MessageEntity fillExtra(MessageEntity msg, FileMsgDTO fileMsgDTO) {
        MessageExtra extra = Optional.ofNullable(msg.getExtra()).orElse(new MessageExtra());
        msg.setExtra(extra);
        extra.setFileMsgDTO(fileMsgDTO);
        return msg;
    }

    @Override
    public BaseMsgDTO showMsg(MessageEntity msg) {
        return msg.getExtra().getFileMsgDTO();
    }

    @Override
    public Object showReplyMsg(MessageEntity replyMessage) {
        return MsgReplyContent.FILE_REPLY_CONTENT;
    }

    @Override
    public String showMsgOnContact(MessageEntity message) {
        return MsgOnContactContent.FILE_CONTACT_CONTENT;
    }
}
