package com.ershi.chat.websocket.handler;

import cn.hutool.json.JSONUtil;
import com.ershi.chat.websocket.domain.dto.WSBaseReq;
import com.ershi.chat.websocket.domain.enums.WSReqTypeEnum;
import com.ershi.chat.websocket.service.ChatWebSocketService;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Ershi-Gu.
 * @since 2025-09-08
 */
@Slf4j
@Setter
@ChannelHandler.Sharable
public class NettyWebSocketServerHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    /**
     * ws相关业务处理，NettyWebSocketServer注入
     */
    private ChatWebSocketService chatWebSocketService;

    /**
     * 连接建立后触发
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("新 websocket 连接建立，channelId:{}", ctx.channel().id());
        super.channelActive(ctx);
    }

    /**
     * 连接断开后触发
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        chatWebSocketService.offline(ctx.channel());
        log.info("websocket 连接断开，channelId:{}", ctx.channel().id());
    }

    /**
     * 客户端线程有事件时触发，用于心跳检查和握手认证
     *
     * @param ctx
     * @param evt
     * @throws Exception
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        // 心跳检查
        if (evt instanceof IdleStateEvent event) {
            IdleState state = event.state();
            // 指定时间内客户端没有发送读事件，视作挂机
            if (state == IdleState.READER_IDLE) {
                // 用户下线，关闭连接
//                ctx.channel().close();
            }
        }
        // 握手认证->聊天室身份认证
        else if (evt instanceof WebSocketServerProtocolHandler.HandshakeComplete) {
            chatWebSocketService.authorize(ctx.channel());
        }

    }

    /**
     * 接收到消息时触发该方法，根据消息类型进行不同业务处理
     *
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        // 获取数据
        String text = msg.text();
        WSBaseReq wsBaseReq = JSONUtil.toBean(text, WSBaseReq.class);

        switch (WSReqTypeEnum.of(wsBaseReq.getType())) {
            // 心跳
            case HEARTBEAT:
                break;
            // 发送消息
            case MESSAGE:
                chatWebSocketService.receiveChatMsg(ctx.channel(), wsBaseReq.getData());
            // todo 接收方回复ack
        }
    }
}
