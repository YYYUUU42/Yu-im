package com.yu.im.common.domain.enums;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


/**
 * @author yu
 * @description 终端类型
 * @date 2024-05-16
 */
public enum IMTerminalType {

    WEB(0,"web"),
    APP(1,"app");

    private final Integer code;

    private final String desc;

    IMTerminalType(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static IMTerminalType fromCode(Integer code){
        for (IMTerminalType typeEnum:values()) {
            if (typeEnum.code.equals(code)) {
                return typeEnum;
            }
        }
        return null;
    }

    public static List<Integer> codes(){
        return Arrays.stream(values()).map(IMTerminalType::code).collect(Collectors.toList());
    }

    public Integer code(){
        return this.code;
    }
}
