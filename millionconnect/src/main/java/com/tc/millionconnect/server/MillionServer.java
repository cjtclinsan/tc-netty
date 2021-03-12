package com.tc.millionconnect.server;

import com.tc.millionconnect.handle.ConnectionCountHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * @author woshi
 * @date 2021/3/9
 */
public class MillionServer {
    public static final int BEGIN_PORT = 8000;
    public static final int END_PORT = 8010;

    public void start(int beginPort, int endPort){
        System.out.println("服务端启动中~~~");

        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childOption(ChannelOption.SO_REUSEADDR, true)
                .childHandler(new ConnectionCountHandler());

        for (int i = 0; i <= (endPort-beginPort); i++) {
            final int port = beginPort + i;
            bootstrap.bind(port).addListener((ChannelFutureListener) channelFuture -> System.out.println("绑定成功监听端口: " + port));
        }

        System.out.println("服务端启动成功");
    }

    public static void main(String[] args) {
        new MillionServer().start(BEGIN_PORT, END_PORT);
    }
}