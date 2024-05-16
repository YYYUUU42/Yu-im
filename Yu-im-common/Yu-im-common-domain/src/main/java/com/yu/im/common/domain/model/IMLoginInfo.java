package com.yu.im.common.domain.model;

/**
 * @author yu
 * @description 登录信息
 * @date 2024-05-16
 */
public class IMLoginInfo {
    /**
     * access-token
     */
    private String accessToken;

    public IMLoginInfo() {
    }

    public IMLoginInfo(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}
