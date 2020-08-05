package com.tc.tcnetty.buffer;

import java.nio.ByteBuffer;

/**
 * @author woshi
 * @date 2020/8/5
 */
public class BufferWrap {
    public void myMethod(){
        //分配指定大小缓冲区
        ByteBuffer buffer = ByteBuffer.allocate(10);

        //包装一个现有的数组
        byte array[] = new byte[10];
        ByteBuffer byteBuffer = ByteBuffer.wrap(array);

        System.out.println(buffer+"  "+byteBuffer);
    }
}