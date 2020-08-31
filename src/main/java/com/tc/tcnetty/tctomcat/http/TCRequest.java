package com.tc.tcnetty.tctomcat.http;

import java.io.InputStream;

/**
 * @author woshi
 * @date 2020/8/20
 */
public class TCRequest {
    private String method;
    private String url;

    public TCRequest(InputStream in){
        try {
            //获取 http 内容
            String content = "";
            byte[] buff = new byte[1024];
            int len = 0;
            if( (len = in.read(buff)) > 0 ){
                content = new String(buff, 0, len);
            }

            String line = content.split("\\n")[0];
            String[] arr = line.split("\\s");
            this.method = arr[0];
            this.url = arr[1].split("\\?")[0];

        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public String getMethod() {
        return method;
    }

    public String getUrl() {
        return url;
    }
}