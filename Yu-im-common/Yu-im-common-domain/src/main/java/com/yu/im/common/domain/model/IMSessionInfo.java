package com.yu.im.common.domain.model;

/**
 * @author yu
 * @description Session信息
 * @date 2024-05-16
 */
public class IMSessionInfo {

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 终端类型
     */
    private Integer terminal;

    public IMSessionInfo() {
    }

    public IMSessionInfo(Long userId, Integer terminal) {
        this.userId = userId;
        this.terminal = terminal;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Integer getTerminal() {
        return terminal;
    }

    public void setTerminal(Integer terminal) {
        this.terminal = terminal;
    }
}
