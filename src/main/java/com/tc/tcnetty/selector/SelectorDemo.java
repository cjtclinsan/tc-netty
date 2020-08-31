package com.tc.tcnetty.selector;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * @author woshi
 * @date 2020/8/11
 */
public class SelectorDemo {
    //准备两个东西
    //轮询器 Selector 大堂经理
    private Selector selector;
    //缓冲区 Buffer 等候区
    private ByteBuffer buffer = ByteBuffer.allocate(1024);

    private Selector getSelector() throws IOException {
        //创建 Selector 对象
        selector = Selector.open();

        //创建可选择的通道，并配置为非阻塞模式
        ServerSocketChannel channel = ServerSocketChannel.open();
        channel.configureBlocking(false);

        //绑定通道到指定端口
        ServerSocket serverSocket = channel.socket();
        InetSocketAddress address = new InetSocketAddress(8080);
        serverSocket.bind(address);

        //向 Selector 注册感兴趣的事件
        channel.register(selector, SelectionKey.OP_ACCEPT);
        return selector;
    }

    /**
     * 开始监听
     */
    public void listen() {
        System.out.println("listen on:"+ 8080);

        while (true){
            //调用会阻塞，直到至少有一个时间发生
            try {
                Selector selector = getSelector();

                selector.select();

                Set<SelectionKey> keys = selector.selectedKeys();
                Iterator<SelectionKey> iter = keys.iterator();
                while (iter.hasNext()){
                    SelectionKey key = iter.next();
                    iter.remove();
                    process(key);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void process(SelectionKey key) throws IOException {
        //接受请求
        if( key.isAcceptable() ){
            ServerSocketChannel server = (ServerSocketChannel) key.channel();
            SocketChannel channel = server.accept();
            channel.configureBlocking(false);
            channel.register(getSelector(), SelectionKey.OP_READ);
        }

        //读数据
        else if( key.isReadable() ){
            SocketChannel channel = (SocketChannel) key.channel();
            int len = channel.read(buffer);
            if ( len > 0 ){
                buffer.flip();
                String content = new String(buffer.array(), 0, len);
                SelectionKey sKey = channel.register(selector, SelectionKey.OP_WRITE);
                sKey.attach(content);
            }else {
                channel.close();
            }
            buffer.clear();
        }else if( key.isWritable() ){
            SocketChannel channel = (SocketChannel) key.channel();
            String content = (String) key.attachment();
            ByteBuffer block = ByteBuffer.wrap( ("输出内容:"+ content).getBytes());
            if( block != null ){
                channel.write(block);
            }else {
                channel.close();
            }
        }
    }

    public static void main(String[] args) {
        new SelectorDemo().listen();
    }
}