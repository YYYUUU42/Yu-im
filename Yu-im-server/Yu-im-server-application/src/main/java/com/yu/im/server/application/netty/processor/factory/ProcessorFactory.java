package com.yu.im.server.application.netty.processor.factory;

import com.yu.im.common.domain.enums.IMCmdType;
import com.yu.im.server.application.netty.processor.MessageProcessor;
import com.yu.im.server.application.netty.processor.impl.GroupMessageProcessor;
import com.yu.im.server.application.netty.processor.impl.HeartbeatProcessor;
import com.yu.im.server.application.netty.processor.impl.LoginProcessor;
import com.yu.im.server.application.netty.processor.impl.PrivateMessageProcessor;
import com.yu.im.server.infrastructure.holder.SpringContextHolder;

/**
 * @author yu
 * @description 处理器工厂类
 * @date 2024-05-17
 */
public class ProcessorFactory {

	public static MessageProcessor<?> getProcessor(IMCmdType cmd){
		switch (cmd){
			//登录
			case LOGIN:
				return SpringContextHolder.getApplicationContext().getBean(LoginProcessor.class);
			//心跳
			case HEART_BEAT:
				return SpringContextHolder.getApplicationContext().getBean(HeartbeatProcessor.class);
			//单聊消息
			case PRIVATE_MESSAGE:
				return SpringContextHolder.getApplicationContext().getBean(PrivateMessageProcessor.class);
			//群聊消息
			case GROUP_MESSAGE:
				return SpringContextHolder.getApplicationContext().getBean(GroupMessageProcessor.class);
			default:
				return null;

		}
	}
}
