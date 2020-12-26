package com.pack.p2p;
 
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
/**
 * 客户点消息处理 Handler
 * @author zhang
 *
 */
public class ClientMessageHandler extends SimpleChannelInboundHandler<String> {
 
    /**
     * 处理收到的消息
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg)
            throws Exception {
        System.out.println(msg);
         
    }
    /**
     * 连接异常后触发
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        ctx.close();
         
    }
}