package com.ershi.chat.service.handler.message.type;

import cn.hutool.core.collection.CollectionUtil;
import com.ershi.chat.adapter.MessageAdapter;
import com.ershi.chat.domain.message.BaseMsgDTO;
import com.ershi.chat.domain.message.MessageEntity;
import com.ershi.chat.domain.message.MessageExtra;
import com.ershi.chat.domain.message.enums.MessageTypeEnum;
import com.ershi.chat.domain.message.type.TextMsgDTO;
import com.ershi.chat.domain.message.vo.TextMsgResp;
import com.ershi.chat.mapper.MessageMapper;
import com.ershi.chat.service.handler.message.AbstractMsgHandler;
import com.ershi.common.exception.BusinessErrorEnum;
import com.ershi.common.utils.AssertUtil;
import com.ershi.user.domain.entity.UserEntity;
import com.ershi.user.service.cache.UserInfoCache;
import com.mybatisflex.core.query.QueryWrapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.ershi.chat.domain.message.table.MessageEntityTableDef.MESSAGE_ENTITY;


/**
 * 文本消息处理器
 *
 * @author Ershi
 * @date 2025/01/15
 */
@Component
public class TextMsgHandler extends AbstractMsgHandler<TextMsgDTO> {

    @Resource
    private MessageMapper messageMapper;

    @Resource
    private UserInfoCache userInfoCache;

    @Override
    protected MessageTypeEnum getMessageTypeEnum() {
        return MessageTypeEnum.TEXT;
    }

    /**
     * 文本消息合法性检查
     *
     * @param messageBody
     * @param roomId
     * @param uid
     */
    @Override
    protected void checkMsg(TextMsgDTO messageBody, Long roomId, Long uid) {
        // 如果是回复消息，校验回复消息是否存在以及合法性
        checkReplyMsg(messageBody, roomId);
        // 如果有艾特用户，需要校验艾特合法性
        checkAtMsg(messageBody, roomId, uid);
    }

    /**
     * 检查消息回复操作的合法性
     *
     * @param messageBody
     * @param roomId
     */
    private void checkReplyMsg(TextMsgDTO messageBody, Long roomId) {
        if (Objects.nonNull(messageBody.getReplyMsgId())) {
            MessageEntity replyMsg = messageMapper.selectOneById(messageBody.getReplyMsgId());
            AssertUtil.isNotEmpty(BusinessErrorEnum.API_PARAM_ERROR, "回复的消息不存在");
            AssertUtil.equal(replyMsg.getRoomId(), roomId,
                    BusinessErrorEnum.API_PARAM_ERROR, "只能回复相同会话内的消息");
        }
    }

    /**
     * 艾特用户操作合法性校验
     *
     * @param messageBody
     * @param roomId
     * @param uid
     */
    private void checkAtMsg(TextMsgDTO messageBody, Long roomId, Long uid) {
        if (CollectionUtil.isNotEmpty(messageBody.getAtUidList())) {
            // 前端传入的艾特用户列表可能会重复，需要去重，支持多次艾特一个用户，但是只显示一次
            List<Long> atUidList = messageBody.getAtUidList().stream().distinct().collect(Collectors.toList());
            Long atUidNum = (long) atUidList.size();
            Map<Long, UserEntity> userMap = userInfoCache.getBatch(atUidList);
            // todo 检查是否是艾特全体成员，判断用户在该群聊中的权限
            boolean atAll = messageBody.getAtUidList().contains(0L);
            if (atAll) {
                return;
            }
            // 校验艾特的用户是否存在
            // 如果艾特用户不存在，userInfoCache返回的map中依然存在该key，但是value为null，需要过滤掉再校验；同时过滤掉0L，表示艾特全体成员
            long batchCount = userMap.values().stream().filter(Objects::nonNull).count();
            AssertUtil.equal(atUidNum, batchCount, "@用户不存在");
        }
    }

    /**
     * 保存文本消息额外参数内容
     *
     * @param msg
     * @param textMsgDTO
     */
    @Override
    public void saveMsg(MessageEntity msg, TextMsgDTO textMsgDTO) {
        // 保存消息内容
        MessageExtra extra = Optional.ofNullable(msg.getExtra()).orElse(new MessageExtra());
        msg.setExtra(extra);
        extra.setTextMsgDTO(textMsgDTO);
        // todo 敏感词过滤
        // 判断是否是回复消息，若是则计算出与回复消息相差条数并存储
        if (Objects.nonNull(textMsgDTO.getReplyMsgId())) {
            Long gapCountToReply = getGapCount(msg.getRoomId(), msg.getId(), textMsgDTO.getReplyMsgId());
            textMsgDTO.setGapCountToReply(gapCountToReply);
        }
        // 去重艾特uid
        if (CollectionUtil.isNotEmpty(textMsgDTO.getAtUidList())) {
            textMsgDTO.setAtUidList(textMsgDTO.getAtUidList().stream().distinct().collect(Collectors.toList()));
        }
        // todo url卡片解析
        // 更新BD
        messageMapper.update(msg);
    }

    @Override
    public BaseMsgDTO showMsg(MessageEntity msg) {
        TextMsgDTO textMsgDTO = msg.getExtra().getTextMsgDTO();

        // 构建文本消息返回体
        TextMsgResp textMsgResp = TextMsgResp.builder()
                .content(textMsgDTO.getContent())
                .build();

        // 设置艾特的uid
        textMsgResp.setAtUidList(textMsgDTO.getAtUidList());

        // 获取回复的消息
        Optional<MessageEntity> reply = Optional.ofNullable(textMsgDTO.getReplyMsgId())
                .map(messageMapper::selectOneById);
        if (reply.isEmpty()) {
            return textMsgResp;
        }
        // 如果回复消息存在，组装回复消息
        MessageEntity replyMessage = reply.get();
        UserEntity replyUserInfo = userInfoCache.get(replyMessage.getSenderId());
        textMsgResp.setReply(MessageAdapter.buildReplyMessage(textMsgDTO, replyMessage, replyUserInfo));
        return textMsgResp;
    }

    @Override
    public Object showReplyMsg(MessageEntity replyMessage) {
        return replyMessage.getExtra().getTextMsgDTO().getContent();
    }

    @Override
    public String showMsgOnContact(MessageEntity message) {
        return message.getExtra().getTextMsgDTO().getContent();
    }

    /**
     * 获取回复消息与被回复消息之间间隔条数
     *
     * @param roomId
     * @param msgId 回复消息id
     * @param replyMsgId 被回复消息id
     * @return {@link Integer }
     */
    private Long getGapCount(Long roomId, Long msgId, Long replyMsgId) {
        return messageMapper.selectCountByQuery(QueryWrapper.create()
                .where(MESSAGE_ENTITY.ROOM_ID.eq(roomId))
                .and(MESSAGE_ENTITY.ID.gt(msgId))
                .and(MESSAGE_ENTITY.ID.le(replyMsgId)));
    }
}
