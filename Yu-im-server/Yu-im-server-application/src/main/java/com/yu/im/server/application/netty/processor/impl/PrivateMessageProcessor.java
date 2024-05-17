package com.yu.im.server.application.netty.processor.impl;

import com.yu.im.common.domain.constants.IMConstants;
import com.yu.im.common.domain.enums.IMCmdType;
import com.yu.im.common.domain.enums.IMSendCode;
import com.yu.im.common.domain.model.IMReceiveInfo;
import com.yu.im.common.domain.model.IMSendInfo;
import com.yu.im.common.domain.model.IMSendResult;
import com.yu.im.common.domain.model.IMUserInfo;
import com.yu.im.common.mq.MessageSenderService;
import com.yu.im.server.application.netty.cache.UserChannelContextCache;
import com.yu.im.server.application.netty.processor.MessageProcessor;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author yu
 * @description 私聊消息处理器
 * @date 2024-05-17
 */
@Component
public class PrivateMessageProcessor implements MessageProcessor<IMReceiveInfo> {
    private final Logger logger = LoggerFactory.getLogger(PrivateMessageProcessor.class);

    @Autowired
    private MessageSenderService messageSenderService;

    @Override
    public void process(IMReceiveInfo receiveInfo) {
        IMUserInfo sender = receiveInfo.getSender();
        IMUserInfo receiver = receiveInfo.getReceivers().get(0);
        logger.info("PrivateMessageProcessor.process|接收到消息,发送者:{}, 接收者:{}, 内容:{}", sender.getUserId(), receiver.getUserId(), receiveInfo.getData());
        try{
            ChannelHandlerContext channelHandlerContext = UserChannelContextCache.getChannelCtx(receiver.getUserId(), receiver.getTerminal());
            if (channelHandlerContext != null){
                //推送消息
                IMSendInfo<?> imSendInfo = new IMSendInfo<>(IMCmdType.PRIVATE_MESSAGE.code(), receiveInfo.getData());
                channelHandlerContext.writeAndFlush(imSendInfo);
                sendResult(receiveInfo, IMSendCode.SUCCESS);
            }else{
                sendResult(receiveInfo, IMSendCode.NOT_FIND_CHANNEL);
                logger.error("PrivateMessageProcessor.process|未找到Channel, 发送者:{}, 接收者:{}, 内容:{}", sender.getUserId(), receiver.getUserId(), receiveInfo.getData());
            }
        }catch (Exception e){
            sendResult(receiveInfo, IMSendCode.UNKONW_ERROR);
            logger.error("PrivateMessageProcessor.process|发送异常,发送者:{}, 接收者:{}, 内容:{}, 异常信息:{}", sender.getUserId(), receiver.getUserId(), receiveInfo.getData(), e.getMessage());
        }
    }

    private void sendResult(IMReceiveInfo receiveInfo, IMSendCode sendCode){
        if (receiveInfo.getSendResult()){
            IMSendResult<?> result = new IMSendResult<>(receiveInfo.getSender(), receiveInfo.getReceivers().get(0), sendCode.code(), receiveInfo.getData());
            String sendKey = IMConstants.IM_RESULT_PRIVATE_QUEUE;
            result.setDestination(sendKey);
            messageSenderService.send(result);
        }
    }
}
