package com.tc.rpc.registry;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

/**
 * @author woshi
 * @date 2020/8/30
 */
public class RpcRegistry {
    private int port;

    public RpcRegistry(int port) {
        this.port = port;
    }

    public void start(){
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {

                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            //自定义协议解码器
                            /**
                             * 入参有五个
                             * maxFrameLength:框架的最大长度，如果帧的长度>这个值，抛出 TooLongFrameLength
                             * lengthFieldOffset:长度属性偏移量，对应的长度属性在整个消息数据中的位置
                             * lengthFieldLength:长度字段长度
                             * lengthAdjustment:要添加到长度属性值的补偿值
                             * initialBytesToStrip:从解码帧中去除的第一个字节数
                             */
                            pipeline.addLast(
                                    new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0 ,4));
                            // 自定义协议编码器
                            pipeline.addLast(new LengthFieldPrepender(4));
                            // 对象参数类型编码器
                            pipeline.addLast("encoder", new ObjectEncoder());
                            // 对象参数类型解码器
                            pipeline.addLast("decoder", new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.cacheDisabled(null)));

                            pipeline.addLast(new RegistryHandler());
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

                    ChannelFuture future = bootstrap.bind(port).sync();
                    System.out.println("TC RPC Registry start listen at:"+port);
                    future.channel().closeFuture().sync();
        }catch (Exception e){
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        new RpcRegistry(8080).start();
    }
}