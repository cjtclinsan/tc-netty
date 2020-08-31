package com.tc.tcnetty.nettyTomcat.servlet;

import com.tc.tcnetty.nettyTomcat.TcNettyRequest;
import com.tc.tcnetty.nettyTomcat.TcNettyResponse;

/**
 * @author woshi
 * @date 2020/8/26
 */
public class FirstNettyServlet extends TCNettyServlet {

    @Override
    public void doGet(TcNettyRequest request, TcNettyResponse response) throws Exception {
        this.doPost(request, response);
    }

    @Override
    public void doPost(TcNettyRequest request, TcNettyResponse response) throws Exception {
        response.write("This First Netty Servlet");
    }
}