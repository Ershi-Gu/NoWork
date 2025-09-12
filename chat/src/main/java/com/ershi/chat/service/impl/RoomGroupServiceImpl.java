package com.ershi.chat.service.impl;


import com.ershi.chat.adapter.GroupMemberAdapter;
import com.ershi.chat.adapter.RoomGroupAdapter;
import com.ershi.chat.domain.GroupMemberEntity;
import com.ershi.chat.domain.RoomEntity;
import com.ershi.chat.domain.RoomGroupEntity;
import com.ershi.chat.domain.enums.GroupMemberRoleEnum;
import com.ershi.chat.domain.enums.RoomHotFlagEnum;
import com.ershi.chat.domain.enums.RoomTypeEnum;
import com.ershi.chat.domain.vo.GroupCreateResp;
import com.ershi.chat.event.CreateRoomGroupEvent;
import com.ershi.chat.mapper.RoomGroupMapper;
import com.ershi.chat.service.IGroupMemberService;
import com.ershi.chat.service.IRoomGroupService;
import com.ershi.chat.service.IRoomService;
import com.ershi.common.exception.BusinessErrorEnum;
import com.ershi.common.exception.BusinessException;
import com.ershi.common.utils.RequestHolder;
import com.ershi.user.domain.entity.UserEntity;
import com.ershi.user.service.IUserService;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Override
    @Transactional
    public GroupCreateResp createRoomGroup(List<Long> uidList) {
        Long ownerUid = RequestHolder.get().getUid();
        // 无法仅与自己创建群聊
        if (uidList.size() == 1 && Objects.equals(uidList.get(0), ownerUid)) {
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

        // todo 发布群聊创建事件 -> 向群成员发送一条入群信息
        applicationEventPublisher.publishEvent(new CreateRoomGroupEvent(this, roomGroupEntity, groupMembers));

        return GroupCreateResp.builder()
                .id(roomGroupEntity.getId())
                .roomId(roomGroupEntity.getRoomId())
                .name(roomGroupEntity.getName())
                .avatarUrl(roomGroupEntity.getAvatarUrl())
                .build();
    }
}