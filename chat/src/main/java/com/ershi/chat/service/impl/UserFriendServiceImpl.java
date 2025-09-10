package com.ershi.chat.service.impl;


import com.mybatisflex.core.query.QueryWrapper;
import org.springframework.stereotype.Service;
import com.ershi.chat.service.IUserFriendService;
import com.ershi.chat.domain.UserFriendEntity;
import com.ershi.chat.mapper.UserFriendMapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;

import java.util.List;

import static com.ershi.chat.domain.table.UserFriendEntityTableDef.USER_FRIEND_ENTITY;


/**
 * 好友表 服务层实现。
 *
 * @author mybatis-flex-helper automatic generation
 * @since 1.0
 */
@Service
public class UserFriendServiceImpl extends ServiceImpl<UserFriendMapper, UserFriendEntity> implements IUserFriendService {

    @Override
    public boolean isFriend(Long uid, Long targetUid) {
        return this.exists(QueryWrapper.create()
                .where(USER_FRIEND_ENTITY.UID.eq(uid).and(USER_FRIEND_ENTITY.FID.eq(targetUid))));
    }

    @Override
    public void createFriend(Long uid, Long targetId) {
        // 创建双向好友关系
        List<UserFriendEntity> friends = List.of(
                UserFriendEntity.builder().uid(uid).fid(targetId).build(),
                UserFriendEntity.builder().uid(targetId).fid(uid).build()
        );
        this.saveBatch(friends);
    }

}