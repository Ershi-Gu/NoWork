package com.ershi.chat.service.impl;


import com.ershi.chat.adapter.FriendAdapter;
import com.ershi.chat.domain.vo.FriendResp;
import com.ershi.common.domain.dto.CursorPageBaseReq;
import com.ershi.common.domain.vo.CursorPageBaseResp;
import com.ershi.common.utils.CursorUtils;
import com.ershi.user.service.cache.UserInfoCache;
import com.mybatisflex.core.query.QueryWrapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import com.ershi.chat.service.IUserFriendService;
import com.ershi.chat.domain.UserFriendEntity;
import com.ershi.chat.mapper.UserFriendMapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.ershi.chat.domain.table.UserFriendEntityTableDef.USER_FRIEND_ENTITY;


/**
 * 好友表 服务层实现。
 *
 * @author mybatis-flex-helper automatic generation
 * @since 1.0
 */
@Service
public class UserFriendServiceImpl extends ServiceImpl<UserFriendMapper, UserFriendEntity> implements IUserFriendService {

    @Resource
    private UserInfoCache userInfoCache;

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

    @Override
    public CursorPageBaseResp<FriendResp> getFriendPage(Long uid, CursorPageBaseReq cursorPageBaseReq) {
        // 获取游标翻页数据
        CursorPageBaseResp<UserFriendEntity> friendPage = CursorUtils.getCursorPageByMysql(this,
                cursorPageBaseReq, wrapper -> wrapper.where(USER_FRIEND_ENTITY.UID.eq(uid)),
                UserFriendEntity::getId, false);
        if (friendPage.isEmpty()) {
            return CursorPageBaseResp.empty();
        }

        // 根据好友数据聚合所有好友的uid
        List<Long> friendUidList = friendPage.getDataList()
                .stream()
                .map(UserFriendEntity::getFid)
                .collect(Collectors.toList());

        // 根据uid获取好友在线状态
        Map<Long, Boolean> isOnlineBatch = userInfoCache.isOnlineBatch(friendUidList);

        // 将数据转换成响应格式返回
        return CursorPageBaseResp.init(friendPage, FriendAdapter.buildFriendRespList(isOnlineBatch));

    }

}