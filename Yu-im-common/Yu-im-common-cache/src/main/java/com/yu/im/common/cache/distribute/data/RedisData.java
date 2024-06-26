package com.yu.im.common.cache.distribute.data;

import java.time.LocalDateTime;

/**
 * @author yu
 * @description 缓存到Redis中的数据，主要配合使用数据的逻辑过期
 * @date 2024-05-16
 */
public class RedisData {
	/**
	 * 实际业务数据
	 */
	private Object data;

	/**
	 * 过期时间点
	 */
	private LocalDateTime expireTime;

	public RedisData() {
	}

	public RedisData(Object data, LocalDateTime expireTime) {
		this.data = data;
		this.expireTime = expireTime;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	public LocalDateTime getExpireTime() {
		return expireTime;
	}

	public void setExpireTime(LocalDateTime expireTime) {
		this.expireTime = expireTime;
	}
}
