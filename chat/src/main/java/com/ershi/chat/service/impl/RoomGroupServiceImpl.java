package com.ershi.chat.service.impl;


import com.ershi.chat.adapter.GroupMemberAdapter;
import com.ershi.chat.adapter.RoomGroupAdapter;
import com.ershi.chat.domain.GroupInviteEntity;
import com.ershi.chat.domain.GroupMemberEntity;
import com.ershi.chat.domain.RoomEntity;
import com.ershi.chat.domain.RoomGroupEntity;
import com.ershi.chat.domain.enums.GroupInviteStatusEnum;
import com.ershi.chat.domain.enums.GroupMemberRoleEnum;
import com.ershi.chat.domain.enums.RoomHotFlagEnum;
import com.ershi.chat.domain.enums.RoomTypeEnum;
import com.ershi.chat.domain.vo.GroupCreateResp;
import com.ershi.chat.domain.vo.GroupInfoResp;
import com.ershi.chat.event.CreateRoomGroupEvent;
import com.ershi.chat.event.InviteGroupEvent;
import com.ershi.chat.mapper.RoomGroupMapper;
import com.ershi.chat.service.*;
import com.ershi.chat.service.cache.GroupMemberCacheV2;
import com.ershi.chat.service.cache.RoomCache;
import com.ershi.chat.service.cache.RoomGroupCache;
import com.ershi.common.exception.BusinessErrorEnum;
import com.ershi.common.exception.BusinessException;
import com.ershi.common.utils.AssertUtil;
import com.ershi.common.utils.RequestHolder;
import com.ershi.user.domain.entity.UserEntity;
import com.ershi.user.service.IUserService;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 群聊房间表 服务层实现。
 *
 * @author mybatis-flex-helper automatic generation
 * @since 1.0
 */
@Slf4j
@Service
public class RoomGroupServiceImpl extends ServiceImpl<RoomGroupMapper, RoomGroupEntity> implements IRoomGroupService {

    @Resource
    private IRoomService roomService;

    @Resource
    private IUserService userService;

    @Resource
    private IGroupMemberService groupMemberService;

    @Resource
    private ApplicationEventPublisher applicationEventPublisher;

    @Resource
    private RoomCache roomCache;

    @Resource
    private RoomGroupCache roomGroupCache;

    @Resource
    private GroupMemberCacheV2 groupMemberCacheV2;

    @Resource
    private IUserFriendService userFriendService;

    @Resource
    private IGroupInviteService groupInviteService;

    @Override
    @Transactional
    public GroupCreateResp createRoomGroup(List<Long> uidList) {
        Long ownerUid = RequestHolder.get().getUid();
        // 无法仅与自己创建群聊
        if (uidList.size() == 1 && Objects.equals(uidList.getFirst(), ownerUid)) {
            throw new BusinessException(BusinessErrorEnum.API_PARAM_ERROR.getErrorCode(), "请选择其他群成员");
        }

        // 获取群主以及被邀请群成员信息
        Map<Long, UserEntity> userMap = Stream.concat(
                        Stream.of(ownerUid),
                        uidList.stream()).distinct()
                .collect(Collectors.collectingAndThen(
                        Collectors.toList(),
                        ids -> userService.listByIds(ids).stream()
                                .collect(Collectors.toMap(UserEntity::getId, Function.identity()))
                ));

        // 创建群组
        RoomEntity roomEntity = roomService.createRoom(RoomTypeEnum.GROUP.getType(), RoomHotFlagEnum.NOT_HOT.getType());
        RoomGroupEntity roomGroupEntity = RoomGroupAdapter.buildRoomGroup(roomEntity, userMap.get(ownerUid));
        this.save(roomGroupEntity);

        // 构建groupMembers信息
        GroupMemberEntity leader = GroupMemberAdapter.buildGroupLeader(userMap.get(ownerUid), roomGroupEntity);
        List<GroupMemberEntity> members = GroupMemberAdapter.buildGroupMembers(
                userMap.values().stream()
                        // 剔除群主
                        .filter(user -> !user.getId().equals(ownerUid))
                        .toList(),
                roomGroupEntity,
                GroupMemberRoleEnum.MEMBER);
        List<GroupMemberEntity> groupMembers = Stream.concat(
                Stream.of(leader),
                members.stream()).toList();
        groupMemberService.saveBatch(groupMembers);

        // 发布群聊创建事件 -> 向群成员发送一条入群信息
        applicationEventPublisher.publishEvent(new CreateRoomGroupEvent(this, roomGroupEntity,
                groupMembers, RequestHolder.get().getUid()));

        return GroupCreateResp.builder()
                .id(roomGroupEntity.getId())
                .roomId(roomGroupEntity.getRoomId())
                .name(roomGroupEntity.getName())
                .avatarUrl(roomGroupEntity.getAvatarUrl())
                .build();
    }

    @Override
    public GroupInfoResp getGroupInfo(Long roomId) {
        Long uid = RequestHolder.get().getUid();

        // 从缓存获取群聊信息
        RoomGroupEntity roomGroup = roomGroupCache.get(roomId);
        AssertUtil.nonNull(roomGroup, BusinessErrorEnum.ROOM_NOT_EXIST_ERROR);

        // 从缓存获取当前用户在群组中的成员信息
        GroupMemberEntity member = groupMemberCacheV2.getMemberInfo(roomId, uid);
        AssertUtil.nonNull(member, BusinessErrorEnum.MEMBER_NOT_EXIST_ERROR);

        // 构建并返回群聊信息
        return GroupInfoResp.builder()
                .roomId(roomId)
                .groupName(roomGroup.getName())
                .avatar(roomGroup.getAvatarUrl())
                .role(member.getRole())
                .build();
    }

