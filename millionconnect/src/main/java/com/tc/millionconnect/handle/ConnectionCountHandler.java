package com.tc.millionconnect.handle;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 用来统计单位时间内的请求数，每接入一个连接则自增一个数字，每 2s 统计一次
 * @author woshi
 * @date 2021/3/9
 */
@ChannelHandler.Sharable
public class ConnectionCountHandler extends ChannelInboundHandlerAdapter {
    private AtomicInteger nConnection = new AtomicInteger();

    public ConnectionCountHandler(){
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(()->{
            System.out.println("当前客户端连接数: " + nConnection.get());
        }, 0, 2, TimeUnit.SECONDS);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        nConnection.incrementAndGet();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        nConnection.decrementAndGet();
    }
}