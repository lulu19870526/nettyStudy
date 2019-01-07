package three;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 *
 * @author bazingaLyncc
 * 描述：客户端的第一个自定义的inbound处理器
 * 时间  2016年5月3日
 */
public class BaseClient1Handler extends ChannelInboundHandlerAdapter{

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("BaseClient1Handler channelActive");
        /**
         * 如果一个channelPipeline中有多个channelHandler时，
         * 且这些channelHandler中有同样的方法时，例如这里的channelActive方法，
         * 只会调用处在第一个的channelHandler中的channelActive方法，
         * 如果你想要调用后续的channelHandler的同名的方法就需要调用以“fire”为开头的方法
         */
        ctx.fireChannelActive();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("BaseClient1Handler channelInactive");
    }

}
