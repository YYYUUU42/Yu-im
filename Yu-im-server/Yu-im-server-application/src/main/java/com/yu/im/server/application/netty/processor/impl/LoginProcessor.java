package com.yu.im.server.application.netty.processor.impl;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSON;
import com.yu.im.common.cache.distribute.DistributedCacheService;
import com.yu.im.common.domain.constants.IMConstants;
import com.yu.im.common.domain.enums.IMCmdType;
import com.yu.im.common.domain.jwt.JwtUtils;
import com.yu.im.common.domain.model.IMLoginInfo;
import com.yu.im.common.domain.model.IMSendInfo;
import com.yu.im.common.domain.model.IMSessionInfo;
import com.yu.im.server.application.netty.cache.UserChannelContextCache;
import com.yu.im.server.application.netty.processor.MessageProcessor;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author yu
 * @description 登录处理器
 * @date 2024-05-17
 */
@Component
public class LoginProcessor implements MessageProcessor<IMLoginInfo> {

	private final Logger logger = LoggerFactory.getLogger(LoginProcessor.class);

	@Value("${jwt.accessToken.secret}")
	private String accessTokenSecret;

	@Value("${server.id}")
	private Long serverId;

	@Autowired
	private DistributedCacheService distributedCacheService;

	private static final AttributeKey<Long> USER_ID_ATTR = AttributeKey.valueOf(IMConstants.USER_ID);
	private static final AttributeKey<Integer> TERMINAL_ATTR = AttributeKey.valueOf(IMConstants.TERMINAL_TYPE);
	private static final AttributeKey<Long> HEARTBEAT_ATTR = AttributeKey.valueOf(IMConstants.HEARTBEAT_TIMES);

	/**
	 * 处理登录消息
	 */
	@Override
	public synchronized void process(ChannelHandlerContext ctx, IMLoginInfo loginInfo) {
		try {
			//登录Token检验未通过
			if (!JwtUtils.checkSign(loginInfo.getAccessToken(), accessTokenSecret)) {
				ctx.channel().close();
				logger.warn("LoginProcessor.process|用户登录信息校验未通过,强制用户下线,token:{}", loginInfo.getAccessToken());
			}

			String info = JwtUtils.getInfo(loginInfo.getAccessToken());
			IMSessionInfo sessionInfo = JSON.parseObject(info, IMSessionInfo.class);
			if (sessionInfo == null) {
				logger.warn("LoginProcessor.process|转化后的SessionInfo为空");
				return;
			}

			Long userId = sessionInfo.getUserId();
			Integer terminal = sessionInfo.getTerminal();
			logger.info("LoginProcessor.process|用户登录, userId:{}", userId);
			ChannelHandlerContext channelCtx = UserChannelContextCache.getChannelCtx(userId, terminal);

			//判断当前连接的id不同，则表示当前用户已经在异地登录
			if (channelCtx != null && !channelCtx.channel().id().equals(ctx.channel().id())) {
				//不允许用户在同一种终端，登录多个设备
				IMSendInfo<String> imSendInfo = new IMSendInfo<>(IMCmdType.FORCE_DOWN_LINE.code(), "您已在其他地方登录，将被强制下线");
				channelCtx.channel().writeAndFlush(imSendInfo);
				logger.info("异地登录，强制下线，userid:{}", userId);
			}

			//缓存用户和Channel的关系
			UserChannelContextCache.addChannelCtx(userId, terminal, ctx);

			//设置用户相关的属性
			ctx.channel().attr(USER_ID_ATTR).set(userId);

			//设置用户的终端
			ctx.channel().attr(TERMINAL_ATTR).set(terminal);

			//初始化心跳的次数
			ctx.channel().attr(HEARTBEAT_ATTR).set(0L);

			//记录用户的 channelId
			String redisKey = String.join(IMConstants.REDIS_KEY_SPLIT, IMConstants.IM_USER_SERVER_ID, userId.toString(), terminal.toString());
			distributedCacheService.set(redisKey, serverId, IMConstants.ONLINE_TIMEOUT_SECONDS, TimeUnit.SECONDS);

			//响应ws
			IMSendInfo<?> imSendInfo = new IMSendInfo<>();
			imSendInfo.setCmd(IMCmdType.LOGIN.code());
			ctx.channel().writeAndFlush(imSendInfo);

		} catch (Exception e) {
			logger.error("LoginProcessor.process|处理登录信息时发生异常", e);
		}
	}

	/**
	 * 转换对象
	 */
	@Override
	public IMLoginInfo transForm(Object obj) {
		Map<?, ?> map = (Map<?, ?>) obj;
		return BeanUtil.fillBeanWithMap(map, new IMLoginInfo(), false);
	}
}
