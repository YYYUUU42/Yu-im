package com.yu.im.server.application.netty.websocket.codec;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yu.im.common.domain.model.IMSendInfo;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

import java.util.List;

/**
 * @author yu
 * @description WebSocket 解码器
 * @date 2024-05-17
 */
public class WebSocketMessageProtocolDecoder extends MessageToMessageDecoder<TextWebSocketFrame> {
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, TextWebSocketFrame textWebSocketFrame, List<Object> list) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        IMSendInfo imSendInfo = objectMapper.readValue(textWebSocketFrame.text(), IMSendInfo.class);
        list.add(imSendInfo);
    }
}
