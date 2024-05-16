package com.yu.im.common.domain.model;

import java.io.Serializable;

/**
 * @author yu
 * @description 基础消息
 * @date 2024-05-16
 */
public class TopicMessage implements Serializable {

    private static final long serialVersionUID = -6489577536014906245L;
    /**
     * 消息的目的地，可以是消息的主题
     */
    private String destination;

    public TopicMessage() {
    }

    public TopicMessage(String destination) {
        this.destination = destination;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }
}
