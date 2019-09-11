package com.tools.service.constant;

public enum  ServerStartTypeEnum {

    SERVICE("服务"),CONSOLE("控制台");

    private String desc;

    ServerStartTypeEnum(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }
}
