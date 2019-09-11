package com.tools.service.constant;

public  enum DeployTypeEnum {
    /**
     * 旧版
     */
    DEPLOY_OLD("旧版"),
    /**
     * 资源分离
     */
    DEPLOY_ZYFL("资源分离"),
    /**
     * 上传组件+CM(单tomcat)
     */
    DEPLOY_UPLOAD2CM_1TOMCAT("上传组件+CM(单tomcat)"),
    /**
     * 上传组件+CM
     */
    DEPLOY_UPLOAD2CM("上传组件+CM"),
    /**
     * 上传组件+IPM
     */
    DEPLOY_UPLOAD2IPM("上传组件+IPM"),
    /**
     * 只部CM
     */
    DEPLOY_2UPLOAD_CM("只部CM"),
    /**
     * 只部上传组件
     */
    DEPLOY_2UPLOAD("只部上传组件");

    private String type;

     DeployTypeEnum(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
