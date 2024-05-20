package com.ps.example.consumer;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.ps.easyrpc.RpcApplication;
import com.ps.easyrpc.serializer.SerializerFactory;
import com.ps.example.common.model.User;
import com.ps.example.common.service.UserService;
import com.ps.easyrpc.model.RpcRequest;
import com.ps.easyrpc.model.RpcResponse;
import com.ps.easyrpc.serializer.JdkSerializer;
import com.ps.easyrpc.serializer.Serializer;

import java.io.IOException;

/**
 * @Description: 静态代理
 * @Author: pangshuai
 * @Date: 2024/5/12 14:07
 **/
public class UserServiceProxy implements UserService {

    @Override
    public User getUser(User user) {
        // 指定序列化器
        final Serializer serializer = SerializerFactory.getInstance(RpcApplication.getRpcConfig().getSerializer());

        // 发请求
        // 这里是与动态代理的区别，动态代理 serviceName 和 methodName 和 parameterTypes 和 args 是变量；静态代理是确定值
        RpcRequest rpcRequest = RpcRequest.builder()
                .serviceName(UserService.class.getName())
                .methodName("getUser")
                .parameterTypes(new Class[]{User.class})
                .args(new Object[]{user})
                .build();
        try {
            byte[] bodyBytes = serializer.serialize(rpcRequest);
            byte[] result;
            try (HttpResponse httpResponse = HttpRequest.post("http://localhost:8090")
                    .body(bodyBytes)
                    .execute()) {
                result = httpResponse.bodyBytes();
            }
            RpcResponse rpcResponse = serializer.deserialize(result, RpcResponse.class);
            return (User) rpcResponse.getData();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
