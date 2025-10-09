package com.ershi.chat.service.cache;

import cn.hutool.core.collection.CollectionUtil;
import com.ershi.chat.domain.GroupMemberEntity;
import com.ershi.chat.mapper.GroupMemberMapper;
import com.ershi.common.constants.RedisKey;
import com.ershi.common.utils.JsonUtils;
import com.ershi.common.utils.RedisUtils;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 群成员缓存V2 - 使用Hash结构优化
 *
 * @author Ershi-Gu.
 * @since 2025-10-09
 */
@Service
public class GroupMemberCacheV2 {

    public static final Long GROUP_MEMBER_EXPIRE_SECONDS = 5 * 60L;

    @Resource
    private GroupMemberMapper groupMemberMapper;

    /**
     * 从数据库加载群成员列表并缓存到Redis
     *
     * @param roomId 房间id
     * @return 成员列表
     */
    private List<GroupMemberEntity> loadAndCache(Long roomId) {
        List<GroupMemberEntity> memberList = groupMemberMapper.getMemberListByRoomId(roomId);
        if (CollectionUtil.isEmpty(memberList)) {
            return Collections.emptyList();
        }
        
        // 将成员列表写入Hash缓存，uid作为field，实体转为json字符串作为value
        String cacheKey = buildRoomKey(roomId);
        Map<String, Object> cacheData = memberList.stream()
                .collect(Collectors.toMap(
                        member -> String.valueOf(member.getUid()),
                        member -> member
                ));
        RedisUtils.hmset(cacheKey, cacheData, GROUP_MEMBER_EXPIRE_SECONDS);
        
        return memberList;
    }

    /**
     * 构建房间维度的缓存key
     *
     * @param roomId 房间id
     * @return Redis key
     */
    private String buildRoomKey(Long roomId) {
        return RedisKey.getKey(RedisKey.ROOM_MEMBERS_BY_ROOM_ID_KEY, roomId);
    }

    /**
     * 获取群成员uid列表
     *
     * @param roomId 房间id
     * @return 成员uid列表
     */
    public List<Long> getMemberUidList(Long roomId) {
        String cacheKey = buildRoomKey(roomId);
        
        // 通过key获取hash缓存中所有的键值对 uid-member
        Map<Object, Object> memberMap = RedisUtils.hmget(cacheKey);
        if (!memberMap.isEmpty()) {
            RedisUtils.expire(cacheKey, GROUP_MEMBER_EXPIRE_SECONDS);
            return memberMap.keySet().stream()
                    .map(key -> Long.valueOf(key.toString()))
                    .collect(Collectors.toList());
        }
        
        // 缓存未命中，从数据库加载并缓存
        List<GroupMemberEntity> memberList = loadAndCache(roomId);
        return memberList.stream()
                .map(GroupMemberEntity::getUid)
                .collect(Collectors.toList());
    }

    /**
     * 批量获取群成员uid列表
     *
     * @param roomIdList 房间id列表
     * @return roomId -> uid列表的映射
     */
    public Map<Long, List<Long>> getMemberUidListBatch(List<Long> roomIdList) {
        if (CollectionUtil.isEmpty(roomIdList)) {
            return Collections.emptyMap();
        }
        
        Map<Long, List<Long>> resultMap = new HashMap<>(roomIdList.size());
        for (Long roomId : roomIdList) {
            List<Long> uidList = getMemberUidList(roomId);
            resultMap.put(roomId, uidList);
        }
        return resultMap;
    }

    /**
     * 获取单个群成员信息
     *
     * @param roomId 房间id
     * @param uid    用户id
     * @return 群成员信息
     */
    public GroupMemberEntity getMemberInfo(Long roomId, Long uid) {
        String cacheKey = buildRoomKey(roomId);
        String field = String.valueOf(uid);
        
        // 尝试从Hash缓存获取指定成员
        Object cachedMember = RedisUtils.hget(cacheKey, field);
        if (cachedMember != null) {
            RedisUtils.expire(cacheKey, GROUP_MEMBER_EXPIRE_SECONDS);
            return JsonUtils.toObj(cachedMember.toString(), GroupMemberEntity.class);
        }
        
        // 缓存未命中，从数据库加载并缓存整个群的成员列表
        List<GroupMemberEntity> memberList = loadAndCache(roomId);
        return memberList.stream()
                .filter(member -> member.getUid().equals(uid))
                .findFirst()
                .orElse(null);
    }

    /**
     * 删除指定房间的缓存
     *
     * @param roomId 房间id
     */
    public void delete(Long roomId) {
        RedisUtils.del(buildRoomKey(roomId));
    }

    /**
     * 批量删除房间缓存
     *
     * @param roomIdList 房间id列表
     */
    public void deleteBatch(List<Long> roomIdList) {
        if (CollectionUtil.isEmpty(roomIdList)) {
            return;
        }
        String[] keys = roomIdList.stream()
                .map(this::buildRoomKey)
                .toArray(String[]::new);
        RedisUtils.del(keys);
    }
}