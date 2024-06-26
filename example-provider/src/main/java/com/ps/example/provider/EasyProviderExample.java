package com.ps.example.provider;

import com.ps.easyrpc.RpcApplication;
import com.ps.easyrpc.config.RpcConfig;
import com.ps.easyrpc.utils.ConfigUtils;
import com.ps.example.common.service.UserService;
import com.ps.easyrpc.registry.LocalRegistry;
import com.ps.easyrpc.server.HttpServer;
import com.ps.easyrpc.server.VertxHttpServer;

/**
 * @Description: EasyProviderExample 启动类
 * @Author: pangshuai
 * @Date: 2024/5/12 11:22
 **/
public class EasyProviderExample {

    public static void main(String[] args) {
        // RPC 框架初始化
        RpcApplication.init();

        // 注册服务
        LocalRegistry.register(UserService.class.getName(), UserServiceImpl.class);

        // 启动 web 服务
        HttpServer httpServer = new VertxHttpServer();
        // 引入配置中的端口号，而不是硬编码为8090
        httpServer.doStart(RpcApplication.getRpcConfig().getServerPort());
    }
}
