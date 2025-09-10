package com.ershi.chat.service;


import com.ershi.chat.domain.UserApplyEntity;
import com.ershi.chat.domain.dto.FriendApplyReq;
import com.ershi.chat.domain.dto.FriendApproveReq;
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
     * @param targetUid
     * @param targetAccount
     * @param applyMsg
     */
    void applyFriend(Long targetUid, String targetAccount, String applyMsg);

    /**
     * 审批好友申请
     *
     * @param applyId
     * @param status
     */
    void approveFriend(Long applyId, Integer status);
}