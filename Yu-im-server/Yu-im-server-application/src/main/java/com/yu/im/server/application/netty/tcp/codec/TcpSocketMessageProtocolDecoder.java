package com.yu.im.server.application.netty.tcp.codec;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yu.im.common.domain.constants.IMConstants;
import com.yu.im.common.domain.model.IMSendInfo;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @author yu
 * @description TCP 消息解码类
 * @date 2024-05-17
 */
public class TcpSocketMessageProtocolDecoder extends ReplayingDecoder {
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        if (byteBuf.readableBytes() < IMConstants.MIN_READABLE_BYTES){
            return;
        }

        //获取数据包长度
        int length = byteBuf.readInt();
        ByteBuf contentBuf = byteBuf.readBytes(length);
        String content = contentBuf.toString(StandardCharsets.UTF_8);

        ObjectMapper objectMapper = new ObjectMapper();
        IMSendInfo imSendInfo = objectMapper.readValue(content, IMSendInfo.class);
        list.add(imSendInfo);
    }
}
