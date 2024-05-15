package com.ps.easyrpc.server;

/**
 * @Description: HTTP 服务器接口
 * @Author: pangshuai
 * @Date: 2024/5/12 13:21
 **/
public interface HttpServer {

    /**
     * 启动服务器
     *
     * @param port
     */
    void doStart(int port);
}
