package com.ps.easyrpc.proxy;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.ps.easyrpc.RpcApplication;
import com.ps.easyrpc.config.RpcConfig;
import com.ps.easyrpc.constant.RpcConstant;
import com.ps.easyrpc.fault.retry.RetryStrategy;
import com.ps.easyrpc.fault.retry.RetryStrategyFactory;
import com.ps.easyrpc.fault.tolerant.TolerantStrategy;
import com.ps.easyrpc.fault.tolerant.TolerantStrategyFactory;
import com.ps.easyrpc.loadbalancer.LoadBalancer;
import com.ps.easyrpc.loadbalancer.LoadBalancerFactory;
import com.ps.easyrpc.model.RpcRequest;
import com.ps.easyrpc.model.RpcResponse;
import com.ps.easyrpc.model.ServiceMetaInfo;
import com.ps.easyrpc.registry.Registry;
import com.ps.easyrpc.registry.RegistryFactory;
import com.ps.easyrpc.serializer.JdkSerializer;
import com.ps.easyrpc.serializer.Serializer;
import com.ps.easyrpc.serializer.SerializerFactory;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description: 服务代理（JDK 动态代理）
 * @Author: pangshuai
 * @Date: 2024/5/12 14:26
 **/
public class ServiceProxy implements InvocationHandler {

    /**
     * 调用代理
     *
     * @return
     * @throws Throwable
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 指定序列化器
        final Serializer serializer = SerializerFactory.getInstance(RpcApplication.getRpcConfig().getSerializer());
        // System.out.println("序列化方式：" + RpcApplication.getRpcConfig().getSerializer());

        // 构造请求
        String serviceName = method.getDeclaringClass().getName();
        RpcRequest rpcRequest = RpcRequest.builder()
                .serviceName(serviceName)
                .methodName(method.getName())
                .parameterTypes(method.getParameterTypes())
                .args(args)
                .build();

        // 序列化
        byte[] bodyBytes = serializer.serialize(rpcRequest);

        // 从注册中心获取服务提供者请求地址
        RpcConfig rpcConfig = RpcApplication.getRpcConfig();
        Registry registry = RegistryFactory.getInstance(rpcConfig.getRegistryConfig().getRegistry());
        ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
        serviceMetaInfo.setServiceName(serviceName);
        serviceMetaInfo.setServiceVersion(RpcConstant.DEFAULT_SERVICE_VERSION);
        List<ServiceMetaInfo> serviceMetaInfoList = registry.serviceDiscovery(serviceMetaInfo.getServiceKey());
        if (CollUtil.isEmpty(serviceMetaInfoList)) {
            throw new RuntimeException("暂无服务地址");
        }

        // 负载均衡
        LoadBalancer loadBalancer = LoadBalancerFactory.getInstance(rpcConfig.getLoadBalancer());
        // 将调用方法名（请求路径）作为负载均衡参数
        Map<String, Object> requestParams = new HashMap<>();
        requestParams.put("methodName", rpcRequest.getMethodName());
        System.out.println("负载均衡类型：" + rpcConfig.getLoadBalancer());
        ServiceMetaInfo selectedServiceMetaInfo = loadBalancer.select(requestParams,serviceMetaInfoList);

        // 发送请求
        // 使用重试机制
        RpcResponse rpcResponse;
        try {
            RetryStrategy retryStrategy = RetryStrategyFactory.getInstance(rpcConfig.getRetryStrategy());
            System.out.println("重试策略：" + rpcConfig.getRetryStrategy());
            rpcResponse = retryStrategy.doRetry(() ->
                    doRequest(bodyBytes, selectedServiceMetaInfo, serializer)
            );
        } catch (Exception e){
            // 容错机制
            TolerantStrategy tolerantStrategy = TolerantStrategyFactory.getInstance(rpcConfig.getTolerantStrategy());
            System.out.println("容错策略：" + rpcConfig.getTolerantStrategy());
            rpcResponse = tolerantStrategy.doTolerant(null, e);
        }
        return rpcResponse.getData();


    }

    private RpcResponse doRequest(byte[] bodyBytes, ServiceMetaInfo serviceMetaInfo,Serializer serializer) {
        try (HttpResponse httpResponse = HttpRequest.post(serviceMetaInfo.getServiceAddress())
                .body(bodyBytes)
                .execute()) {
            byte[] result = httpResponse.bodyBytes();
            // 反序列化
            RpcResponse rpcResponse = serializer.deserialize(result, RpcResponse.class);
            return rpcResponse;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}