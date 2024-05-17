package com.yu.im.server.application.consumer;

import com.alibaba.fastjson.JSONObject;
import com.yu.im.common.domain.constants.IMConstants;
import com.yu.im.common.domain.model.IMReceiveInfo;

/**
 * @author yu
 * @description 基础消息消费者
 * @date 2024-05-17
 */
public class BaseMessageConsumer {
    /**
     * 解析数据
     */
    protected IMReceiveInfo getReceiveMessage(String msg){
        JSONObject jsonObject = JSONObject.parseObject(msg);
        String eventStr = jsonObject.getString(IMConstants.MSG_KEY);
        return JSONObject.parseObject(eventStr, IMReceiveInfo.class);
    }
}
