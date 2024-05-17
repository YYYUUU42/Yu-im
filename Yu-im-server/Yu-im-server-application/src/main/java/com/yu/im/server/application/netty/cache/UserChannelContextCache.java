package com.yu.im.server.application.netty.cache;

import io.netty.channel.ChannelHandlerContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author yu
 * @description 用于缓存 userId 和 ChannelHandlerContext 的关系
 * @date 2024-05-17
 */
public class UserChannelContextCache {

	/**
	 * 缓存userId和ChannelHandlerContext的关系
	 * 主要格式：Map<userId, Map<terminal, ctx>>
	 */
	private static Map<Long, Map<Integer, ChannelHandlerContext>> channelMap = new ConcurrentHashMap<>();

	/**
	 * 添加 ChannelHandlerContext
	 */
	public static void addChannelCtx(Long userId, Integer channel, ChannelHandlerContext ctx) {
		channelMap.computeIfAbsent(userId, key -> new ConcurrentHashMap<>()).put(channel, ctx);
	}

	/**
	 * 移除 ChannelHandlerContext
	 */
	public static void removeChannelCtx(Long userId, Integer terminal) {
		if (userId != null && terminal != null && channelMap.containsKey(userId)) {
			Map<Integer, ChannelHandlerContext> userChannelMap = channelMap.get(userId);
			if (userChannelMap.containsKey(terminal)) {
				userChannelMap.remove(terminal);
			}
		}
	}

	/**
	 * 获取 ChannelHandlerContext
	 */
	public static ChannelHandlerContext getChannelCtx(Long userId, Integer terminal){
		if (userId != null && terminal != null && channelMap.containsKey(userId)){
			Map<Integer, ChannelHandlerContext> userChannelMap = channelMap.get(userId);
			if (userChannelMap.containsKey(terminal)){
				return userChannelMap.get(terminal);
			}
		}
		return null;
	}

	/**
	 * 获取 ChannelHandlerContext
	 */
	public static Map<Integer, ChannelHandlerContext> getChannelCtx(Long userId){
		if (userId == null){
			return null;
		}
		return channelMap.get(userId);
	}
}
