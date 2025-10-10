package com.ershi.chat.service.impl;


import com.ershi.chat.domain.RoomGroupEntity;
import com.ershi.chat.domain.enums.GroupInviteStatusEnum;
import com.ershi.chat.domain.vo.GroupInviteResp;
import com.ershi.chat.service.cache.RoomGroupCache;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import com.ershi.chat.service.IGroupInviteService;
import com.ershi.chat.domain.GroupInviteEntity;
import com.ershi.chat.mapper.GroupInviteMapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;

import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

import static com.ershi.chat.domain.table.GroupInviteEntityTableDef.GROUP_INVITE_ENTITY;

/**
 * 群聊邀请表 服务层实现。
 *
 * @author mybatis-flex-helper automatic generation
 * @since 1.0
 */
@Service
public class GroupInviteServiceImpl extends ServiceImpl<GroupInviteMapper, GroupInviteEntity> implements IGroupInviteService {

    @Resource
    private RoomGroupCache roomGroupCache;

    @Override
    public boolean hasPendingInvite(Long roomId, Long inviterId, Long invitedId) {
        return this.queryChain()
                .where(GROUP_INVITE_ENTITY.ROOM_ID.eq(roomId))
                .and(GROUP_INVITE_ENTITY.INVITER_ID.eq(inviterId))
                .and(GROUP_INVITE_ENTITY.INVITED_ID.eq(invitedId))
                .and(GROUP_INVITE_ENTITY.STATUS.eq(GroupInviteStatusEnum.PENDING.getType()))
                .exists();
    }

    @Override
    public List<GroupInviteResp> getGroupInviteList(Long uid) {
        // 查询用户收到的邀请记录，按创建时间倒序排列，仅返回前10条
        List<GroupInviteEntity> inviteList = this.queryChain()
                .where(GROUP_INVITE_ENTITY.INVITED_ID.eq(uid))
                .orderBy(GROUP_INVITE_ENTITY.CREATE_TIME.desc())
                .limit(10)
                .list();

        // 转换为响应DTO
        return inviteList.stream()
                .map(invite -> {
                    GroupInviteResp resp = new GroupInviteResp();
                    resp.setId(invite.getId());
                    resp.setRoomId(invite.getRoomId());
                    resp.setInviterId(invite.getInviterId());
                    resp.setStatus(invite.getStatus());
                    resp.setCreateTime(invite.getCreateTime());

                    // 从缓存获取群聊信息
                    RoomGroupEntity roomGroup = roomGroupCache.get(invite.getRoomId());
                    if (roomGroup != null) {
                        resp.setGroupName(roomGroup.getName());
                        resp.setGroupAvatar(roomGroup.getAvatarUrl());
                    }

                    return resp;
                })
                .collect(Collectors.toList());
    }
}