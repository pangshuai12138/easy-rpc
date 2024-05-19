package com.ps.example.provider;

import com.ps.easyrpc.RpcApplication;
import com.ps.easyrpc.config.RegistryConfig;
import com.ps.easyrpc.config.RpcConfig;
import com.ps.easyrpc.model.ServiceMetaInfo;
import com.ps.easyrpc.registry.LocalRegistry;
import com.ps.easyrpc.registry.Registry;
import com.ps.easyrpc.registry.RegistryFactory;
import com.ps.easyrpc.server.HttpServer;
import com.ps.easyrpc.server.VertxHttpServer;
import com.ps.example.common.service.UserService;

/**
 * 服务提供者示例
 */
public class ProviderExample {

    public static void main(String[] args) {
        // RPC 框架初始化
        RpcApplication.init();

        // 注册服务
        String serviceName = UserService.class.getName();
        LocalRegistry.register(serviceName, UserServiceImpl.class);

        // 注册服务到注册中心
        RpcConfig rpcConfig = RpcApplication.getRpcConfig();
        RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
        Registry registry = RegistryFactory.getInstance(registryConfig.getRegistry());
        ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
        serviceMetaInfo.setServiceName(serviceName);
        serviceMetaInfo.setServiceAddress(rpcConfig.getServerHost() + ":" + rpcConfig.getServerPort());
        try {
            registry.register(serviceMetaInfo);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // 启动 web 服务
        HttpServer httpServer = new VertxHttpServer();
        httpServer.doStart(RpcApplication.getRpcConfig().getServerPort());
    }
}
