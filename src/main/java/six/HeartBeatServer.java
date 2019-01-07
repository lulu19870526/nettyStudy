package six;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

/**
 * 心跳机制
 * ﻿心跳是在TCP长连接中，客户端和服务端定时向对方发送数据包通知对方自己还在线，
 * 保证连接的有效性的一种机制
 *
 * 在服务器和客户端之间一定时间内没有数据交互时, 即处于 idle 状态时,
 * 客户端或服务器会发送一个特殊的数据包给对方, 当接收方收到这个数据报文后,
 * 也立即发送一个特殊的数据报文, 回应发送方, 此即一个 PING-PONG 交互. 自然地,
 * 当某一端收到心跳消息后, 就知道了对方仍然在线, 这就确保 TCP 连接的有效性.
 *
 * 心跳实现
 * 使用TCP协议层的Keeplive机制，但是该机制默认的心跳时间是2小时，依赖操作系统实现不够灵活；
 * 应用层实现自定义心跳机制，比如Netty实现心跳机制；
 */


/**
 * 服务端添加IdleStateHandler心跳检测处理器，并添加自定义处理Handler类实现userEventTriggered()方法作为超时事件的逻辑处理；
 * 设定IdleStateHandler心跳检测每五秒进行一次读检测，如果五秒内ChannelRead()方法未被调用则触发一次userEventTrigger()方法
 */
public class HeartBeatServer {

    private int port;

    public HeartBeatServer(int port) {
        this.port = port;
    }

    public void start(){
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap sbs = new ServerBootstrap()
                    .group(bossGroup,workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .localAddress(new InetSocketAddress(port))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        protected void initChannel(SocketChannel ch) throws Exception {
                            //每隔5秒检测一下服务端的读超时
                            /**
                             * readerIdleTime读空闲超时时间设定，如果channelRead()方法超过readerIdleTime时间未被调用则会触发超时事件调用userEventTrigger()方法；
                             * writerIdleTime写空闲超时时间设定，如果write()方法超过writerIdleTime时间未被调用则会触发超时事件调用userEventTrigger()方法；
                             * allIdleTime所有类型的空闲超时时间设定，包括读空闲和写空闲；
                             * unit时间单位，包括时分秒等；
                             */
                            ch.pipeline().addLast(new IdleStateHandler(5, 0, 0, TimeUnit.SECONDS));
                            ch.pipeline().addLast("decoder", new StringDecoder());
                            ch.pipeline().addLast("encoder", new StringEncoder());
                            ch.pipeline().addLast(new HeartBeatServerHandler());
                        };

                    }).option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);
            // 绑定端口，开始接收进来的连接
            ChannelFuture future = sbs.bind(port).sync();

            System.out.println("Server start listen at " + port );
            future.channel().closeFuture().sync();
        } catch (Exception e) {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception {
        int port;
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        } else {
            port = 8080;
        }
        new HeartBeatServer(port).start();
    }

}
