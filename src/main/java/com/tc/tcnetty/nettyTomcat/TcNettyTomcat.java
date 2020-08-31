package com.tc.tcnetty.nettyTomcat;

import com.tc.tcnetty.nettyTomcat.servlet.TCNettyServlet;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;

import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @author woshi
 * @date 2020/8/25
 * Netty 就是一个同时支持多协议的网络通信框架
 */
public class TcNettyTomcat {
    //打开tomcat源码，全局搜索 ServletSocket

    private int port = 8080;
    private Map<String, TCNettyServlet> servletMapping = new HashMap<>();

    private Properties webxml = new Properties();

    private void init(){
        //加载 web.properties 文件，同时初始化 ServletMapping 对象
        try {
            String WEB_INF = this.getClass().getResource("/").getPath();
            FileInputStream fis = new FileInputStream(WEB_INF + "WEB-INF/web.properties");
            webxml.load(fis);
            for (Object k : webxml.keySet()){
                String key = k.toString();
                if( key.endsWith(".url") ){
                    String serviceName = key.replaceAll("\\.url$", "");
                    String url = webxml.getProperty(key);
                    String className = webxml.getProperty(serviceName+".className");
                    TCNettyServlet obj = (TCNettyServlet) Class.forName(className).newInstance();
                    servletMapping.put(url, obj);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void start(){
        init();

        // Netty 封装了 NIO 的 Reactor 模型，Boss，Worker
        // Boss 线程
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        // Worker 线程
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            // 1.创建对象
            ServerBootstrap server = new ServerBootstrap();
            // 2.配置参数  链路式编程
            System.out.println(server);
            server.group(bossGroup, workerGroup)
                    //主线程处理类，底层使用的是反射
                    .channel(NioServerSocketChannel.class)
                    //子线程处理类 Handler
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        //客户端初始化处理
                        @Override
                        protected void initChannel(SocketChannel client) throws Exception {
                            // 无锁化串行编程,Netty 对 Http 的封装，对顺序有要求
                            // HttpResponseEncoder 编码器
                            // 责任链模式，双向链表 Inbound OutBound
                            client.pipeline().addLast(new HttpResponseEncoder());
                            // HttpResponseDecoder 解码器
                            client.pipeline().addLast(new HttpRequestDecoder());
                            // 业务逻辑处理
                            client.pipeline().addLast(new TCTomcatHandler());
                        }
                    })
                    // 针对主线程的配置，分配线程最大数量 128
                    .option(ChannelOption.SO_BACKLOG, 128)
                    // 针对子线程的配置 设置长连接
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

                    //3.启动服务器
                    ChannelFuture f = server.bind(port).sync();
                    System.out.println("TCTomcat 已启动，监听端口是：" + port);
                    f.channel().closeFuture().sync();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            //关闭线程池
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public class TCTomcatHandler extends ChannelInboundHandlerAdapter {
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            System.out.println(msg);
            if( msg instanceof HttpRequest){
                System.out.println("hello");
                HttpRequest req = (HttpRequest) msg;

                // 转交给自己的 Request 实现
                TcNettyRequest request = new TcNettyRequest(ctx, req);
                // 转交给我们自己的 Response 实现
                TcNettyResponse response = new TcNettyResponse(ctx, req);
                //实际业务处理
                String url = request.getUrl();
                if( servletMapping.containsKey(url) ){
                    servletMapping.get(url).service(request, response);
                }else {
                    response.write("404 - Not Found");
                }
            }
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            cause.printStackTrace();
            System.out.println("异常"+cause.getMessage());
        }
    }

    public static void main(String[] args) {
        new TcNettyTomcat().start();
    }
}