package com.ershi.chat.service;


import com.ershi.chat.domain.RoomGroupEntity;
import com.ershi.chat.domain.vo.GroupCreateResp;
import com.ershi.chat.domain.vo.GroupInfoResp;
import com.mybatisflex.core.service.IService;

import java.util.List;

/**
 * 群聊房间表 服务层。
 *
 * @author mybatis-flex-helper automatic generation
 * @since 1.0
 */
public interface IRoomGroupService extends IService<RoomGroupEntity> {

    /**
     * 新建群聊
     *
     * @param uidList
     * @return {@link GroupCreateResp }
     */
    GroupCreateResp createRoomGroup(List<Long> uidList);

    /**
     * 获取群聊信息
     *
     * @param roomId
     * @return {@link GroupInfoResp }
     */
    GroupInfoResp getGroupInfo(Long roomId);

    /**
     * 获取群成员uid列表
     *
     * @param roomId
     * @return {@link List }<{@link Long }>
     */
    List<Long> getMemberUidList(Long roomId);
}