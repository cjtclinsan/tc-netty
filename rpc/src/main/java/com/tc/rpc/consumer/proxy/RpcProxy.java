package com.tc.rpc.consumer.proxy;

import com.tc.rpc.protocol.InvokerProtocol;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import org.springframework.cglib.proxy.InvocationHandler;
import org.springframework.cglib.proxy.Proxy;

import java.lang.reflect.Method;

/**
 * @author woshi
 * @date 2020/8/30
 */
public class RpcProxy {
    public static <T> T create(Class<?> clazz){
        // clazz 传进来本身就是 interface
        MethodProxy proxy = new MethodProxy(clazz);
        Class<?> [] interfaces = clazz.isInterface() ? new Class[]{clazz}:clazz.getInterfaces();
        T result = (T) Proxy.newProxyInstance(clazz.getClassLoader(), interfaces, proxy);
        return result;
    }

    private static class MethodProxy implements InvocationHandler {
        private Class<?> clazz;
        public MethodProxy(Class<?> clazz){
            this.clazz = clazz;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            // 如果传进来的是一个已实现的具体类
            if( Object.class.equals(method.getDeclaringClass()) ){
                return method.invoke(this, args);
            }else {
                //如果传进来的是一个接口(核心)
                return rpcInvoker(proxy, method, args);
            }
        }

        private Object rpcInvoker(Object proxy, Method method, Object[] args) {
            //传输协议封装
            InvokerProtocol msg = new InvokerProtocol();
            msg.setClassName(this.clazz.getName());
            msg.setMethodName(method.getName());
            msg.setValues(args);
            msg.setParames(method.getParameterTypes());

            final RpcProxyHandler consumerHandler = new RpcProxyHandler();
            EventLoopGroup group = new NioEventLoopGroup();

            try {
                Bootstrap b = new Bootstrap();
                b.group(group)
                        .channel(NioSocketChannel.class)
                        .option(ChannelOption.TCP_NODELAY, true)
                        .handler(new ChannelInitializer<SocketChannel>() {
                            @Override
                            protected void initChannel(SocketChannel channel) throws Exception {
                                ChannelPipeline pipeline = channel.pipeline();
                                // 自定义协议解码器，参与与编码器同
                                pipeline.addLast("frameDecoder", new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0,4,0,4));
                                // 自定义协议编码器
                                pipeline.addLast("frameEncoder", new LengthFieldPrepender(4));
                                // 对象参数类型编码器
                                pipeline.addLast("encoder", new ObjectEncoder());
                                // 对象参数类型解码器
                                pipeline.addLast("decoder", new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.cacheDisabled(null)));
                                pipeline.addLast("handler", consumerHandler);
                            }
                        });
                ChannelFuture future = b.connect("localhost", 8080).sync();
                future.channel().writeAndFlush(msg).sync();
                future.channel().closeFuture().sync();
            }catch (Exception e){
                e.printStackTrace();
            } finally {
                group.shutdownGracefully();
            }

            return consumerHandler.getResponse();
        }
    }
}