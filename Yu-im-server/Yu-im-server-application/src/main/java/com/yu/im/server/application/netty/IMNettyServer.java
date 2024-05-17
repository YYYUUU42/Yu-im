package com.yu.im.server.application.netty;

/**
 * @author yu
 * @description 启动 Netty 服务的通用接口
 * @date 2024-05-17
 */
public interface IMNettyServer {
    /**
     * 是否已就绪
     */
    boolean isReady();

    /**
     * 启动服务
     */
    void start();

    /**
     * 停止服务
     */
    void shutdown();
}


