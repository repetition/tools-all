package com.tools.agent.process;

public enum CommandMethodEnum {
    //配置同步
    SYNC_CR_CONFIG("同步CM部署配置",3000),
    SYNC_APACHE_CONFIG("同步Apache部署配置",3001),
    //部署时的同步
    SYNC_CM_WAR("同步cm包",2001),
    SYNC_ZYFL_WAR("同步资源分离包",2002),
    SYNC_UPLOAD_WAR("同步上传组件包",2003),
    SYNC_DEPLOY_CONFIG("同步部署时配置",2004),
    //部署指令
    DEPLOY_INIT("部署准备",1000),
    DEPLOY_START("开始部署",1001);

    private String desc;
    private int code;

     CommandMethodEnum(String desc, int code) {
        this.desc = desc;
         this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public int getCode() {
        return code;
    }


    public static CommandMethodEnum getEnum(int code){

        for (CommandMethodEnum commandMethodEnum : CommandMethodEnum.values()) {
            if (commandMethodEnum.getCode() == code) {
                return commandMethodEnum;
            }
        }
        return null;
    }
}