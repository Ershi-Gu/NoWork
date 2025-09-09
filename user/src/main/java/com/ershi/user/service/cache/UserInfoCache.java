package com.ershi.user.service.cache;

import com.ershi.common.cache.cache.AbstractRedisStringCache;
import com.ershi.common.constants.RedisKey;
import com.ershi.common.utils.RedisUtils;
import com.ershi.user.domain.entity.UserEntity;
import com.ershi.user.domain.vo.UserLoginVO;
import com.ershi.user.mapper.UserMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;


/**
 * 登录用户信息缓存
 *
 * @author Ershi-Gu.
 * @since 2025-09-09
 */
@Service
public class UserInfoCache extends AbstractRedisStringCache<Long, UserEntity> {

    @Resource
    private UserMapper userMapper;

    private final static Long EXPIRE_SECONDS_TIME = 5 * 60L;

    @Override
    protected String getKey(Long uid) {
        return RedisKey.getKey(RedisKey.LOGIN_USER_INFO_KEY, uid);
    }

    @Override
    protected Long getExpireSeconds() {
        return EXPIRE_SECONDS_TIME;
    }

    @Override
    protected Map<Long, UserEntity> load(List<Long> uidList) {
        // fromDB
        List<UserEntity> userEntityList = userMapper.selectListByIds(uidList);
        return userEntityList.stream().collect(Collectors.toMap(UserEntity::getId, Function.identity()));
    }

    /**
     * 用户上线更新在线用户表
     *
     * @param uid
     * @param lastLoginTime
     */
    public void online(Long uid, LocalDateTime lastLoginTime) {
        // 移除离线表
        RedisUtils.zRemove(RedisKey.getKey(RedisKey.OFFLINE_UID_ZET), uid);
        // 更新在线表
        RedisUtils.zAdd(RedisKey.getKey(RedisKey.ONLINE_UID_ZET), uid,
                lastLoginTime.atZone(ZoneId.systemDefault()).toEpochSecond());
    }

    /**
     * 用户下线更新在线用户表
     *
     * @param uid
     * @param lastLoginTime
     */
    public void offline(Long uid, LocalDateTime lastLoginTime) {
        // 移除在线表
        RedisUtils.zRemove(RedisKey.getKey(RedisKey.ONLINE_UID_ZET), uid);
        // 更新离线表
        RedisUtils.zAdd(RedisKey.getKey(RedisKey.OFFLINE_UID_ZET), uid,
                lastLoginTime.atZone(ZoneId.systemDefault()).toEpochSecond());
    }
}
