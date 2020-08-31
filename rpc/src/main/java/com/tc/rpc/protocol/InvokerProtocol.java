package com.tc.rpc.protocol;

import lombok.Data;

import java.io.Serializable;

/**
 * @author woshi
 * @date 2020/8/30
 */
@Data
public class InvokerProtocol implements Serializable {
    /**
     * 类名
     */
    private String className;

    /**
     * 函数名
     */
    private String methodName;

    /**
     * 参数类型
     */
    private Class<?>[] parames;

    /**
     * 参数列表
     */
    private Object[] values;
}