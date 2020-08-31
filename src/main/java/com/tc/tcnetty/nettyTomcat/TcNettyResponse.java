package com.tc.tcnetty.nettyTomcat;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;

/**
 * @author woshi
 * @date 2020/8/26
 */
public class TcNettyResponse {
    /**SocketChannel 的封装*/
    private ChannelHandlerContext ctx;

    private HttpRequest request;

    public TcNettyResponse(ChannelHandlerContext ctx, HttpRequest request) {
        this.ctx = ctx;
        this.request = request;
    }

    public void write(String out){
        try {
            if( out == null || out.length() == 0 ){
                return;
            }
            //设置 HTTP 及请求头信息
            FullHttpResponse response = new DefaultFullHttpResponse(
                    //设置版本为 HTTP 1.1
                    HttpVersion.HTTP_1_1,
                    //设置响应状态码
                    HttpResponseStatus.OK,
                    //设置输出内容编码格式
                    Unpooled.wrappedBuffer(out.getBytes("UTF-8"))
            );
            response.headers().set("Content-Type", "text/html;");

            ctx.write(response);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            ctx.flush();
            ctx.close();
        }
    }
}