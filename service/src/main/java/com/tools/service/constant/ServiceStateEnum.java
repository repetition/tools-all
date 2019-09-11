package com.tools.service.constant;

public enum ServiceStateEnum {

    STATED("服务已经启动成功"),
    START_PENDING("服务正在启动"),
    STOPPED("服务已成功停止"),
    STOP_PENDING("服务正在停止"),
    ERROR("服务无法停止"),
    NOT_EXIST("服务名无效");

    private String desc;

    ServiceStateEnum(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

}
