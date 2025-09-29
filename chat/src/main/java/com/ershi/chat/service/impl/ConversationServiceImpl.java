package com.ershi.chat.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Pair;
import com.ershi.chat.domain.RoomEntity;
import com.ershi.chat.domain.RoomFriendEntity;
import com.ershi.chat.domain.RoomGroupEntity;
import com.ershi.chat.domain.UserMsgInboxEntity;
import com.ershi.chat.domain.dto.C10nBaseInfo;
import com.ershi.chat.domain.dto.C10nUnreadCount;
import com.ershi.chat.domain.enums.RoomTypeEnum;
import com.ershi.chat.domain.message.MessageEntity;
import com.ershi.chat.domain.vo.ConversationResp;
import com.ershi.chat.mapper.MessageMapper;
import com.ershi.chat.service.IConversationService;
import com.ershi.chat.service.IUserMsgInboxService;
import com.ershi.chat.service.adapter.C10nAdapter;
import com.ershi.chat.service.cache.HotRoomCache;
import com.ershi.chat.service.cache.RoomCache;
import com.ershi.chat.service.cache.RoomFriendCache;
import com.ershi.chat.service.cache.RoomGroupCache;
import com.ershi.chat.service.handler.MsgReadHandler;
import com.ershi.common.constants.RedisKey;
import com.ershi.common.domain.dto.CursorPageBaseReq;
import com.ershi.common.domain.vo.CursorPageBaseResp;
import com.ershi.common.utils.CursorUtils;
import com.ershi.common.utils.RequestHolder;
import com.ershi.user.domain.entity.UserEntity;
import com.ershi.user.service.cache.UserInfoCache;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.ershi.chat.domain.table.UserMsgInboxEntityTableDef.USER_MSG_INBOX_ENTITY;

/**
 *
 * @author Ershi-Gu.
 * @since 2025-09-25
 */
@Slf4j
@Service
public class ConversationServiceImpl implements IConversationService {

    @Resource
    private IUserMsgInboxService userMsgInboxService;

    @Resource
    private HotRoomCache hotRoomCache;

    @Resource
    private RoomCache roomCache;

    @Resource
    private UserInfoCache userInfoCache;

    @Resource
    private MessageMapper messageMapper;

    @Resource
    private MsgReadHandler msgReadHandler;

    @Resource
    private RoomGroupCache roomGroupCache;

    @Resource
    private RoomFriendCache roomFriendCache;

    @Override
    public CursorPageBaseResp<ConversationResp> getConversationPage(CursorPageBaseReq cursorPageBaseReq) {
        Long uid = RequestHolder.get().getUid();

        CursorPageBaseResp<Long> resultC10nIdPage;

        if (uid != null) {
            // 登录态
            resultC10nIdPage = getRoomIdsOnLogin(cursorPageBaseReq, uid);
        } else {
            // 游客态
            resultC10nIdPage = getRoomIdsOnNoLogin(cursorPageBaseReq);
        }

        // 无会话返回空列表
        if (resultC10nIdPage.getDataList() == null || resultC10nIdPage.getDataList().isEmpty()) {
            return CursorPageBaseResp.empty();
        }

        // 获取侧边栏会话列表展示信息
        List<ConversationResp> conversationResps = batchBuildC10nResp(resultC10nIdPage.getDataList(), uid);

        return CursorPageBaseResp.init(resultC10nIdPage, conversationResps);
    }

    /**
     * 游客态获取全员群会话roomId List，会话活跃时间降序
     *
     * @param cursorPageBaseReq
     * @return {@link CursorPageBaseResp }<{@link Long }>
     */
    private CursorPageBaseResp<Long> getRoomIdsOnNoLogin(CursorPageBaseReq cursorPageBaseReq) {
        CursorPageBaseResp<Pair<Long, Double>> hotConversationPage = getHotC10nPage(cursorPageBaseReq);
        List<Long> roomIds = hotConversationPage.getDataList()
                .stream().map(Pair::getKey).collect(Collectors.toList());

        // 从中筛选出全员群房间id
        List<Long> allRoomIds = getAllRoom(roomIds);
        return CursorPageBaseResp.init(hotConversationPage, allRoomIds);

    }

