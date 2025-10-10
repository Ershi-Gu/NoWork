package com.ershi.chat.service;


import com.ershi.chat.domain.GroupInviteEntity;
import com.ershi.chat.domain.vo.GroupInviteResp;
import com.mybatisflex.core.service.IService;

import java.util.List;

/**
 * 群聊邀请表 服务层。
 *
 * @author mybatis-flex-helper automatic generation
 * @since 1.0
 */
public interface IGroupInviteService extends IService<GroupInviteEntity> {

    /**
     * 检查是否存在待确认的邀请记录
     *
     * @param roomId 群聊房间id
     * @param inviterId 邀请人id
     * @param invitedId 被邀请人id
     * @return 是否存在待确认的邀请
     */
    boolean hasPendingInvite(Long roomId, Long inviterId, Long invitedId);

    /**
     * 获取用户收到的群聊邀请列表（仅展示10条）
     *
     * @param uid 用户id
     * @return 邀请列表
     */
    List<GroupInviteResp> getGroupInviteList(Long uid);
}