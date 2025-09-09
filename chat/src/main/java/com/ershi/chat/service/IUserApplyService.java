package com.ershi.chat.service;


import com.ershi.chat.domain.UserApplyEntity;
import com.ershi.chat.domain.dto.FriendApplyReq;
import com.mybatisflex.core.service.IService;

/**
 * 用户申请表 服务层。
 *
 * @author mybatis-flex-helper automatic generation
 * @since 1.0
 */
public interface IUserApplyService extends IService<UserApplyEntity> {

    /**
     * 申请添加好友
     *
     * @param friendApplyReq
     */
    void applyFriend(FriendApplyReq friendApplyReq);
}