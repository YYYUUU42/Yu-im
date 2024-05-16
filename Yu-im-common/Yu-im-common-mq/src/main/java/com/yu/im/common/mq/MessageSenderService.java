package com.yu.im.common.mq;


import com.yu.im.common.domain.model.TopicMessage;
import org.apache.rocketmq.client.producer.TransactionSendResult;

/**
 * @author yu
 * @description 消息发送服务
 * @date 2024-05-16
 */
public interface MessageSenderService {
	/**
	 * 发送消息
	 *
	 * @param message 发送的消息
	 */
	boolean send(TopicMessage message);

	/**
	 * 发送事务消息，主要是RocketMQ
	 *
	 * @param message 事务消息
	 * @param arg     其他参数
	 * @return 返回事务发送结果
	 */
	default TransactionSendResult sendMessageInTransaction(TopicMessage message, Object arg) {
		return null;
	}
}