    /**
     * 登录态翻页获取会话无序roomId List
     *
     * @param cursorPageBaseReq
     * @param uid
     * @return {@link CursorPageBaseResp }<{@link Long }>
     */
    private CursorPageBaseResp<Long> getRoomIdsOnLogin(CursorPageBaseReq cursorPageBaseReq, Long uid) {
        // 热点会话查询游标
        Double hotMaxMsgId = getDoubleCursorOrNull(cursorPageBaseReq.getCursor());

        // 查询用户收件箱，获取范围内非热点会话page。
        CursorPageBaseResp<UserMsgInboxEntity> notHotC10nPage =
                getNotHotC10nPage(cursorPageBaseReq, uid);

        // 查询热点信箱，获取范围内热点会话page。
        Set<ZSetOperations.TypedTuple<String>> hotC10nPage =
                hotRoomCache.getHotC10nPage(hotMaxMsgId, Long.valueOf(cursorPageBaseReq.getPageSize()));

        // 聚合热点群聊和基础会话的会话房间id信息-无序
        List<Long> baseRoomIds = notHotC10nPage.getDataList()
                .stream().map(UserMsgInboxEntity::getRoomId).collect(Collectors.toList());
        List<Long> hotRoomIds = hotC10nPage
                .stream().map(ZSetOperations.TypedTuple::getValue).filter(Objects::nonNull)
                .map(Long::parseLong).toList();
        baseRoomIds.addAll(hotRoomIds);

        return CursorPageBaseResp.init(notHotC10nPage, baseRoomIds);
    }

    /**
     * 查询非热点会话列表，通过msgId进行排序（全局唯一且递增），id大的在前（最新消息）。<br>
     * 该查询为向下滑动查询，即越往下滑消息越古老，查询完毕后更新游标为底部msgId
     *
     * @param cursorPageBaseReq
     * @param uid
     * @return {@link CursorPageBaseResp }<{@link UserMsgInboxEntity }>
     */
    private CursorPageBaseResp<UserMsgInboxEntity> getNotHotC10nPage(CursorPageBaseReq cursorPageBaseReq, Long uid) {
        return CursorUtils.getCursorPageByMysql(userMsgInboxService, cursorPageBaseReq,
                wrapper -> wrapper.where(USER_MSG_INBOX_ENTITY.UID.eq(uid)),
                UserMsgInboxEntity::getLastMsgId, false);
    }

    /**
     * 将字符串游标转换为Double类型
     *
     * @param cursor
     * @return {@link Double } or null
     */
    private Double getDoubleCursorOrNull(String cursor) {
        return Optional.ofNullable(cursor).map(Double::parseDouble).orElse(null);
    }

    /**
     * 查询热点会话列表，通过msgId进行排序（全局唯一且递增），id大的在前（最新消息）。<br>
     * 该查询为向下滑动查询，即越往下滑消息越古老，查询完毕后更新游标为底部msgId
     *
     * @param cursorPageBaseReq
     * @return {@link List }<{@link Long }>
     */
    private CursorPageBaseResp<Pair<Long, Double>> getHotC10nPage(CursorPageBaseReq cursorPageBaseReq) {
        return CursorUtils.getCursorPageByRedis(cursorPageBaseReq, RedisKey.getKey(RedisKey.HOT_ROOM_LAST_MSG_KEY),
                Long::parseLong);
    }

    /**
     * 从群聊中筛选出全员群聊
     *
     * @param roomIds
     * @return {@link List }<{@link Long }>
     */
    private List<Long> getAllRoom(List<Long> roomIds) {
        // 获取所有热点群聊信息
        Map<Long, RoomEntity> roomMap = roomCache.getBatch(roomIds);
        return roomMap.values().stream().filter(room -> Objects.equals(room.getType(),
                RoomTypeEnum.ALL.getType())).map(RoomEntity::getId).collect(Collectors.toList());
    }

