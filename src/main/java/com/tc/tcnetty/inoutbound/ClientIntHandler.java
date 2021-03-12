package com.tc.tcnetty.inoutbound;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * @author woshi
 * @date 2021/2/23
 */
public class ClientIntHandler extends ChannelInboundHandlerAdapter {
    /**
     * 读取服务端的消息
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("ClientIntHandler.channelRead");
        ByteBuf buf = (ByteBuf) msg;
        byte[] result = new byte[buf.readableBytes()];
        buf.readBytes(result);
        buf.release();
        ctx.close();
        System.out.println("server said : " + new String(result));
    }

    /**
     * 当连接建立的时候向服务端发送消息，channelActive 事件在连接建立的时候被触发
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("ClientIntHandler.channelActive");
        String msg = "Are you ok?";
        ByteBuf encode = ctx.alloc().buffer(4 * msg.length());
        encode.writeBytes(msg.getBytes());
        ctx.write(encode);
        ctx.flush();
    }
}