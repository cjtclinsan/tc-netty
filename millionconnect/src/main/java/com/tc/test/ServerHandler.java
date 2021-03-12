package com.tc.test;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.concurrent.ThreadLocalRandom;

/**
 * @author woshi
 * @date 2021/3/10
 */
public class ServerHandler extends SimpleChannelInboundHandler<ByteBuf> {
    public static final ChannelHandler INSTANCE = new ServerHandler();

    /**
     * channelRead0 是主线程
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
        ByteBuf data = Unpooled.directBuffer();
        // 从客户端读取一个时间戳
        data.writeBytes(msg);
        // 模拟一次业务处理  有可能是数据库操作，也有可能是逻辑处理
        Object result = getResult(data);
        // 重新写回客户端
        ctx.channel().writeAndFlush(result);
    }


    /**
     * 模拟从数据库获取一个结果
     */
    private Object getResult(ByteBuf data) {
        int level = ThreadLocalRandom.current().nextInt(1, 1000);

        // 计算出每次响应需要的时间，用来作为 QPS 的参考数据
        // 90% == 1ms  1000 100 > 1ms
        int time;
        if( level <= 900 ){
            time = 1;
            // 95% == 1ms  1000 50 > 10ms
        }else if( level <= 950 ){
            time = 10;
            // 99% == 1ms  1000 10 > 100ms
        }else if( level <= 990 ){
            time = 100;
            // 99.9% == 1ms  1000 1 > 1000ms
        }else {
            time = 1000;
        }

        try {
            Thread.sleep(time);
        }catch (InterruptedException e){

        }
        return data;
    }
}