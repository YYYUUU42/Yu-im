package com.yu.im.server.application.netty.handler;

import com.yu.im.common.cache.distribute.DistributedCacheService;
import com.yu.im.common.domain.constants.IMConstants;
import com.yu.im.common.domain.model.IMSendInfo;
import com.yu.im.server.application.netty.cache.UserChannelContextCache;
import com.yu.im.server.infrastructure.holder.SpringContextHolder;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;


/**
 * @author yu
 * @description IM 通道处理器
 * @date 2024-05-17
 */
public class IMChannelHandler extends SimpleChannelInboundHandler<IMSendInfo> {

	private final Logger logger = LoggerFactory.getLogger(IMChannelHandler.class);
	private static final AttributeKey<Long> USER_ID_ATTR = AttributeKey.valueOf(IMConstants.USER_ID);
	private static final AttributeKey<Integer> TERMINAL_ATTR = AttributeKey.valueOf(IMConstants.TERMINAL_TYPE);

	/**
	 * 当接收到新的IMSendInfo消息时调用
	 */
	@Override
	protected void channelRead0(ChannelHandlerContext channelHandlerContext, IMSendInfo imSendInfo) throws Exception {
		//TODO 处理登录和心跳消息
	}

	/**
	 * 当发生异常时调用
	 */
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		logger.error("IMChannelHandler.exceptionCaught|异常:{}", cause.getMessage());
	}

	/**
	 * 当新的连接建立时调用
	 */
	@Override
	public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
		logger.info("IMChannelHandler.handlerAdded|{}连接", ctx.channel().id().asLongText());
	}

	/**
	 * 当连接断开时调用
	 */
	@Override
	public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
		// 获取用户ID和终端类型
		Long userId = ctx.channel().attr(USER_ID_ATTR).get();
		Integer terminal = ctx.channel().attr(TERMINAL_ATTR).get();

		// 获取用户的 ChannelHandlerContext
		ChannelHandlerContext channelCtx = UserChannelContextCache.getChannelCtx(userId, terminal);

		// 防止异地登录误删
		if (channelCtx != null && channelCtx.channel().id().equals(ctx.channel().id())) {
			// 移除用户的ChannelHandlerContext
			UserChannelContextCache.removeChannelCtx(userId, terminal);

			// 获取分布式缓存服务
			Optional<DistributedCacheService> distributedCacheServiceOpt = SpringContextHolder.getBean(IMConstants.DISTRIBUTED_CACHE_REDIS_SERVICE_KEY);
			if (distributedCacheServiceOpt.isPresent()) {
				// 构建Redis键
				DistributedCacheService distributedCacheService = distributedCacheServiceOpt.get();
				String redisKey = String.join(IMConstants.REDIS_KEY_SPLIT, IMConstants.IM_USER_SERVER_ID, userId.toString(), terminal.toString());

				// 删除Redis键
				distributedCacheService.delete(redisKey);
			}
			logger.info("IMChannelHandler.handlerRemoved|断开连接, userId:{}, 终端类型:{}", userId, terminal);
		}

	}

	/**
	 * 当用户事件触发时调用
	 */
	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		if (evt instanceof IdleStateEvent) {
			// 获取空闲状态
			IdleState state = ((IdleStateEvent) evt).state();

			// 如果是读空闲状态
			if (state == IdleState.READER_IDLE) {
				// 获取用户ID和终端类型
				Long userId = ctx.channel().attr(USER_ID_ATTR).get();
				Integer terminal = ctx.channel().attr(TERMINAL_ATTR).get();

				if (userId != null && terminal != null) {
					logger.info("IMChannelHandler.userEventTriggered|心跳超时.即将断开连接, userId:{}, 终端类型:{}", userId, terminal);
					// 关闭连接
					ctx.channel().close();
				}
			}
		} else {
			super.userEventTriggered(ctx, evt);
		}
	}
}
