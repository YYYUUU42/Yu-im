package com.yu.im.common.domain.enums;


/**
 * @author yu
 * @description 命令类型
 * @date 2024-05-16
 */
public enum IMCmdType {

    LOGIN(0,"登录"),
    HEART_BEAT(1,"心跳"),
    FORCE_DOWN_LINE(2,"强制下线"),
    PRIVATE_MESSAGE(3,"私聊消息"),
    GROUP_MESSAGE(4,"群发消息");


    private final Integer code;

    private final String desc;

    IMCmdType(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static IMCmdType fromCode(Integer code){
        for (IMCmdType typeEnum:values()) {
            if (typeEnum.code.equals(code)) {
                return typeEnum;
            }
        }
        return null;
    }


    public Integer code(){
        return this.code;
    }

}
