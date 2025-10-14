package com.ershi.chat.websocket.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * websocket 后端推送类型枚举
 *
 * @author Ershi
 * @date 2024/11/24
 */
@AllArgsConstructor
@Getter
public enum WSRespTypeEnum {
    ERROR("error", "错误消息"),
    LOGIN_SUCCESS("login_success", "用户身份认证成功"),
    SERVER_ACK("server_ack", "服务端接收聊天消息确认"),
    NEW_MESSAGE("new_message", "推送新消息"),
    FRIEND_APPLY("friend_apply", "好友申请通知"),
    ACK_SUCCESS("ack_success", "消息ack操作完成"),
    READ_SUCCESS("read_success", "消息已读游标更新"),
    ;

    /**
     * 推送消息类型
     */
    private final String type;

    /**
     * 推送消息描述
     */
    private final String desc;

    /**
     * 枚举类缓存
     */
    private static final Map<String, WSRespTypeEnum> CACHE;

    static {
        CACHE = Arrays.stream(WSRespTypeEnum.values()).collect(Collectors.toMap(WSRespTypeEnum::getType, Function.identity()));
    }

    public static WSRespTypeEnum of(String type) {
        return CACHE.get(type);
    }
}
