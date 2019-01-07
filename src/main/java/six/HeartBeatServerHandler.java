package six;


import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

/**
 * 1）服务器端每隔5秒检测服务器端的读超时，如果5秒没有接受到客户端的写请求，
 * 也就说服务器端5秒没有收到读事件，则视为一次超时
 * 2）如果超时二次则说明连接处于不活跃的状态，关闭ServerChannel
 * 3）客户端每隔4秒发送一些写请求，这个请求相当于一次心跳包，告之服务器端：客户端仍旧活着
 *
 * 自定义处理类Handler继承ChannlInboundHandlerAdapter，实现其userEventTriggered()方法，
 * 在出现超时事件时会被触发，包括读空闲超时或者写空闲超时；
 */
public class HeartBeatServerHandler extends ChannelInboundHandlerAdapter {

    private int loss_connect_time = 0;

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.READER_IDLE) {
                loss_connect_time++;
                System.out.println("5 秒没有接收到客户端的信息了");
                if (loss_connect_time > 2) {
                    System.out.println("关闭这个不活跃的channel");
                    ctx.channel().close();
                }
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("server channelRead..");
        System.out.println(ctx.channel().remoteAddress() + "->Server :" + msg.toString());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

}
