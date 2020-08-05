package com.tc.tcnetty.aio.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author woshi
 * @date 2020/8/3
 */
public class AIOServer {
    private final int port;

    public AIOServer(int port) {
        this.port = port;
        listen();
    }

    private void listen() {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(8, 8, 10 ,TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(10), new ThreadPoolExecutor.CallerRunsPolicy());

        try {
            AsynchronousChannelGroup threadGroup = AsynchronousChannelGroup.withCachedThreadPool(executor, 1);
            final AsynchronousServerSocketChannel server = AsynchronousServerSocketChannel.open(threadGroup);
            server.bind(new InetSocketAddress(port));
            System.out.println("服务已启动，监听端口: " + port);

            server.accept(null, new CompletionHandler<AsynchronousSocketChannel, Object>() {
                final ByteBuffer buffer = ByteBuffer.allocate(1024);
                @Override
                public void completed(AsynchronousSocketChannel result, Object attachment) {
                    System.out.println("I/O 操作成功，开始获取数据");

                    try {
                        buffer.clear();
                        result.read(buffer).get();
                        buffer.flip();
                        result.write(buffer);
                        buffer.flip();
                    } catch ( Exception e) {
                        System.out.println(e.getMessage());
                    } finally {
                        try {
                            result.close();
                            server.accept(null, this);
                        } catch (IOException e) {
                            System.out.println(e.getMessage());
                        }
                    }

                    System.out.println("操作完成!");
                }

                @Override
                public void failed(Throwable exc, Object attachment) {
                    System.out.println("I/O 操作失败: " + exc);
                }
            });

            try {
                Thread.sleep(Integer.MAX_VALUE);
            } catch (InterruptedException e) {
                System.out.println(e.getMessage());
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void main(String[] args) {
        int port = 8088;
        new AIOServer(port);
    }
}