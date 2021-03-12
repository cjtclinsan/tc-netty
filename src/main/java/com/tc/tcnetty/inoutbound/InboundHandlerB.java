package com.tc.tcnetty.inoutbound;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * @author woshi
 * @date 2021/2/23
 */
public class InboundHandlerB extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("InboundHandlerB");
        ctx.fireChannelRead(msg);
    }
}