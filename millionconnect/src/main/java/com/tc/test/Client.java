package com.tc.test;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.FixedLengthFrameDecoder;

import java.util.concurrent.ExecutionException;

/**
 * @author woshi
 * @date 2021/3/10
 */
public class Client {
    private static final String SERVER_HOST = "127.0.0.1";

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        new Client().start(8000);
    }

    public void start(int port) throws ExecutionException, InterruptedException {
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        final Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(workerGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_REUSEADDR, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel channel) {
                        channel.pipeline().addLast(new FixedLengthFrameDecoder(Long.BYTES));
                        channel.pipeline().addLast(ClientHandler.INSTANCE);
                    }
                });

        // 客户端每秒发起 1000 次请求
        for (int i = 0; i < 1000; i++) {
            bootstrap.connect(SERVER_HOST, port).get();
        }
    }
}