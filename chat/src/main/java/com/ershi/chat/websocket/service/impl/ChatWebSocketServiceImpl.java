package com.ershi.chat.websocket.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson2.JSON;
import com.ershi.chat.service.IMessageService;
import com.ershi.chat.websocket.domain.dto.ChatMsgReq;
import com.ershi.chat.websocket.domain.dto.WSChannelExtraDTO;
import com.ershi.chat.websocket.domain.enums.UserActiveTypeEnum;
import com.ershi.chat.websocket.domain.enums.WSRespTypeEnum;
import com.ershi.chat.websocket.domain.vo.CMReceiveAckResp;
import com.ershi.chat.websocket.domain.vo.WSBaseResp;
import com.ershi.chat.websocket.domain.vo.WSErrorResp;
import com.ershi.chat.websocket.event.UserOfflineEvent;
import com.ershi.chat.websocket.service.ChatWebSocketService;
import com.ershi.chat.websocket.utils.NettyUtil;
import com.ershi.common.exception.BusinessErrorEnum;
import com.ershi.common.exception.BusinessException;
import com.ershi.user.domain.entity.UserEntity;
import com.ershi.user.domain.vo.UserLoginVO;
import com.ershi.chat.websocket.event.UserOnlineEvent;
import com.ershi.user.mapper.UserMapper;
import com.mybatisflex.core.query.QueryWrapper;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;

import static com.ershi.user.domain.entity.table.UserEntityTableDef.USER_ENTITY;

/**
 * 聊天室websocket业务服务
 *
 * @author Ershi-Gu.
 * @since 2025-09-08
 */
