package com.tc.millionconnect.client;

import com.tc.millionconnect.server.MillionServer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * @author woshi
 * @date 2021/3/9
 */
public class MillionClient {
    private static final String SERVER_PORT = "127.0.0.1";
    private static final int BEGIN_PORT = 8200;
    private static final int END_PORT = 8300;

    public void start(final int beginPort, int endPort){
        System.out.println("客户端启动中~~~");

        EventLoopGroup workerGroup = new NioEventLoopGroup();
        final Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(workerGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_REUSEADDR, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel channel) throws Exception {

                    }
                });

        int index = 0;
        int port;

        while (!Thread.interrupted()){
            port = beginPort + index;
            try {
                ChannelFuture future = bootstrap.connect(SERVER_PORT, port);
                future.addListener((ChannelFutureListener) channelFuture -> {
                    if( !channelFuture.isSuccess() ){
                        System.out.println("连接失败，程序关闭！");
                        System.exit(0);
                    }
                });
                future.get();
            }catch (Exception e){
                e.printStackTrace();
            }

            if( port == endPort ){
                index = 0;
            }else {
                index++;
            }
        }
    }

    public static void main(String[] args) {
        new MillionClient().start(MillionServer.BEGIN_PORT, MillionServer.END_PORT);
    }
}