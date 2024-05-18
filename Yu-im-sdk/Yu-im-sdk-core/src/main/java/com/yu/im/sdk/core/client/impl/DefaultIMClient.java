package com.yu.im.sdk.core.client.impl;

import com.yu.im.common.domain.model.IMPrivateMessage;
import com.yu.im.sdk.core.client.IMClient;
import com.yu.im.sdk.interfaces.sender.IMSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author yu
 * @description 默认的客户端实现
 * @date 2024-05-18
 */
@Service
public class DefaultIMClient implements IMClient {

	@Autowired
	private IMSender imSender;

	/**
	 * 发送私聊消息
	 */
	@Override
	public <T> void sendPrivateMessage(IMPrivateMessage<T> message) {
		imSender.sendPrivateMessage(message);
	}
}
