package com.yu.im.sdk.core.client;

import com.yu.im.common.domain.model.IMPrivateMessage;

/**
 * @author yu
 * @description IM客户端
 * @date 2024-05-18
 */
public interface IMClient {
	/**
	 * 发送私聊消息
	 */
	<T> void sendPrivateMessage(IMPrivateMessage<T> message);
}
