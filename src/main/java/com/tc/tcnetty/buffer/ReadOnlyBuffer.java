package com.tc.tcnetty.buffer;

import java.nio.ByteBuffer;

/**
 * @author woshi
 * @date 2020/8/5
 */
public class ReadOnlyBuffer {
    public static void main(String[] args) {
        ByteBuffer buffer = ByteBuffer.allocate(10);
        //缓冲区赋值
        for( int i = 0; i < buffer.capacity(); ++i ){
            buffer.put((byte) i);
        }

        //创建只读缓冲区
        ByteBuffer readOnly = buffer.asReadOnlyBuffer();

        //改变原缓冲区数据
        for( int i = 0; i < buffer.capacity(); ++i ){
            byte b = buffer.get(i);
            b *= 10;
            buffer.put(i, b);
        }

        readOnly.position(0);
        readOnly.limit(buffer.capacity());

        while ( readOnly.remaining() > 0 ){
            System.out.println(readOnly.get());
        }
    }
}