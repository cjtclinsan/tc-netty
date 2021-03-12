package com.tc.test;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author woshi
 * @date 2021/3/10
 */
public class ServerThreadPoolHandler extends ServerHandler {
    public static final ChannelHandler INSTANCE = new ServerThreadPoolHandler();
    private static ExecutorService threadPool = Executors.newFixedThreadPool(25);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
        final ByteBuf data = Unpooled.directBuffer();
        data.writeBytes(msg);
        threadPool.submit(()->{
            Object result = getResult(data);
            ctx.channel().writeAndFlush(result);
        });
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