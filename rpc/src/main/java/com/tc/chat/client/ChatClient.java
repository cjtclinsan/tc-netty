package com.tc.chat.client;

import com.tc.chat.client.handler.ChatClientHandler;
import com.tc.chat.ptotocol.IMDecoder;
import com.tc.chat.ptotocol.IMEncoder;
import com.tc.chat.ptotocol.IMMessage;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

/**
 * @author woshi
 * @date 2021/3/4
 */
@Slf4j
public class ChatClient {
    private ChatClientHandler clientHandler;
    private String host;
    private int port;

    public ChatClient(String nickName){
        this.clientHandler = new ChatClientHandler(nickName);
    }

    public void connect(String host, int port){
        this.host = host;
        this.port = port;

        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(workerGroup)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new IMDecoder());
                            ch.pipeline().addLast(new IMEncoder());
                            ch.pipeline().addLast(clientHandler);
                        }
                    });

            ChannelFuture future = b.connect(this.host, this.port).sync();
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            workerGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        new ChatClient("tcc").connect("127.0.0.1", 8080);

        String url = "http://localhost:8080/iamges/a.png";

        System.out.println(url.toLowerCase().matches(".*\\.(gif|png|jpg)$"));
    }
}