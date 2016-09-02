package com.aexit.driver.device.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

/**
 * http://my.oschina.net/waylau/blog/380957
 */
public class ServerHandler extends SimpleChannelInboundHandler<String> { // (1)

    public static ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    /**
     * 函数当连接建立的时候被调用
     * @param ctx
     * @throws Exception
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        Channel incoming = ctx.channel();
        for (Channel channel : channels) {
            channel.writeAndFlush("[SERVER] - " + incoming.remoteAddress() + " 加入\n");
        }
        channels.add(ctx.channel());
    }

    /**
     * 当连接被断开的时候被调用
     * @param ctx
     * @throws Exception
     */
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {  // (3)
        Channel incoming = ctx.channel();
        for (Channel channel : channels) {
            channel.writeAndFlush("[SERVER] - " + incoming.remoteAddress() + " 离开\n");
        }
        channels.remove(ctx.channel());
    }

    /**
     * 对接收到的信息进行处理
     * @param ctx
     * @param s
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String s) throws Exception { // (4)
        Channel incoming = ctx.channel();
        for (Channel channel : channels) {
            if (channel != incoming){
                channel.writeAndFlush("[" + incoming.remoteAddress() + "]" + s + "\n");
            } else {
                channel.writeAndFlush("[you]" + s + "\n");
            }
        }
    }

    /**
     * 服务端监听到客户端处于活动状态时，被调用
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception { // (5)
        Channel incoming = ctx.channel();
        System.out.println("ClientStarter:"+incoming.remoteAddress()+"在线");
    }

    /**
     * 服务端监听到客户端处于不活动状态时，被调用
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception { // (6)
        Channel incoming = ctx.channel();
        System.out.println("ClientStarter:"+incoming.remoteAddress()+"掉线");
    }

    /**
     * 由于 IO 错误或者处理器在处理事件时抛出的异常时
     * @param ctx
     * @param cause
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) { // (7)
        Channel incoming = ctx.channel();
        System.out.println("ClientStarter:"+incoming.remoteAddress()+"异常");
        // 当出现异常就关闭连接
        cause.printStackTrace();
        ctx.close();
    }
}