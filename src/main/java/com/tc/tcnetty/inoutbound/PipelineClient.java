package com.tc.tcnetty.inoutbound;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * @author woshi
 * @date 2021/2/23
 */
public class PipelineClient {
    public void connect(String host, int port){
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            Bootstrap strap = new Bootstrap();
            strap.group(workerGroup)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel channel) throws Exception {
                            channel.pipeline().addLast(new ClientIntHandler());
                        }
                    });

            ChannelFuture future = strap.connect(host, port).sync();
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            workerGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        PipelineClient client = new PipelineClient();
        client.connect("localhost", 8000);
    }
}