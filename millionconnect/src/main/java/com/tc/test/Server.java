package com.tc.test;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.FixedLengthFrameDecoder;

/**
 * @author woshi
 * @date 2021/3/10
 */
public class Server {
    private static final int port = 8000;

    public static void main(String[] args) {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup= new NioEventLoopGroup();
        final EventLoopGroup businessGroup = new NioEventLoopGroup(1000);

        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childOption(ChannelOption.SO_REUSEADDR,true);

        bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel channel) throws Exception {
                // 自定义长度的解码，每次发送一个 long 类型的长度数据
                // 每次传递一个系统的时间戳
                channel.pipeline().addLast(new FixedLengthFrameDecoder(Long.BYTES));
                channel.pipeline().addLast(businessGroup, ServerThreadPoolHandler.INSTANCE);
            }
        });

        ChannelFuture channelFuture = bootstrap.bind(port).addListener(
            (ChannelFutureListener) channelFuture1 -> System.out.println("服务端启动成功，监听端口为: " + port)
        );
    }
}