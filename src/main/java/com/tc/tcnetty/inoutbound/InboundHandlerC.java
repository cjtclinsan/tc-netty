package com.tc.tcnetty.inoutbound;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * @author woshi
 * @date 2021/2/23
 */
public class InboundHandlerC extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("InboundHandlerC");
        ctx.fireChannelRead(msg);
    }
}