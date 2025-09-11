package com.ershi.chat.adapter;

import com.ershi.chat.domain.vo.FriendResp;
import com.ershi.chat.websocket.domain.enums.UserActiveTypeEnum;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * 好友相关模块适配器
 *
 * @author Ershi
 * @date 2024/12/25
 */
public class FriendAdapter {

    /**
     * 将User转换成FriendResp返回
     *
     * @param isOnlineBatch
     * @return {@link List}<{@link FriendResp}>
     */
    public static List<FriendResp> buildFriendRespList( Map<Long, Boolean> isOnlineBatch) {
        return isOnlineBatch.entrySet()
                .stream()
                .map(entry -> FriendResp.builder()
                        .uid(entry.getKey())
                        .activeStatus(entry.getValue() ?
                                UserActiveTypeEnum.ONLINE.getType() :
                                UserActiveTypeEnum.OFFLINE.getType())
                        .build())
                .collect(Collectors.toList());
    }
}
