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
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author yu
 * @description 群消息处理器
 * @date 2024-05-17
 */
@Component
public class GroupMessageProcessor implements MessageProcessor<IMReceiveInfo> {
    private final Logger logger = LoggerFactory.getLogger(GroupMessageProcessor.class);

    @Autowired
    private MessageSenderService messageSenderService;

    @Async
    @Override
    public void process(IMReceiveInfo receiveInfo) {
        IMUserInfo sender = receiveInfo.getSender();
        List<IMUserInfo> receivers = receiveInfo.getReceivers();
        logger.info("GroupMessageProcessor.process|接收到群消息,发送消息用户:{}，接收消息用户数量:{}，消息内容:{}", sender.getUserId(), receivers.size(), receiveInfo.getData());
        receivers.forEach((receiver) -> {
            try{
                ChannelHandlerContext channelHandlerCtx = UserChannelContextCache.getChannelCtx(receiver.getUserId(), receiver.getTerminal());
                if (channelHandlerCtx != null){
                    //向用户推送消息
                    IMSendInfo<?> imSendInfo = new IMSendInfo<>(IMCmdType.GROUP_MESSAGE.code(), receiveInfo.getData());
                    channelHandlerCtx.writeAndFlush(imSendInfo);
                    //发送确认消息
                    sendResult(receiveInfo, receiver, IMSendCode.SUCCESS);
                }else{
                    //未找到用户的连接信息
                    sendResult(receiveInfo, receiver, IMSendCode.NOT_FIND_CHANNEL);
                    logger.error("GroupMessageProcessor.process|未找到Channel,发送者:{}, 接收者:{}, 消息内容:{}", sender.getUserId(), receiver.getUserId(), receiveInfo.getData());
                }
            }catch (Exception e){
                sendResult(receiveInfo, receiver, IMSendCode.UNKONW_ERROR);
                logger.error("GroupMessageProcessor.process|发送消息异常,发送者:{}, 接收者:{}, 消息内容:{}, 异常信息:{}", sender.getUserId(), receiver.getUserId(), receiveInfo.getData(), e.getMessage());
            }
        });
    }

    /**
     * 发送结果数据
     */
    private void sendResult(IMReceiveInfo receiveInfo, IMUserInfo imUserInfo, IMSendCode imSendCode){
        if (receiveInfo.getSendResult()){
            IMSendResult<?> imSendResult = new IMSendResult<>(receiveInfo.getSender(), imUserInfo, imSendCode.code(), receiveInfo.getData());
            imSendResult.setDestination(IMConstants.IM_RESULT_GROUP_QUEUE);
            messageSenderService.send(imSendResult);
        }
    }
}
