package com.pack.p2p;
 
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
 
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
/**
 * 自定义 服务器端消息处理Handler
 * @author zhang
 *
 */
public class ServerMessageHandler extends SimpleChannelInboundHandler<String>{
    /**
     * 管理全局的channel
     * GlobalEventExecutor.INSTANCE 全局事件监听器
     * 一旦将channel 加入 ChannelGroup 就不要用手动去
     * 管理channel的连接失效后移除操作，他会自己移除
     */
    private static ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    /**
     * 为了实现私聊功能，这里key存储用户的唯一标识，
     * 我保存 客户端的端口号
     * 当然 这个集合也需要自己去维护 用户的上下线 不能像 ChannelGroup那样自己去维护
     */
    private static Map<String,Channel> all = new HashMap<String,Channel>();
     
    private SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    /**
     * 处理收到的消息
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg)
            throws Exception {
        Channel channel = ctx.channel();
        /**
         * 这里简单判断 如果内容里边包含#那么就是私聊
         */
        if(msg.contains("#")){
            String id = msg.split("#")[0];
            String body = msg.split("#")[1];
            Channel userChannel = all.get(id);
            String key = channel.remoteAddress().toString().split(":")[1];
            userChannel.writeAndFlush(sf.format(new Date())+"\n 【用户】 "+key+" 说 : "+body);
            return;
        }
         
        //判断当前消息是不是自己发送的
        for(Channel c : channels){
            String addr = c.remoteAddress().toString();
            if(channel !=c){
                c.writeAndFlush(sf.format(new Date())+"\n 【用户】 "+addr+" 说 : "+msg);
            }else{
                c.writeAndFlush(sf.format(new Date())+"\n 【自己】 "+addr+" 说 : "+msg);
            }
        }
         
    }
    /**
     * 建立连接以后第一个调用的方法
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        String addr = channel.remoteAddress().toString();
        /**
         * 这里 ChannelGroup 底层封装会遍历给所有的channel发送消息
         * 
         */
        channels.writeAndFlush(sf.format(new Date())+"\n 【用户】 "+addr+" 加入聊天室 ");
        channels.add(channel);
        String key = channel.remoteAddress().toString().split(":")[1];
        all.put(key, channel);
    }
    /**
     * channel连接状态就绪以后调用
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        String addr = ctx.channel().remoteAddress().toString();
        System.out.println(sf.format(new Date())+" \n【用户】 "+addr+" 上线 ");
    }
    /**
     * channel连接状态断开后触发
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        String addr = ctx.channel().remoteAddress().toString();
        System.out.println(sf.format(new Date())+" \n【用户】 "+addr+" 下线 ");
        //下线移除
        String key = ctx.channel().remoteAddress().toString().split(":")[1];
        all.remove(key);
    }
    /**
     * 连接发生异常时触发
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        //System.out.println("连接发生异常！");
        ctx.close();
    }
    /**
     * 断开连接会触发该消息
     * 同时当前channel 也会自动从ChannelGroup中被移除
     */
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        String addr = channel.remoteAddress().toString();
        /**
         * 这里 ChannelGroup 底层封装会遍历给所有的channel发送消息
         * 
         */
        channels.writeAndFlush(sf.format(new Date())+"\n 【用户】 "+addr+" 离开了 ");
        //打印 ChannelGroup中的人数
        System.out.println("当前在线人数是:"+channels.size());
        System.out.println("all："+all.size());
    }
 
     
}