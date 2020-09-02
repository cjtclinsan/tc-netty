package com.tc.tcnetty.byteBuf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.Unpooled;

/**
 * @author woshi
 * @date 2020/9/1
 */
public class ByteBufDemo {

    public static void main(String[] args) {
        final byte[] CONTENT = new byte[1024];
        long loop = 18000000;
        long startTime = System.currentTimeMillis();
        ByteBuf poolBuffer = null;
        for( int i = 0; i < loop; i++ ){
            poolBuffer = PooledByteBufAllocator.DEFAULT.directBuffer(1024);
            poolBuffer.writeBytes(CONTENT);
            poolBuffer.release();
        }
        long endTime = System.currentTimeMillis();
        System.out.println("内存池分配缓冲区耗时" + (endTime-startTime) + "ms");

        long startTime1 = System.currentTimeMillis();
        ByteBuf byteBuf = null;
        for( int i = 0; i < loop; i++ ){
            byteBuf = Unpooled.directBuffer(1024);
            byteBuf.writeBytes(CONTENT);
            byteBuf.release();
        }
        endTime = System.currentTimeMillis();
        System.out.println("非内存池分配缓冲区耗时"+(endTime-startTime1)+"ms");
    }
}