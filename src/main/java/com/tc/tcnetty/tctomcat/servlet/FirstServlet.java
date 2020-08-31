package com.tc.tcnetty.tctomcat.servlet;

import com.tc.tcnetty.tctomcat.http.TCRequest;
import com.tc.tcnetty.tctomcat.http.TCResponse;

/**
 * @author woshi
 * @date 2020/8/20
 */
public class FirstServlet extends TCServlet {
    @Override
    public void doGet(TCRequest request, TCResponse response) throws Exception {
        this.doPost(request, response);
    }

    @Override
    public void doPost(TCRequest request, TCResponse response) throws Exception{
        response.write("This is First Servlet");
    }
}