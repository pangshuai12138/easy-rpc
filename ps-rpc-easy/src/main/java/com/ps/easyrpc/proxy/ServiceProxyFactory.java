package com.ps.easyrpc.proxy;

import java.lang.reflect.Proxy;

/**
 * @Description: 服务代理工厂（用于创建代理对象）
 * @Author: pangshuai
 * @Date: 2024/5/12 14:29
 **/
public class ServiceProxyFactory {

    /**
     * 根据服务类获取代理对象
     *
     * @param serviceClass
     * @param <T>
     * @return
     */
    public static <T> T getProxy(Class<T> serviceClass) {
        return (T) Proxy.newProxyInstance(
                serviceClass.getClassLoader(),
                new Class[]{serviceClass},
                new ServiceProxy());
    }
}