    /**
     * 批量构造侧边栏会话列表返回
     *
     * @param roomIds
     * @param uid
     * @return {@link List }<{@link ConversationResp }>
     */
    private List<ConversationResp> batchBuildC10nResp(List<Long> roomIds, Long uid) {
        // 获取会话基本信息
        Map<Long, C10nBaseInfo> c10nBaseInfoMap = batchGetC10nBaseInfo(roomIds, uid);

        // 查询会话最新消息，用于会话侧边栏显示
        List<Long> lastMsgIds = c10nBaseInfoMap.values().stream().map(C10nBaseInfo::getLastMsgId)
                .collect(Collectors.toList());
        List<MessageEntity> lastMsgs = lastMsgIds.isEmpty() ?
                new ArrayList<>() : messageMapper.selectListByIds(lastMsgIds);

        // 获取最新消息发送者信息
        Map<Long, UserEntity> senderInfoMap =
                userInfoCache.getBatch(lastMsgs.stream().map(MessageEntity::getSenderId).toList());

        // 查询用户会话未读数
        List<C10nUnreadCount> c10nUnreadMsgCount = msgReadHandler.getC10nUnreadMsgCount(roomIds, uid);

        // 组装返回
        return C10nAdapter.batchBuildC10nResp(c10nBaseInfoMap, lastMsgs, senderInfoMap, c10nUnreadMsgCount);
    }

    /**
     * 构造会话基本信息
     *
     * @param roomIds
     * @param uid
     * @return {@link Map }<{@link Long }, {@link C10nBaseInfo }>
     */
    private Map<Long, C10nBaseInfo> batchGetC10nBaseInfo(List<Long> roomIds, Long uid) {
        // 获取room信息
        Map<Long, RoomEntity> roomEntityMap = roomCache.getBatch(roomIds);

        // 按照会话类型进行分类 roomType -> List<roomId>，全员群算群聊
        Map<Integer, List<Long>> groupRoomIdByRoomTypeMap = roomEntityMap.values().stream()
                .collect(Collectors.groupingBy(RoomEntity::getType,
                        Collectors.mapping(RoomEntity::getId, Collectors.toList())));

        // 获取群聊信息
        List<Long> groupRoomIds = Stream.of(
                        groupRoomIdByRoomTypeMap.get(RoomTypeEnum.ALL.getType()),
                        groupRoomIdByRoomTypeMap.get(RoomTypeEnum.GROUP.getType())
                ).filter(Objects::nonNull)
                .flatMap(List::stream)
                .collect(Collectors.toList());
        Map<Long, RoomGroupEntity> groupRoomInfoMap = roomGroupCache.getBatch(groupRoomIds);

        // 获取单聊对象信息
        List<Long> friendRoomIds = groupRoomIdByRoomTypeMap.get(RoomTypeEnum.FRIEND.getType());
        Map<Long, UserEntity> friendRoomInfoMap = getFriendRoomInfoMap(friendRoomIds, uid);

        // 按照会话活跃先后组装详情返回
        return C10nAdapter.buildC10nBaseInfo(roomEntityMap, groupRoomInfoMap, friendRoomInfoMap);
    }

    /**
     * 获取单聊房间基本信息
     *
     * @param friendRoomIds
     * @param uid
     * @return {@link Map }<{@link Long }, {@link UserEntity }> roomId-FriendUserInfo
     */
    private Map<Long, UserEntity> getFriendRoomInfoMap(List<Long> friendRoomIds, Long uid) {
        if (CollectionUtil.isEmpty(friendRoomIds)) {
            return new HashMap<>();
        }
        // 获取单聊房间信息
        Map<Long, RoomFriendEntity> roomFriendMap = roomFriendCache.getBatch(friendRoomIds);

        // 从单聊房间中获取好友的uid
        Set<Long> friendUidSet = C10nAdapter.batchGetFriendUids(roomFriendMap.values(), uid);

        // 查询好友详情-单聊房间头像、名称显示为对方的信息
        Map<Long, UserEntity> userBatch = userInfoCache.getBatch(new ArrayList<>(friendUidSet));

        // 组装返回
        return roomFriendMap.values()
                .stream()
                .collect(Collectors.toMap(RoomFriendEntity::getRoomId, roomFriend -> {
                    Long friendUid = C10nAdapter.getFriendUid(roomFriend, uid);
                    return userBatch.get(friendUid);
                }));
    }
}


