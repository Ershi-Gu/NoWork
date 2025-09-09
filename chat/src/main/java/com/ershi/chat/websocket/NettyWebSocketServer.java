package com.ershi.chat.websocket;

import cn.hutool.extra.spring.SpringUtil;
import com.ershi.chat.websocket.handler.HttpHeaderHandler;
import com.ershi.chat.websocket.handler.NettyWebSocketServerHandler;
import com.ershi.chat.websocket.service.ChatWebSocketService;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.NettyRuntime;
import io.netty.util.concurrent.Future;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;


/**
 * Netty WebSocket 服务端
 *
 * @author Ershi
 * @date 2024/11/24
 */
@Configuration
@Slf4j
public class NettyWebSocketServer {

    /**
     * websocket 连接端口
     */
    @Value("${netty.websocket.port:18090}")
    private Integer webSocketPort;

    /**
     * 请求头处理器（全局共享）
     */
    private static final HttpHeaderHandler HTTP_HEADER_HANDLER = new HttpHeaderHandler();

    /**
     * 自定义业务处理器（全局共用）
     */
    private static final NettyWebSocketServerHandler NETTY_WEB_SOCKET_SERVER_HANDLER = new NettyWebSocketServerHandler();

    /**
     * 负责接受客户端的连接，单一线程即可完成
     */
    private final EventLoopGroup bossGroup = new NioEventLoopGroup(1);

    /**
     * 负责读写，线程设置为cpu核心数一样
     */
    private final EventLoopGroup workGroup = new NioEventLoopGroup(NettyRuntime.availableProcessors());

    /**
     * 启动 websocket server（在依赖注入完成后自动执行）
     *
     * @throws InterruptedException
     */
    @PostConstruct
    public void start() throws InterruptedException {
        NETTY_WEB_SOCKET_SERVER_HANDLER.setChatWebSocketService(SpringUtil.getBean(ChatWebSocketService.class));
        run();
    }

    /**
     * ws server 销毁方法（在spring应用停止前执行）
     */
    @PreDestroy
    public void destroy() {
        Future<?> boosFuture = bossGroup.shutdownGracefully();
        Future<?> workFuture = workGroup.shutdownGracefully();
        boosFuture.syncUninterruptibly();
        workFuture.syncUninterruptibly();
        log.info("关闭 WebSocket Server:{} 成功", webSocketPort);
    }

    /**
     * websocket server 启动主方法
     *
     * @throws InterruptedException
     */
    public void run() throws InterruptedException {
        // 创建 netty 服务器引导对象
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        // 配置引导对象
        serverBootstrap.group(bossGroup, workGroup)
                .channel(NioServerSocketChannel.class)
                // 设置服务器端接受连接的队列大小为128，在一个TCP连接被创建但还未被accept前，它会进入到内核的连接等待队列中。
                // 如果这个队列的大小超过了指定的值，新连接将会被拒绝。
                .option(ChannelOption.SO_BACKLOG, 128)
                // 用于设置长时间没有数据交互的TCP连接是否需要发送心跳包（KeepAlive packet）来检测连接的状态
                .option(ChannelOption.SO_KEEPALIVE, true)
                // 为服务端 bossGroup 设置日志处理器，用于记录客户端连接等信息
                .handler(new LoggingHandler(LogLevel.INFO))
                // 为客户端连接设置处理器链
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel channel) throws Exception {
                        ChannelPipeline pipeline = channel.pipeline();
                        /**
                         * 1. 30秒客户端没有向服务器发送心跳则关闭连接
                         * 2. IdleStateHandler能够侦测连接空闲状态
                         * 3. 第一个参数表示客户端多少秒没有读操作时触发事件，第二个是写操作(服务端是否推送)，第三个是读写操作都算，0表示禁用
                         */
                        pipeline.addLast(new IdleStateHandler(30, 0, 0));
                        // 因为使用http协议，所以需要使用http的复合编码器解码器
                        pipeline.addLast(new HttpServerCodec());
                        // 数据采用逐块发送到通道，可以处理大文件
                        pipeline.addLast(new ChunkedWriteHandler());
                        // Netty 中http数据在传输过程中是分段的，请求头和请求体会分开接受，HttpObjectAggregator可以把多个段聚合起来；
                        pipeline.addLast(new HttpObjectAggregator(8192));
                        // http请求头处理器，获取token进行握手认证
                        pipeline.addLast(HTTP_HEADER_HANDLER);
                        /**
                         * 说明：
                         *  1. 该处理器用于升级 HTTP 协议到 WebSocket
                         *  2. 对于 WebSocket，它的数据是以帧形式传递的；
                         *  3. 浏览器发送请求时： ws://localhost:8090/hello 表示请求的uri
                         *  4. WebSocketServerProtocolHandler 核心功能是把 http协议升级为 ws 协议，保持长连接；
                         *    是通过一个状态码 101 来切换的
                         *  5. websocketPath 表示该路径下的请求都走 websocket 协议，这里使用 "/" 表示所有请求都走
                         */
                        pipeline.addLast(new WebSocketServerProtocolHandler("/"));
                        // 自定义 WebSocketHandler，用于处理业务，所有连接共用一个业务处理器即可，可以大大减少资源消耗
                        pipeline.addLast(NETTY_WEB_SOCKET_SERVER_HANDLER);
                    }
                });

        // 启动服务器，监听端口，阻塞直到启动成功
        serverBootstrap.bind(webSocketPort).sync();
    }
}
