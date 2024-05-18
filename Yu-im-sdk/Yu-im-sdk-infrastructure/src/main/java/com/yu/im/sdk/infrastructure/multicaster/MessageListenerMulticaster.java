package com.yu.im.sdk.infrastructure.multicaster;

import com.yu.im.common.domain.enums.IMListenerType;
import com.yu.im.common.domain.model.IMSendResult;

/**
 * @author yu
 * @description 广播消息
 * @date 2024-05-18
 */
public interface MessageListenerMulticaster {

	/**
	 * 广播消息
	 *
	 * @param listenerType 监听的类型
	 * @param result       发送消息的结果
	 * @param <T>          泛型类型
	 */
	<T> void multicast(IMListenerType listenerType, IMSendResult<T> result);
}
