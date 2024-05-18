package com.yu.im.sdk.interfaces.sender.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import com.yu.im.common.cache.distribute.DistributedCacheService;
import com.yu.im.common.domain.constants.IMConstants;
import com.yu.im.common.domain.enums.IMCmdType;
import com.yu.im.common.domain.enums.IMListenerType;
import com.yu.im.common.domain.enums.IMSendCode;
import com.yu.im.common.domain.model.IMPrivateMessage;
import com.yu.im.common.domain.model.IMReceiveInfo;
import com.yu.im.common.domain.model.IMSendResult;
import com.yu.im.common.domain.model.IMUserInfo;
import com.yu.im.common.mq.MessageSenderService;
import com.yu.im.sdk.infrastructure.multicaster.MessageListenerMulticaster;
import com.yu.im.sdk.interfaces.sender.IMSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * @author yu
 * @description 默认消息发送器
 * @date 2024-05-18
 */
@Service
public class DefaultIMSender implements IMSender {

	@Autowired
	private DistributedCacheService distributedCacheService;

	@Autowired
	private MessageSenderService messageSenderService;

	@Autowired
	private MessageListenerMulticaster messageListenerMulticaster;


	/**
	 * 发送私聊消息
	 */
	@Override
	public <T> void sendPrivateMessage(IMPrivateMessage<T> message) {
		if (message == null) {
			return;
		}

		//向用户的终端发送数据
		List<Integer> receiveTerminals = message.getReceiveTerminals();

		//终端不为空
		if (!CollectionUtil.isEmpty(receiveTerminals)) {
			//向目标用户发送私聊消息
			this.sendPrivateMessageToTargetUser(message, receiveTerminals);

			//向自己的其他终端发送私聊消息
			this.sendPrivateMessageToSelf(message, receiveTerminals);
		}
	}

	/**
	 * 向自己的其他终端发送消息
	 */
	private <T> void sendPrivateMessageToSelf(IMPrivateMessage<T> message, List<Integer> receiveTerminals) {
		//向自己的其他终端发送消息
		if (BooleanUtil.isTrue(message.getSendToSelf())) {
			receiveTerminals.forEach((receiveTerminal) -> {

				//向自己的其他终端发送消息
				if (!message.getSender().getTerminal().equals(receiveTerminal)) {
					String redisKey = String.join(IMConstants.REDIS_KEY_SPLIT, IMConstants.IM_USER_SERVER_ID, message.getSender().getUserId().toString(), receiveTerminal.toString());
					String serverId = distributedCacheService.get(redisKey);

					if (!StrUtil.isEmpty(serverId)) {
						String sendKey = String.join(IMConstants.MESSAGE_KEY_SPLIT, IMConstants.IM_MESSAGE_PRIVATE_QUEUE, serverId);
						IMReceiveInfo receiveInfo = new IMReceiveInfo(IMCmdType.PRIVATE_MESSAGE.code(),
								message.getSender(),
								Collections.singletonList(new IMUserInfo(message.getSender().getUserId(), receiveTerminal)),
								false,
								message.getData());
						receiveInfo.setDestination(sendKey);

						messageSenderService.send(receiveInfo);
					}
				}
			});
		}
	}

	/**
	 * 向其他用户发送私聊消息
	 */
	private <T> void sendPrivateMessageToTargetUser(IMPrivateMessage<T> message, List<Integer> receiveTerminals) {
		receiveTerminals.forEach((receiveTerminal) -> {
			//获取接收消息的用户的channelId
			String redisKey = String.join(IMConstants.REDIS_KEY_SPLIT, IMConstants.IM_USER_SERVER_ID, message.getReceiveId().toString(), receiveTerminal.toString());
			String serverId = distributedCacheService.get(redisKey);

			//用户在线，将消息推送到RocketMQ
			if (!StrUtil.isEmpty(serverId)) {
				String sendKey = String.join(IMConstants.MESSAGE_KEY_SPLIT, IMConstants.IM_MESSAGE_PRIVATE_QUEUE, serverId);
				IMReceiveInfo receiveInfo = new IMReceiveInfo(IMCmdType.PRIVATE_MESSAGE.code(),
						message.getSender(),
						Collections.singletonList(new IMUserInfo(message.getReceiveId(), receiveTerminal)),
						message.getSendResult(),
						message.getData());

				//设置发送的主题
				receiveInfo.setDestination(sendKey);

				//发送消息
				messageSenderService.send(receiveInfo);
			} else if (BooleanUtil.isTrue(message.getSendResult())) {
				//回复消息的状态
				IMSendResult<T> result = new IMSendResult<>(message.getSender(), new IMUserInfo(message.getReceiveId(), receiveTerminal), IMSendCode.NOT_ONLINE.code(), message.getData());
				messageListenerMulticaster.multicast(IMListenerType.PRIVATE_MESSAGE, result);
			}
		});
	}
}
