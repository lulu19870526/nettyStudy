package four.nianbao;


import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.FixedLengthFrameDecoder;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;

import java.net.InetSocketAddress;


/**
 * 客户端发送100次的信息，被服务器端分三次就接收了，这就发生了粘包
 *
 * 我们发送了100次的信息“BazingaLyncc is learner”，
 * 这个案例很特殊，这个信息是一个特长的数据，字节长度是23，
 * 所以我们可以使用Netty为我们提供的FixedLengthFrameDecoder这个解码器，
 * 看到这个名字就明白了大半，定长数据帧的解码器
 */
public class BaseServer {

    private int port;

    public BaseServer(int port) {
        this.port = port;
    }

    public void start(){
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap sbs = new ServerBootstrap()
                    .group(bossGroup,workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .localAddress(new InetSocketAddress(port))
                    .childHandler(new ChannelInitializer<SocketChannel>() {

                        protected void initChannel(SocketChannel ch) throws Exception {
                            /**
                             * 在channelhandler链中，加入了FixedLengthFrameDecoder，且参数是23，告诉Netty，获取的帧数据有23个字节就切分一次
                             */
                            ch.pipeline().addLast(new FixedLengthFrameDecoder(23));
                            ch.pipeline().addLast(new StringDecoder());
                            ch.pipeline().addLast(new BaseServerHandler());
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
        new BaseServer(port).start();
    }
}
