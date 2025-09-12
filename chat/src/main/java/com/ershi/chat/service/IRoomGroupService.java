package com.ershi.chat.service;


import com.ershi.chat.domain.RoomGroupEntity;
import com.ershi.chat.domain.vo.GroupCreateResp;
import com.mybatisflex.core.service.IService;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

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
}