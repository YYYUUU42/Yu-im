package com.yu.im.server.application.netty.tcp.codec;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yu.im.common.domain.model.IMSendInfo;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.nio.charset.StandardCharsets;

/**
 * @author yu
 * @description TCP消息编码
 * @date 2024-05-17
 */
public class TcpSocketMessageProtocolEncoder extends MessageToByteEncoder<IMSendInfo<?>> {
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, IMSendInfo<?> imSendInfo, ByteBuf byteBuf) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        String content = objectMapper.writeValueAsString(imSendInfo);
        byte[] bytes = content.getBytes(StandardCharsets.UTF_8);
        byteBuf.writeInt(bytes.length);
        byteBuf.writeBytes(bytes);

    }
}
