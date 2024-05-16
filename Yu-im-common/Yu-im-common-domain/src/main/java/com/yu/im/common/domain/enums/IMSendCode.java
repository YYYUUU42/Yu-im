package com.yu.im.common.domain.enums;

/**
 * @author yu
 * @description 发送编码
 * @date 2024-05-16
 */
public enum IMSendCode {

    SUCCESS(0,"发送成功"),
    NOT_ONLINE(1,"对方当前不在线"),
    NOT_FIND_CHANNEL(2,"未找到对方的channel"),
    UNKONW_ERROR(9999,"未知异常");

    private final Integer code;
    private final String desc;

    IMSendCode(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public Integer code(){
        return this.code;
    }

}
