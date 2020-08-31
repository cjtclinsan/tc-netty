package com.tc.tcnetty.tctomcat;

import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;
import com.tc.tcnetty.tctomcat.http.TCRequest;
import com.tc.tcnetty.tctomcat.http.TCResponse;
import com.tc.tcnetty.tctomcat.servlet.TCServlet;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @author woshi
 * @date 2020/8/20
 */
public class TCTomcat {
    private int port = 8080;
    private ServerSocket server;
    private Map<String, TCServlet> servletMapping = new HashMap<>();

    private Properties webxml = new Properties();

    private void init(){
        //加载 web.xml 文件，同时初始化 ServletMapping 对象
        try {
            String WEB_INF = this.getClass().getResource("/").getPath();
            FileInputStream fis = new FileInputStream(WEB_INF+ "WEB-INF/web.properties");
            webxml.load(fis);

            for (Object k : webxml.keySet()) {
                String key = k.toString();
                if( key.endsWith(".url") ){
                    String servletName = key.replaceAll("\\.url$", "");
                    String url = webxml.getProperty(key);
                    String className = webxml.getProperty(servletName+".className");
                    //单实例，多线程
                    TCServlet obj = (TCServlet) Class.forName(className).newInstance();
                    servletMapping.put(url, obj);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void start(){
        // 1.加载配置文件
        init();

        try {
            server = new ServerSocket(this.port);
            System.out.println("TCTomcat 已经启动，监听端口是:"+port);

            // 2.等待用户请求，用一个死循环来等待用户请求
            while (true){
                Socket client = server.accept();

                // 3.HTTP 请求，发送的数据就是字符串 -----有规律的字符串（HTTP）
                process(client);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void process(Socket client) throws Exception {
        InputStream is = client.getInputStream();
        OutputStream os = client.getOutputStream();

        // 4.Request/Response
        TCResponse response = new TCResponse(os);
        TCRequest request = new TCRequest(is);

        // 5.从协议内容中获取url，把相应的 Servlet 用反射进行实例化
        String url = request.getUrl();

        if( servletMapping.containsKey(url) ){
            // 6.调用实例化对象的 service() 方法，执行具体的逻辑 doGet/doPost 方法
            servletMapping.get(url).service(request, response);
        }else {
            response.write("404 - Not Found");
        }

        os.flush();
        os.close();

        is.close();
        client.close();
    }

    public static void main(String[] args) {
        new TCTomcat().start();
    }
}