@Slf4j
@Service
public class ChatWebSocketServiceImpl implements ChatWebSocketService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private IMessageService messageService;

    @Resource
    private Executor websocketVirtualExecutor;

    @Resource
    private ApplicationEventPublisher applicationEventPublisher;

    /**
     * 【活跃连接】包含游客以及已登录用户连接，游客连接没有uid。记录已登录用户连接是为了通过channel快速获取到uid，用于后续offline
     */
    private static final ConcurrentHashMap<Channel, WSChannelExtraDTO> ACTIVE_CHANNELS_MAP = new ConcurrentHashMap<>();

    /**
     * 【在线用户连接】携带token的用户连接时进行authorize，认证成功后保存uid->channels的连接，一个uid可以对应多个channel，允许多端同时在线
     */
    private static final ConcurrentHashMap<Long, CopyOnWriteArrayList<Channel>> ONLINE_USER_CHANNELS_MAP = new ConcurrentHashMap<>();

    // 3. 认证成功后，更新在线用户表【redis实现】，发送用户上线通知给所有已存在channel 【推送用户上线通知】
    // 4. 连接断开后，删除游客连接，若已登录，同时删除在线用户连接
    // 5. 连接断开后，发送用户下线通知

    /**
     * 授权聊天室用户信息
     *
     * @param channel
     */
    @Override
    public void authorize(Channel channel) {
        // 1. 获取token
        String token = NettyUtil.getAttr(channel, NettyUtil.TOKEN);
        Long loginIdByToken = Optional.ofNullable(StpUtil.getLoginIdByToken(token))
                .map(Object::toString)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(Long::valueOf)
                .orElse(null);

        // 2. token无效 -> 保存为游客连接
        if (loginIdByToken == null) {
            ACTIVE_CHANNELS_MAP.put(channel, new WSChannelExtraDTO());
            return;
        }

        // 3. token有效 -> 获取当前登录用户信息
        UserEntity loginUser = userMapper.selectOneByQuery(QueryWrapper.create().where(USER_ENTITY.ID.eq(loginIdByToken)));

        // 3.1 用户上线，更新信息，发送事件
        UserLoginVO userLoginVO = online(channel, loginUser);

        // 3.2 推送前端授权成功后的用户信息
        sendMsg(channel, WSBaseResp.build(WSRespTypeEnum.AUTHORIZE_SUCCESS.getType(), userLoginVO));
    }

    /**
     * 用户上线，更新在线状态
     *
     * @param loginUser
     * @return {@link UserEntity }
     */
    @Override
    public UserLoginVO online(Channel channel, UserEntity loginUser) {
        // 更新活跃连接池，记录uid
        WSChannelExtraDTO wsChannelExtraDTO = ACTIVE_CHANNELS_MAP.computeIfAbsent(channel, k -> new WSChannelExtraDTO());
        wsChannelExtraDTO.setUid(loginUser.getId());

        // 更新在线用户连接池
        Long uid = loginUser.getId();
        ONLINE_USER_CHANNELS_MAP.putIfAbsent(uid, new CopyOnWriteArrayList<>());
        ONLINE_USER_CHANNELS_MAP.get(uid).add(channel);
        NettyUtil.setAttr(channel, NettyUtil.UID, uid);

        // 更新最近上线时间
        loginUser.setLastLoginTime(LocalDateTime.now());
        // 更新在线状态
        loginUser.setActiveStatus(UserActiveTypeEnum.ONLINE.getType());

        UserLoginVO userLoginVO = UserLoginVO.objectToVO(loginUser);

        // 发送上线事件
        applicationEventPublisher.publishEvent(new UserOnlineEvent(this, loginUser, userLoginVO));
        return userLoginVO;
    }

    /**
     * 用户下线，清除连接池，并更新在线状态
     *
     * @param channel
     */
    @Override
    public void offline(Channel channel) {
        // 通过channel找到对应uid
        WSChannelExtraDTO wsChannelExtraDTO = ACTIVE_CHANNELS_MAP.get(channel);
        Optional<Long> uidOptional = Optional.ofNullable(wsChannelExtraDTO)
                .map(WSChannelExtraDTO::getUid);

        // 用于判断用户是否多端全部下线
        boolean offlineAll = true;

        // 删除当前活跃连接
        ACTIVE_CHANNELS_MAP.remove(channel);

        // 若当前连接已身份认证
        if (uidOptional.isPresent()) {
            // 获取uid对应的所有连接
            CopyOnWriteArrayList<Channel> channels = ONLINE_USER_CHANNELS_MAP.get(uidOptional.get());
            // 删除当前连接
            if (CollectionUtil.isNotEmpty(channels)) {
                channels.removeIf(ch -> Objects.equals(ch, channel));
            }
            // 若uid还有其他对应连接，则说明还有端在线，不在下线范围内
            if (!CollectionUtil.isEmpty(ONLINE_USER_CHANNELS_MAP.get(uidOptional.get()))) {
                offlineAll = false;
            }
            ;
        }

        // 若全下线则发出用户下线事件
        if (uidOptional.isPresent() && offlineAll) {
            // 更新用户在线状态
            UserEntity user = new UserEntity();
            user.setId(uidOptional.get());
            user.setActiveStatus(UserActiveTypeEnum.OFFLINE.getType());
            user.setLastLoginTime(LocalDateTime.now());

            // 发出用户下线事件
            applicationEventPublisher.publishEvent(new UserOfflineEvent(this, user));
        }
    }

    @Override
    public void receiveChatMsg(Channel channel, String data) {
        // 转换消息dto
        ChatMsgReq chatMsgReq;
        try {
            chatMsgReq = JSON.parseObject(data, ChatMsgReq.class);
        } catch (Exception e) {
            throw new BusinessException(BusinessErrorEnum.MSG_FORMAT_ERROR);
        }

        // 判断用户是否合法登录
        String token = NettyUtil.getAttr(channel, NettyUtil.TOKEN);
        if (token.isEmpty()) {
            // 构建ws错误返回
            WSErrorResp wsErrorResp = WSErrorResp.build(BusinessErrorEnum.USER_NOT_LOGIN_ERROR.getErrorMsg());
            sendMsg(channel, WSBaseResp.build(WSRespTypeEnum.ERROR.getType(), wsErrorResp));
            return;
        }

        // 虚拟线程异步处理
        websocketVirtualExecutor.execute(() -> {
            // 调用chat服务持久化数据并发送消息
            messageService.sendMultiTypeMessage(chatMsgReq);
            // 回复客户端ack -> 消息已接收
            sendMsg(channel, WSBaseResp.build(WSRespTypeEnum.RECEIVE_ACK.getType(),
                    CMReceiveAckResp.build(chatMsgReq.getClientMsgId())));
        });
    }

    /**
     * ws主动推送消息
     *
     * @param channel
     * @param msg     websocket 后端推送返回体
     */
    private <T> void sendMsg(Channel channel, WSBaseResp<T> msg) {
        channel.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(msg)));
    }
}
