package com.tools.service.constant;

public enum CommandMethodEnum {
    /**
     * 同步部署的配置信息
     */
    SYNC_CR_CONFIG("syncCRConfig",100),
    SYNC_APACHE_REPLACE_CONFIG("syncApacheReplaceConfig",101),
    /**
     * 部署方式
     */
    DEPLOY_CM("deployCM",201),
    DEPLOY_ZYFL("deployZYFL",202),
    DEPLOY_UPLOAD("deployUpload",203);

    private String desc;
    private int code;
    private String method;

    CommandMethodEnum(String method, int code ) {
        this.method = method;
        this.code = code;
    }

    CommandMethodEnum( String method, int code , String desc) {
        this.desc = desc;
        this.code = code;
        this.method = method;
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
