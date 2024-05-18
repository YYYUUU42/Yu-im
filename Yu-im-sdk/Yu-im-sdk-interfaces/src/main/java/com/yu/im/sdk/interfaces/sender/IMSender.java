package com.yu.im.sdk.interfaces.sender;

import com.yu.im.common.domain.model.IMPrivateMessage;

/**
 * @author yu
 * @description 消息发送接口
 * @date 2024-05-18
 */
public interface IMSender {

    /**
     * 发送私聊消息
     */
    <T> void sendPrivateMessage(IMPrivateMessage<T> message);
}
