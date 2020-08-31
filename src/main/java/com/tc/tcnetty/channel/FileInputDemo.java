package com.tc.tcnetty.channel;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @author woshi
 * @date 2020/8/13
 */
public class FileInputDemo {
    public static void main(String[] args) throws IOException {
        FileInputStream fin = new FileInputStream("E://demo/test.txt");

        //从fileinputstream 获取channel
        FileChannel fileChannel = fin.getChannel();

        ByteBuffer buffer = ByteBuffer.allocate(1024);

        //将数据从channel写入buffer
        fileChannel.read(buffer);

        buffer.flip();

        while(buffer.remaining() > 0){
            byte b = buffer.get();
            System.out.print(((char) b)+"  ");
        }

        fin.close();
    }
}