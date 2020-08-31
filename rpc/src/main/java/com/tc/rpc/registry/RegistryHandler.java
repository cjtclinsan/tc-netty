package com.tc.rpc.registry;

import com.tc.rpc.protocol.InvokerProtocol;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author woshi
 * @date 2020/8/30
 */
public class RegistryHandler extends ChannelInboundHandlerAdapter {
    // 保存所有可用的服务
    public static ConcurrentHashMap<String, Object> registryMap = new ConcurrentHashMap<>();

    // 保存所有相关的服务类
    public List<String> classNames = new ArrayList<>();

    public RegistryHandler() {
        // 完成递归扫描
        scannerClass("com.tc.rpc.provider");
        doRegistry();
    }

    /**
     * 递归扫描
     * @param packageName
     */
    private void scannerClass(String packageName) {
        URL url = this.getClass().getClassLoader().getResource(packageName.replaceAll("\\.", "/"));
        File dir = new File(url.getFile());
        for (File file : dir.listFiles()) {
            //如果是一个文件夹，继续递归
            if( file.isDirectory() ){
                scannerClass(packageName+"."+file.getName());
            }else {
                classNames.add(packageName+"."+file.getName().replace(".class", "").trim());
            }
        }
    }

    /**
     * 完成注册
     */
    private void doRegistry() {
        if( classNames.size() == 0 ){
            return;
        }
        for (String className : classNames) {
            try {
                Class<?> clazz = Class.forName(className);
                Class<?> i = clazz.getInterfaces()[0];
                registryMap.put(i.getName(), clazz.newInstance());
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Object result = new Object();
        InvokerProtocol request = (InvokerProtocol) msg;

        // 当客户端建立连接时，需要从自定义协议中获取信息，以及具体的服务和实参
        // 使用反射调用
        if( registryMap.containsKey(request.getClassName()) ){
            Object clazz = registryMap.get(request.getClassName());
            Method method = clazz.getClass().getMethod(request.getMethodName(), request.getParames());
            result = method.invoke(clazz, request.getValues());
        }

        ctx.write(result);
        ctx.flush();
        ctx.close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        ctx.close();
    }
}