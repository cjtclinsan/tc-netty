package com.tc.tcnetty.tctomcat.servlet;

import com.tc.tcnetty.tctomcat.http.TCRequest;
import com.tc.tcnetty.tctomcat.http.TCResponse;

/**
 * @author woshi
 * @date 2020/8/20
 */
public abstract class TCServlet {
    public void service(TCRequest request, TCResponse response) throws Exception {
        if("GET".equalsIgnoreCase(request.getMethod())){
            doGet(request, response);
        }else {
            doPost(request, response);
        }
    }

    public abstract void doGet(TCRequest request, TCResponse response) throws Exception;

    public abstract void doPost(TCRequest request, TCResponse response) throws Exception;
}