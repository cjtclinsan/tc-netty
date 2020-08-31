package com.tc.rpc.consumer;

import com.tc.rpc.api.IRpcHelloService;
import com.tc.rpc.api.IRpcService;
import com.tc.rpc.consumer.proxy.RpcProxy;

/**
 * @author woshi
 * @date 2020/8/30
 */
public class RpcConsumer {
    public static void main(String[] args) {
        IRpcHelloService rpcHelloService = RpcProxy.create(IRpcHelloService.class);

        System.out.println(rpcHelloService.hello("tccc"));

        IRpcService rpcService = RpcProxy.create(IRpcService.class);

        System.out.println("1+2="+rpcService.add(1,2));
        System.out.println("4-2="+rpcService.sub(4,2));
        System.out.println("1*2="+rpcService.mult(1,2));
        System.out.println("10/2="+rpcService.div(10,2));
    }
}