package com.tc.tcnetty.inoutbound;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * @author woshi
 * @date 2021/2/23
 */
public class PipelineServer {
    public void start(int port) {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap strap = new ServerBootstrap();
            strap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel channel) throws Exception {
                            // InboundHandler 执行顺序为 A->B->C
                            channel.pipeline().addLast(new InboundHandlerA());
                            channel.pipeline().addLast(new InboundHandlerB());
                            channel.pipeline().addLast(new InboundHandlerC());

                            // Outbound 执行顺序应该为逆序 C->B->A
                            channel.pipeline().addLast(new OutboundHandlerA());
                            channel.pipeline().addLast(new OutboundHandlerB());
                            channel.pipeline().addLast(new OutboundHandlerC());
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            ChannelFuture f = strap.bind(port).sync();

            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        PipelineServer server = new PipelineServer();
        server.start(8000);
    }
}