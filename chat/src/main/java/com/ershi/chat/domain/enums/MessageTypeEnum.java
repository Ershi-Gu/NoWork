package com.ershi.chat.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 消息类型枚举
 *
 * @author Ershi
 * @date 2025/01/14
 */
@AllArgsConstructor
@Getter
public enum MessageTypeEnum {

    TEXT(1, "文本消息"),
    RECALL(2, "撤回消息"),
    IMG(3, "图片"),
    FILE(4, "文件"),
    SOUND(5, "语音"),
    VIDEO(6, "视频"),
    EMOJI(7, "表情"),
    SYSTEM(8, "系统消息"),
    ;

    private final Integer type;
    private final String desc;

    private static Map<Integer, MessageTypeEnum> CACHE;

    static {
        CACHE = Arrays.stream(MessageTypeEnum.values()).collect(Collectors.toMap(MessageTypeEnum::getType, Function.identity()));
    }

    public static MessageTypeEnum of(Integer type) {
        return CACHE.get(type);
    }
}
