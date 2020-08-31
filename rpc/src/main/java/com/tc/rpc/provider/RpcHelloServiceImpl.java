package com.tc.rpc.provider;

import com.tc.rpc.api.IRpcHelloService;

/**
 * @author woshi
 * @date 2020/8/30
 */
public class RpcHelloServiceImpl implements IRpcHelloService {
    @Override
    public String hello(String name) {
        return "Hello:" + name;
    }
}