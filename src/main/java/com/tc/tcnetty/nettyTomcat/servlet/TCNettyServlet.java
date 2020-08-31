package com.tc.tcnetty.nettyTomcat.servlet;

import com.tc.tcnetty.nettyTomcat.TcNettyRequest;
import com.tc.tcnetty.nettyTomcat.TcNettyResponse;

/**
 * @author woshi
 * @date 2020/8/20
 */
public abstract class TCNettyServlet {
    public void service(TcNettyRequest request, TcNettyResponse response) throws Exception {
        if("GET".equalsIgnoreCase(request.getMethod())){
            doGet(request, response);
        }else {
            doPost(request, response);
        }
    }

    public abstract void doGet(TcNettyRequest request, TcNettyResponse response) throws Exception;

    public abstract void doPost(TcNettyRequest request, TcNettyResponse response) throws Exception;
}