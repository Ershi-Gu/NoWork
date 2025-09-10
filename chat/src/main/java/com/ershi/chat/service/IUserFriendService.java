package com.ershi.chat.service;


import com.ershi.chat.domain.UserFriendEntity;
import com.mybatisflex.core.service.IService;

/**
 * 好友表 服务层。
 *
 * @author mybatis-flex-helper automatic generation
 * @since 1.0
 */
public interface IUserFriendService extends IService<UserFriendEntity> {

    /**
     * 判断是否是好友
     *
     * @param uid
     * @param targetUid
     * @return {@link Boolean }
     */
    boolean isFriend(Long uid, Long targetUid);

    /**
     * 创建好友关系
     *
     * @param uid
     * @param targetId
     */
    void createFriend(Long uid, Long targetId);
}