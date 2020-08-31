package com.tc.tcnetty.channel;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @author woshi
 * @date 2020/8/13
 */
public class FileOutputDemo {
    static private final byte message[] = {67,89,90,98,78,86,90,98,95,101, 109, 120, 127};

    public static void main(String[] args) throws IOException {
        FileOutputStream fout = new FileOutputStream("E://demo/test.txt");

        FileChannel fileChannel = fout.getChannel();

        ByteBuffer buffer = ByteBuffer.allocate(1024);

        for( int i = 0; i < message.length; ++i ){
            buffer.put(message[i]);
        }

        buffer.flip();

        fileChannel.write(buffer);

        fout.close();
    }
}