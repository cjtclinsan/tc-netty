package com.tc.tcnetty.tctomcat.servlet;

import com.tc.tcnetty.tctomcat.http.TCRequest;
import com.tc.tcnetty.tctomcat.http.TCResponse;

import java.io.IOException;

/**
 * @author woshi
 * @date 2020/8/20
 */
public class SecondServlet extends TCServlet {
    @Override
    public void doGet(TCRequest request, TCResponse response) throws IOException {
        this.doPost(request, response);
    }

    @Override
    public void doPost(TCRequest request, TCResponse response) throws IOException {
        response.write("This is Second Servlet");
    }
}