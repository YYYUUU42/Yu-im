package com.yu.im.server.application.netty.tcp;

import com.yu.im.server.application.netty.IMNettyServer;
import com.yu.im.server.application.netty.handler.IMChannelHandler;
import com.yu.im.server.application.netty.tcp.codec.TcpSocketMessageProtocolDecoder;
import com.yu.im.server.application.netty.tcp.codec.TcpSocketMessageProtocolEncoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @author yu
 * @description 基于 TCP 实现的 Netty 服务启动类
 * @date 2024-05-17
 */
@Component
@ConditionalOnProperty(prefix = "tcpsocket", value = "enable", havingValue = "true", matchIfMissing = true)
public class TcpSocketServer implements IMNettyServer {
	private final Logger logger = LoggerFactory.getLogger(TcpSocketServer.class);

	private volatile boolean ready = false;

	@Value("${tcpsocket.port}")
	private int port;

	private EventLoopGroup bossGroup;
	private EventLoopGroup workGroup;

	public TcpSocketServer() {
		bossGroup = new NioEventLoopGroup();
		workGroup = new NioEventLoopGroup();
	}

	@Override
	public boolean isReady() {
		return ready;
	}

	@Override
	public void start() {
		ServerBootstrap bootstrap = new ServerBootstrap();
		bootstrap.group(bossGroup, workGroup)
				.channel(NioServerSocketChannel.class)
				.childHandler(new ChannelInitializer<Channel>() {
					@Override
					protected void initChannel(Channel ch) throws Exception {
						ChannelPipeline pipeline = ch.pipeline();
						pipeline.addLast(new IdleStateHandler(120, 0, 0, TimeUnit.SECONDS));
						pipeline.addLast("encode", new TcpSocketMessageProtocolEncoder());
						pipeline.addLast("decode", new TcpSocketMessageProtocolDecoder());
						pipeline.addLast("handler", new IMChannelHandler());
					}
				}).option(ChannelOption.SO_BACKLOG, 5)
				.childOption(ChannelOption.SO_KEEPALIVE, true);
		try {
			bootstrap.bind(port).sync().channel();
			this.ready = true;
			logger.info("TcpSocketServer.start|服务启动正常,监听端口:{}", port);
		} catch (InterruptedException e) {
			logger.error("TcpSocketServer.start|服务启动异常:{}", e.getMessage());
		}
	}

	@Override
	public void shutdown() {
		try {
			if (bossGroup != null && !bossGroup.isShuttingDown() && !bossGroup.isShutdown()) {
				bossGroup.shutdownGracefully();
			}
			if (workGroup != null && !workGroup.isShuttingDown() && !workGroup.isShutdown()) {
				workGroup.shutdownGracefully();
			}
		} catch (Exception e) {
			logger.error("WebSocketServer.shutdown|websocket server 停止异常", e);
		}
		this.ready = false;
		logger.info("WebSocketServer.shutdown|websocket server 停止");
	}
}
