package com.ershi.chat.service.impl;

import cn.hutool.core.lang.Pair;
import com.ershi.chat.domain.RoomEntity;
import com.ershi.chat.domain.UserMsgInboxEntity;
import com.ershi.chat.domain.enums.RoomTypeEnum;
import com.ershi.chat.domain.message.MessageEntity;
import com.ershi.chat.domain.vo.ConversationResp;
import com.ershi.chat.mapper.MessageMapper;
import com.ershi.chat.service.IConversationService;
import com.ershi.chat.service.IUserMsgInboxService;
import com.ershi.chat.service.cache.HotRoomCache;
import com.ershi.chat.service.cache.RoomCache;
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

    @Override
    public CursorPageBaseResp<ConversationResp> getConversationPage(CursorPageBaseReq cursorPageBaseReq) {
        Long uid = RequestHolder.get().getUid();

        List<Long> roomIds;

        if (uid != null) {
            // 登录态
            roomIds = getRoomIdsOnLogin(cursorPageBaseReq, uid);
        } else {
            // 游客态
            roomIds = getRoomIdsOnNoLogin(cursorPageBaseReq);
        }

        // 无会话返回空列表
        if (roomIds == null || roomIds.isEmpty()) {
            return CursorPageBaseResp.empty();
        }

        // 获取侧边栏会话列表展示信息
        List<ConversationResp> conversationResps = batchBuildConversationResp(roomIds);

        return null;
    }

    /**
     * 游客态获取全员群会话roomId List，会话活跃时间降序
     *
     * @param cursorPageBaseReq
     * @return {@link List }<{@link Long }>
     */
    private List<Long> getRoomIdsOnNoLogin(CursorPageBaseReq cursorPageBaseReq) {
        CursorPageBaseResp<Pair<Long, Double>> hotConversationPage = getHotConversationPage(cursorPageBaseReq);
        List<Long> roomIds = hotConversationPage.getDataList()
                .stream().map(Pair::getKey).collect(Collectors.toList());

        // 从中筛选出全员群房间id
        return getAllRoom(roomIds);

    }

    /**
     * 登录态翻页获取会话无序roomId List
     *
     * @param cursorPageBaseReq
     * @param uid
     * @return {@link List }<{@link Long }>
     */
    private List<Long> getRoomIdsOnLogin(CursorPageBaseReq cursorPageBaseReq, Long uid) {
        // 热点会话查询游标
        Double hotMaxMsgId = getDoubleCursorOrNull(cursorPageBaseReq.getCursor());

        // 查询用户收件箱，获取范围内非热点会话page。
        CursorPageBaseResp<UserMsgInboxEntity> notHotConversationPage =
                getNotHotConversationPage(cursorPageBaseReq, uid);

        // 查询热点信箱，获取范围内热点会话page。
        Set<ZSetOperations.TypedTuple<String>> hotConversionPage =
                hotRoomCache.getHotConversationPage(hotMaxMsgId, Long.valueOf(cursorPageBaseReq.getPageSize()));

        // 聚合热点群聊和基础会话的会话房间id信息-无序
        List<Long> baseRoomIds = notHotConversationPage.getDataList()
                .stream().map(UserMsgInboxEntity::getRoomId).collect(Collectors.toList());
        List<Long> hotRoomIds = hotConversionPage
                .stream().map(ZSetOperations.TypedTuple::getValue).filter(Objects::nonNull).map(Long::parseLong).toList();
        baseRoomIds.addAll(hotRoomIds);

        return baseRoomIds;
    }

    /**
     * 查询非热点会话列表，通过msgId进行排序（全局唯一且递增），id大的在前（最新消息）。<br>
     * 该查询为向下滑动查询，即越往下滑消息越古老，查询完毕后更新游标为底部msgId
     *
     * @param cursorPageBaseReq
     * @param uid
     * @return {@link CursorPageBaseResp }<{@link UserMsgInboxEntity }>
     */
    private CursorPageBaseResp<UserMsgInboxEntity> getNotHotConversationPage(CursorPageBaseReq cursorPageBaseReq, Long uid) {
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
    private CursorPageBaseResp<Pair<Long, Double>> getHotConversationPage(CursorPageBaseReq cursorPageBaseReq) {
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

    // todo batchBuildConversationResp
    private List<ConversationResp> batchBuildConversationResp(List<Long> roomIds) {
        // 获取会话基本信息
        Map<Long, RoomEntity> roomEntityMap = roomCache.getBatch(roomIds);

        // 查询会话最新消息，用于会话侧边栏显示
        List<Long> lastMsgIds = roomEntityMap.values().stream().map(RoomEntity::getLastMsgId).collect(Collectors.toList());
        List<MessageEntity> messageEntityList = lastMsgIds.isEmpty() ? new ArrayList<>() : messageMapper.selectListByIds(lastMsgIds);

        // 获取最新消息发送者信息
        Map<Long, UserEntity> senderInfoMap =
                userInfoCache.getBatch(messageEntityList.stream().map(MessageEntity::getSenderId).toList());

        // 查询用户会话未读数


        // 组装返回
        return null;
    }
}