    @Override
    public List<Long> getMemberUidList(Long roomId) {
        // 获取群成员uid列表
        List<Long> memberUidList = groupMemberCacheV2.getMemberUidList(roomId);

        if (memberUidList == null) {
            log.error("会话：{}，不存在群成员", roomId);
            return Collections.emptyList();
        }

        return memberUidList;
    }

    @Override
    @Transactional
    public void exitGroup(Long roomId) {
        Long uid = RequestHolder.get().getUid();

        // 从缓存获取群聊信息
        RoomGroupEntity roomGroup = roomGroupCache.get(roomId);
        AssertUtil.nonNull(roomGroup, BusinessErrorEnum.ROOM_NOT_EXIST_ERROR);

        // 从缓存获取当前用户在群组中的成员信息
        GroupMemberEntity member = groupMemberCacheV2.getMemberInfo(roomId, uid);

        // 判断用户是否已退出
        AssertUtil.nonNull(member, BusinessErrorEnum.MEMBER_NOT_EXIST_ERROR);
        AssertUtil.isFalse(GroupMemberRoleEnum.REMOVE.getType().equals(member.getRole()),
                BusinessErrorEnum.MEMBER_NOT_EXIST_ERROR);

        // 群主不能退出群聊
        AssertUtil.isFalse(Objects.equals(member.getRole(), GroupMemberRoleEnum.LEADER.getType()),
                BusinessErrorEnum.API_PARAM_ERROR, "群主无法退出群聊，请先转让群主");

        // 更新成员角色为已移除
        member.setRole(GroupMemberRoleEnum.REMOVE.getType());
        groupMemberService.updateById(member);

        // 清除缓存，保证缓存一致性
        groupMemberCacheV2.delete(roomId, uid);
    }

    @Override
    public void inviteFriendToGroup(Long roomId, List<Long> friendUidList) {
        Long uid = RequestHolder.get().getUid();

        // 校验参数
        AssertUtil.nonNull(roomId, BusinessErrorEnum.API_PARAM_ERROR);
        AssertUtil.isNotEmpty(friendUidList, BusinessErrorEnum.API_PARAM_ERROR);

        // 验证群聊是否存在
        RoomEntity room = roomCache.get(roomId);
        AssertUtil.nonNull(room, BusinessErrorEnum.ROOM_NOT_EXIST_ERROR);
        RoomGroupEntity roomGroup = roomGroupCache.get(roomId);
        AssertUtil.nonNull(roomGroup, BusinessErrorEnum.ROOM_NOT_EXIST_ERROR);

        // 验证群聊类型是否正确
        AssertUtil.isTrue(RoomTypeEnum.GROUP.getType().equals(room.getType()), BusinessErrorEnum.API_PARAM_ERROR,
                "目标群组类型错误，请选择群聊类型会话");

        // 验证当前用户是否是群成员
        GroupMemberEntity inviter = groupMemberCacheV2.getMemberInfo(roomId, uid);
        AssertUtil.nonNull(inviter, BusinessErrorEnum.MEMBER_NOT_EXIST_ERROR);
        AssertUtil.isFalse(GroupMemberRoleEnum.REMOVE.getType().equals(inviter.getRole()),
                BusinessErrorEnum.MEMBER_NOT_EXIST_ERROR);

        // 去重好友uid列表
        List<Long> distinctFriendUidList = friendUidList.stream().distinct().toList();

        // 批量验证好友关系、群成员状态和邀请记录
        List<GroupInviteEntity> inviteList = distinctFriendUidList.stream()
                .filter(friendUid -> {
                    // 验证是否是好友关系
                    boolean isFriend = userFriendService.isFriend(uid, friendUid);
                    if (!isFriend) {
                        log.warn("用户uid:{}尝试邀请非好友uid:{}加入群聊roomId:{}", uid, friendUid, roomId);
                        return false;
                    }

                    // 验证被邀请人是否已经是群成员
                    GroupMemberEntity friendMember = groupMemberCacheV2.getMemberInfo(roomId, friendUid);
                    if (friendMember != null && !GroupMemberRoleEnum.REMOVE.getType().equals(friendMember.getRole())) {
                        log.warn("用户uid:{}已是群聊roomId:{}的成员，无需重复邀请", friendUid, roomId);
                        return false;
                    }

                    // 验证是否已存在待确认的邀请记录
                    boolean hasPendingInvite = groupInviteService.hasPendingInvite(roomId, uid, friendUid);
                    if (hasPendingInvite) {
                        log.warn("用户uid:{}已邀请uid:{}加入群聊roomId:{}，邀请待确认中", uid, friendUid, roomId);
                        return false;
                    }

                    return true;
                })
                .map(friendUid -> {
                    // 构建群聊邀请记录
                    GroupInviteEntity invite = new GroupInviteEntity();
                    invite.setRoomId(roomId);
                    invite.setInviterId(uid);
                    invite.setInvitedId(friendUid);
                    // 默认待确认状态
                    invite.setStatus(GroupInviteStatusEnum.PENDING.getType());
                    return invite;
                })
                .toList();

        // 发布群聊邀请事件，异步处理持久化和通知消息
        if (!inviteList.isEmpty()) {
            applicationEventPublisher.publishEvent(new InviteGroupEvent(this, inviteList));
        }
    }
}