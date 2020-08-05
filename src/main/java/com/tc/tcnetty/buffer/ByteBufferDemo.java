package com.tc.tcnetty.buffer;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @author woshi
 * @date 2020/8/4
 */
public class ByteBufferDemo {
    public static void main(String[] args) throws IOException {
        // I/O 处理
        FileInputStream fin = new FileInputStream("E:/demo/hello.txt");

        //创建文件的操作管道
        FileChannel fc = fin.getChannel();

        //分配一个 10 个大小的缓冲区
        ByteBuffer buffer = ByteBuffer.allocate(20);
        output("初始化", buffer);

        //读一下
        fc.read(buffer);
        output("调用read之后", buffer);

        //准备操作之前，锁定操作范围
        buffer.flip();
        output("调用 flip:", buffer);

        //判断有没有可读数据
        while (buffer.remaining() > 0){
            byte b = buffer.get();
        }
        output("调用get():", buffer);

        //解锁
        buffer.clear();
        output("调用 clear():", buffer);

        //关闭管道
        fin.close();
    }

    private static void output(String step, ByteBuffer buffer) {
        System.out.println(step+" : ");

        System.out.print("capacity:" + buffer.capacity()+",");

        System.out.print("position:" + buffer.position()+",");

        System.out.println("limit:"+ buffer.limit());
    }
}