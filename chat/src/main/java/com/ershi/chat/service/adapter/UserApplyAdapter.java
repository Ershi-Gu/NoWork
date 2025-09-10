package com.ershi.chat.service.adapter;

import com.ershi.chat.domain.UserApplyEntity;
import com.ershi.chat.domain.enums.UserApplyReadStatusEnum;
import com.ershi.chat.domain.enums.UserApplyStatusEnum;
import com.ershi.chat.domain.enums.UserApplyTypeEnum;

/**
 * 用户申请记录构造器
 *
 * @author Ershi-Gu.
 * @since 2025-09-10
 */
public class UserApplyAdapter {

    /**
     * 构造好友添加申请记录
     *
     * @param uid
     * @param targetUid
     * @param applyMsg
     * @return {@link UserApplyEntity }
     */
    public static UserApplyEntity buildApplyFriendRecord(Long uid, Long targetUid, String applyMsg) {
        return UserApplyEntity.builder()
                .uid(uid)
                .targetId(targetUid)
                .type(UserApplyTypeEnum.FRIEND.getType())
                .msg(applyMsg)
                .status(UserApplyStatusEnum.PENDING.getType())
                .build();
    }
}
