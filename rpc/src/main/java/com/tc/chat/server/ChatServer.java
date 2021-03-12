package com.tc.chat.server;

import com.tc.chat.ptotocol.IMDecoder;
import com.tc.chat.ptotocol.IMEncoder;
import com.tc.chat.server.handler.HttpServerHandler;
import com.tc.chat.server.handler.TerminalServerHandler;
import com.tc.chat.server.handler.WebSocketServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @author woshi
 * @date 2021/3/4
 */
@Slf4j
public class ChatServer {
    private int port = 8080;
    public void start(int port){
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try{

            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel channel) throws Exception {
                            ChannelPipeline pipeline = channel.pipeline();

                            /**解析自定义协议*/
                            pipeline.addLast(new IMDecoder());       // Inbound
                            pipeline.addLast(new IMEncoder());       // Outbound
                            pipeline.addLast(new TerminalServerHandler());      // Inbound

                            /**解析 HTTP 请求*/
                            pipeline.addLast(new HttpServerCodec());       // Outbound

                            // 主要是将同一个 Http 请求或响应的多个消息对象变成一个 fullHttpRequest 完整的消息对象
                            pipeline.addLast(new HttpObjectAggregator(64 * 1024));

                            // 用于处理大数据流，比如 1GB 的文件如果直接传输可定会占满 JVM 内存
                            pipeline.addLast(new ChunkedWriteHandler());    //Inbound Outbound
                            pipeline.addLast(new HttpServerHandler());      // Inbound

                            /**解析 WebSocket 请求*/
                            pipeline.addLast(new WebSocketServerProtocolHandler("/im"));    // Inbound
                            pipeline.addLast(new WebSocketServerHandler());    // Inbound
                        }
                    });
            ChannelFuture future = b.bind(this.port).sync();
            log.info("服务已启动，监听端口:"+this.port);
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    public void start(){
        start(this.port);
    }

    public static void main(String[] args) {
        if( args.length > 0 ){
            new ChatServer().start(Integer.valueOf(args[0]));
        }else {
            new ChatServer().start();
        }
    }
}