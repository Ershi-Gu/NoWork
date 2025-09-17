package com.ershi.chat.service.handler.message;

import cn.hutool.core.bean.BeanUtil;
import com.ershi.chat.adapter.MessageAdapter;
import com.ershi.chat.domain.message.BaseMsgDTO;
import com.ershi.chat.domain.message.MessageEntity;
import com.ershi.chat.domain.enums.MessageTypeEnum;
import com.ershi.chat.mapper.MessageMapper;
import com.ershi.chat.websocket.domain.dto.ChatMsgReq;
import com.ershi.common.utils.AssertUtil;
import com.ershi.transaction.annotation.SecureInvoke;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.ParameterizedType;

/**
 * 消息处理器抽象父类 - 用于规范和接管所有类型的消息处理器
 *
 * @author Ershi
 * @date 2025/01/14
 */
@Component
public abstract class AbstractMsgHandler<Req> {

    @Resource
    private MessageMapper messageMapper;

    /**
     * 消息体类型保存
     */
    private Class<Req> messageBodyClass;

    /**
     * 消息处理器初始化方法，继承了该类的所有子类都会执行该方法，用于初始化消息处理器，并注册到消息处理器工厂中
     */
    @PostConstruct
    private void init() {
        // 获取泛型类型保存
        ParameterizedType genericSuperclass = (ParameterizedType) this.getClass().getGenericSuperclass();
        this.messageBodyClass = (Class<Req>) genericSuperclass.getActualTypeArguments()[0];
        // 将消息处理器注册到工厂中，k-v:消息类型-消息处理器
        MsgHandlerFactory.register(getMessageTypeEnum().getType(), this);
    }

    /**
     * 获取消息类型
     *
     * @return {@link MessageTypeEnum }
     */
    protected abstract MessageTypeEnum getMessageTypeEnum();

    /**
     * 消息检查规则
     *
     * @param messageBody
     * @param roomId
     * @param uid
     */
    protected abstract void checkMsg(Req messageBody, Long roomId, Long uid);

    /**
     * 消息额外信息保存-子类实现
     *
     * @param message
     * @param messageBody
     */
    protected abstract MessageEntity fillExtra(MessageEntity message, Req messageBody);

    /**
     * 聊天内展示消息
     *
     * @param msg
     * @return {@link java.lang.Object }
     */
    public abstract BaseMsgDTO showMsg(MessageEntity msg);

    /**
     * 当消息被回复时展示形态
     *
     * @param replyMessage
     */
    public abstract Object showReplyMsg(MessageEntity replyMessage);

    /**
     * 消息展示在会话列表上的形式
     *
     * @param message
     * @return {@link String }
     */
    public abstract String showMsgOnContact(MessageEntity message);

    /**
     * 检查消息请求合法性，并转换为消息持久体
     *
     * @param chatMsgReq 发送来的消息体
     * @return {@link Long } 消息持久化后的msgId
     */
    public MessageEntity checkAndBuildEntity(ChatMsgReq chatMsgReq) {
        // 获取消息体的实际类型
        Req messageBody = this.toBean(chatMsgReq.getMessageBody());

        // 消息基本注解校验，校验ChatMessageReq中messageBody的validate注解校验
        AssertUtil.allCheckValidateThrow(messageBody);

        // 子类扩展校验规则
        checkMsg(messageBody, chatMsgReq.getRoomId(), chatMsgReq.getSenderId());

        // 基础消息体转换
        MessageEntity baseMessageEntity = MessageAdapter.buildBaseMessageEntity(chatMsgReq);

        // 子类根据不同的消息类型保存extra扩展信息
        return fillExtra(baseMessageEntity, messageBody);
    }

    /**
     * 异步持久化消息数据，该方法由本地事务表保证可靠性，并在内部进行异步调用
     *
     * @param messageEntity
     * @return {@link Long } 持久化消息主键
     */
    @Transactional(rollbackFor = Exception.class)
    @SecureInvoke(maxRetryTimes = 5, async = true)
    public Long saveMessage(MessageEntity messageEntity) {
        messageMapper.insertSelective(messageEntity);
        return messageEntity.getId();
    }

    /**
     * 将消息转换成具体的消息类型
     *
     * @param body
     * @return {@link Req }
     */
    private Req toBean(Object body) {
        return BeanUtil.toBean(body, messageBodyClass);
    }
}
