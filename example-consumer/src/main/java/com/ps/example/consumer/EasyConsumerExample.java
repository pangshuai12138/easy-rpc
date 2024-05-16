package com.ps.example.consumer;

import com.ps.easyrpc.RpcApplication;
import com.ps.easyrpc.config.RpcConfig;
import com.ps.easyrpc.proxy.ServiceProxyFactory;
import com.ps.easyrpc.utils.ConfigUtils;
import com.ps.example.common.model.User;
import com.ps.example.common.service.UserService;

/**
 * @Description: 简易服务消费者示例
 * @Author: pangshuai
 * @Date: 2024/5/12 13:16
 **/
public class EasyConsumerExample {

    public static void main(String[] args) {
        // 加载配置 测试全部配置是否生效
        RpcConfig rpc = ConfigUtils.loadConfig(RpcConfig.class, "rpc");
        System.out.println(rpc);

        // 这里需要获取 UserService 的实现类对象
        // 静态代理
        // UserService userService = new UserServiceProxy();
        // 动态代理
        UserService userService = ServiceProxyFactory.getProxy(UserService.class);

        User user = new User();
        user.setName("pangshuai");
        // 调用
        User newUser = userService.getUser(user);
        if (newUser != null) {
            System.out.println(newUser.getName());
        } else {
            System.out.println("user == null");
        }
    }
}
