package four.chaibao;


import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;

import java.net.InetSocketAddress;


/**
 * 单次发送的包内容过多的情况，出现拆包的现象
 * 客户端发送一个报文，而服务端分两次接收
 *
 * 粘包和拆包的问题，归根结底的解决方案就是发送端给远程端一个标记，
 * 告诉远程端，每个信息的结束标志是什么，这样，远程端获取到数据后，
 * 根据跟发送端约束的标志，将接收的信息分切或者合并成我们需要的信息，
 * 这样我们就可以获取到正确的信息了
 *
 * 在发送的信息中，加一个结束标志，例如两个远程端规定以行来切分数据，
 * 那么发送端，就需要在每个信息体的末尾加上行结束的标志
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
                            //我们发送的一行信息大小是1076，大于了我们规定的1024会报错
                           //ch.pipeline().addLast(new LineBasedFrameDecoder(1024));
                            //它是继承ByteToMessageDecoder的，是将byte类型转化成Message的，所以我们应该将这个解码器放在inbound处理器链的第一个
                            ch.pipeline().addLast(new LineBasedFrameDecoder(2048));
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
