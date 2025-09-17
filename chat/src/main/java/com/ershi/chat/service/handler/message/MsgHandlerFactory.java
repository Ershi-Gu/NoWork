package com.ershi.chat.service.handler.message;


import com.ershi.chat.domain.enums.MessageTypeEnum;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * 消息处理器工厂 - 用来加载不同的消息处理器
 *
 * @author Ershi
 * @date 2025/01/14
 */
@Slf4j
public class MsgHandlerFactory {

    /**
     * 保存各个类型消息对应的处理器 <br>
     * k:v-消息类型整型-消息处理器
     */
    private static final Map<Integer, AbstractMsgHandler> msgHandlerMap = new HashMap<>();

    /**
     * 将消息处理器注册到工厂中
     *
     * @param msgType
     * @param msgHandler
     */
    public static <Req> void register(Integer msgType, AbstractMsgHandler<Req> msgHandler) {
        msgHandlerMap.put(msgType, msgHandler);
    }

    /**
     * 根据消息类型获取消息处理器
     *
     * @param msgType
     * @return {@link AbstractMsgHandler }
     */
    public static AbstractMsgHandler getMsgHandlerNoNull(Integer msgType) {
        AbstractMsgHandler msgHandler = msgHandlerMap.get(msgType);
        if (msgHandler == null) {
            log.error("【{}】 消息处理器获取失败，注册错误", MessageTypeEnum.of(msgType).getDesc());
        }
        return msgHandler;
    }
}
