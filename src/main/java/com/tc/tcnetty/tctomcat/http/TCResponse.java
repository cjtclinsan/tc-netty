package com.tc.tcnetty.tctomcat.http;

import java.io.IOException;
import java.io.OutputStream;

/**
 * @author woshi
 * @date 2020/8/20
 */
public class TCResponse {
    private OutputStream out;

    public TCResponse(OutputStream out) {
        this.out = out;
    }

    public void write(String s) throws IOException {
        //输出也遵循 http，状态码为 200
        StringBuilder sb = new StringBuilder();
        sb.append("HTTP/1.1 200 OK\n").append("Content-Type: text/html;\n")
                .append("\r\n").append(s);
        out.write(sb.toString().getBytes());
    }
}