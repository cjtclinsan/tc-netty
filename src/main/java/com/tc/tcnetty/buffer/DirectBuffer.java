package com.tc.tcnetty.buffer;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @author woshi
 * @date 2020/8/6
 */
public class DirectBuffer {
    public static void main(String[] args) throws IOException {
        //首先从磁盘读取内容
        String infile = "E:/demo/hello.txt";
        FileInputStream fin = new FileInputStream(infile);
        FileChannel fcin = fin.getChannel();

        //把读取的内容写入一个新的文件
        String outfile = String.format("E:/demo/out.txt");
        FileOutputStream fout = new FileOutputStream(outfile);
        FileChannel fcout = fout.getChannel();

        //使用allocateDirect
        ByteBuffer buffer = ByteBuffer.allocateDirect(20);

        while (true){
            buffer.clear();

            int r = fcin.read(buffer);

            if( r == -1 ){
                break;
            }

            buffer.flip();

            fcout.write(buffer);
        }
    }
}