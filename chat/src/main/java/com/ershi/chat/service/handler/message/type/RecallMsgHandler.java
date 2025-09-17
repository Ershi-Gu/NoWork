package com.ershi.chat.service.handler.message.type;

import com.ershi.chat.constants.MsgOnContactContent;
import com.ershi.chat.constants.MsgReplyContent;
import com.ershi.chat.domain.message.BaseMsgDTO;
import com.ershi.chat.domain.message.MessageEntity;
import com.ershi.chat.domain.message.MessageExtra;
import com.ershi.chat.domain.message.enums.MessageTypeEnum;
import com.ershi.chat.domain.message.type.RecallMsgDTO;
import com.ershi.chat.domain.message.type.TextMsgDTO;
import com.ershi.chat.mapper.MessageMapper;
import com.ershi.chat.service.handler.message.AbstractMsgHandler;
import com.ershi.user.domain.entity.UserEntity;
import com.ershi.user.service.cache.UserInfoCache;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Objects;

/**
 * 撤回消息处理器
 *
 * @author Ershi
 * @date 2025/02/07
 */
@Component
public class RecallMsgHandler extends AbstractMsgHandler<TextMsgDTO> {

    @Resource
    private UserInfoCache userInfoCache;

    @Resource
    private MessageMapper messageMapper;

    @Override
    protected MessageTypeEnum getMessageTypeEnum() {
        return MessageTypeEnum.RECALL;
    }

    @Override
    protected void checkMsg(TextMsgDTO messageBody, Long roomId, Long uid) {}

    @Override
    protected void saveMsg(MessageEntity message, TextMsgDTO messageBody) {
        throw new UnsupportedOperationException("撤回消息不需要保存");

    }

    @Override
    public BaseMsgDTO showMsg(MessageEntity msg) {
        RecallMsgDTO recallInfo = msg.getExtra().getRecallMsgDTO();
        // 获取撤回者信息
        UserEntity userInfo = userInfoCache.get(recallInfo.getRecallUid());
        // 构建返回
        if (!Objects.equals(recallInfo.getRecallUid(), msg.getSenderId())) {
            return TextMsgDTO.builder().content("管理员\"" + userInfo.getName() + "\"撤回了一条成员消息").build();
        }
        return TextMsgDTO.builder().content("\"" + userInfo.getName() + "\"撤回了一条消息").build();
    }

    @Override
    public Object showReplyMsg(MessageEntity replyMessage) {
        return MsgReplyContent.RECALL_REPLY_CONTENT;
    }

    @Override
    public String showMsgOnContact(MessageEntity message) {
        return MsgOnContactContent.RECALL_CONTACT_CONTENT;
    }

    /**
     * 撤回消息
     *
     * @param uid
     * @param msg
     * @return {@link MessageEntity }
     */
    public RecallMsgDTO recall(Long uid, MessageEntity msg) {
        MessageExtra extra = msg.getExtra();
        RecallMsgDTO recallMsgDTO = RecallMsgDTO.builder()
                .recallUid(uid)
                .recallTime(new Date())
                .build();
        extra.setRecallMsgDTO(recallMsgDTO);
        // 更新被撤回的消息，更新其类型以及扩展信息
        msg.setType(MessageTypeEnum.RECALL.getType()) ;
        msg.setExtra(extra);
        messageMapper.update(msg);
        return recallMsgDTO;
    }
}
