package com.yu.im.server.application.consumer;

import cn.hutool.core.util.StrUtil;
import com.yu.im.common.domain.constants.IMConstants;
import com.yu.im.common.domain.enums.IMCmdType;
import com.yu.im.common.domain.model.IMReceiveInfo;
import com.yu.im.server.application.netty.processor.MessageProcessor;
import com.yu.im.server.application.netty.processor.factory.ProcessorFactory;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQPushConsumerLifecycleListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * @author yu
 * @description 私聊消息消费者
 * @date 2024-05-17
 */
@Component
@ConditionalOnProperty(name = "message.mq.type", havingValue = "rocketmq")
@RocketMQMessageListener(consumerGroup = IMConstants.IM_MESSAGE_PRIVATE_CONSUMER_GROUP, topic = IMConstants.IM_MESSAGE_PRIVATE_NULL_QUEUE)
public class PrivateMessageConsumer extends BaseMessageConsumer implements RocketMQListener<String>, RocketMQPushConsumerLifecycleListener {
    private final Logger logger = LoggerFactory.getLogger(PrivateMessageConsumer.class);

    @Value("${server.id}")
    private Long serverId;

    /**
     * 消息处理
     */
    @Override
    public void onMessage(String message) {
        if (StrUtil.isEmpty(message)){
            logger.warn("PrivateMessageConsumer.onMessage|接收到的消息为空");
            return;
        }
        IMReceiveInfo imReceiveInfo = this.getReceiveMessage(message);
        if (imReceiveInfo == null){
            logger.warn("PrivateMessageConsumer.onMessage|转化后的数据为空");
            return;
        }
        MessageProcessor processor = ProcessorFactory.getProcessor(IMCmdType.PRIVATE_MESSAGE);
        processor.process(imReceiveInfo);
    }

    /**
     * 准备启动
     */
    @Override
    public void prepareStart(DefaultMQPushConsumer consumer) {
        try{
            String topic = String.join(IMConstants.MESSAGE_KEY_SPLIT, IMConstants.IM_MESSAGE_PRIVATE_QUEUE, String.valueOf(serverId));
            consumer.subscribe(topic, "*");
        }catch (Exception e){
            logger.error("PrivateMessageConsumer.prepareStart|异常:{}", e.getMessage());
        }
    }
}
