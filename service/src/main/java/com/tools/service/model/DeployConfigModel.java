package com.tools.service.model;

import com.tools.service.constant.DeployTypeEnum;
import com.tools.service.constant.ServerStartTypeEnum;

import java.io.Serializable;
import java.util.Map;

public class DeployConfigModel implements Serializable {
    private static final long serialVersionUID = 3L;

    /**
     * cm部署配置
     */
    private Map<String,String> cmDeployConfigMap;
    /**
     * 资源分离配置
     */
    private Map<String,String> zyflDeployConfigMap;
    /**
     * 上传组件配置
     */
    private Map<String,String> uploadDeployConfigMap;
    /**
     * 部署的类型
     */
    private Map<String,Boolean> deployModeSelectorMap;



    private DeployTypeEnum deployTypeEnum;
    /**
     *
     */
    private boolean isServerStart;

    private ServerStartTypeEnum serverStartTypeEnum;

    private String httpdOldChangedPath;

    private String httpdZYFLChangedPath;

    private String httpdUploadChangedPath;

    private String httpdUpload1TomcatChangedPath;

    private String httpdIPMChangedPath;

    private String workersOldChangedPath;

    private String wordkersUploadChangedPath;

    public boolean isServerStart() {
        return isServerStart;
    }

    public void setServerStart(boolean serverStart) {
        isServerStart = serverStart;
    }

    public ServerStartTypeEnum getServerStartTypeEnum() {
        return serverStartTypeEnum;
    }

    public void setServerStartTypeEnum(ServerStartTypeEnum serverStartTypeEnum) {
        this.serverStartTypeEnum = serverStartTypeEnum;
    }

    public DeployTypeEnum getDeployTypeEnum() {
        return deployTypeEnum;
    }

    public void setDeployTypeEnum(DeployTypeEnum deployTypeEnum) {
        this.deployTypeEnum = deployTypeEnum;
    }

    public String getHttpdOldChangedPath() {
        return httpdOldChangedPath;
    }

    public void setHttpdOldChangedPath(String httpdOldChangedPath) {
        this.httpdOldChangedPath = httpdOldChangedPath;
    }

    public String getHttpdZYFLChangedPath() {
        return httpdZYFLChangedPath;
    }

    public void setHttpdZYFLChangedPath(String httpdZYFLChangedPath) {
        this.httpdZYFLChangedPath = httpdZYFLChangedPath;
    }

    public String getHttpdUploadChangedPath() {
        return httpdUploadChangedPath;
    }

    public void setHttpdUploadChangedPath(String httpdUploadChangedPath) {
        this.httpdUploadChangedPath = httpdUploadChangedPath;
    }

    public String getHttpdIPMChangedPath() {
        return httpdIPMChangedPath;
    }

    public void setHttpdIPMChangedPath(String httpdIPMChangedPath) {
        this.httpdIPMChangedPath = httpdIPMChangedPath;
    }

    public String getWorkersOldChangedPath() {
        return workersOldChangedPath;
    }

    public void setWorkersOldChangedPath(String workersOldChangedPath) {
        this.workersOldChangedPath = workersOldChangedPath;
    }

    public String getWordkersUploadChangedPath() {
        return wordkersUploadChangedPath;
    }

    public void setWordkersUploadChangedPath(String wordkersUploadChangedPath) {
        this.wordkersUploadChangedPath = wordkersUploadChangedPath;
    }

    public Map<String, String> getCmDeployConfigMap() {
        return cmDeployConfigMap;
    }

    public void setCmDeployConfigMap(Map<String, String> cmDeployConfigMap) {
        this.cmDeployConfigMap = cmDeployConfigMap;
    }

    public Map<String, String> getZyflDeployConfigMap() {
        return zyflDeployConfigMap;
    }

    public void setZyflDeployConfigMap(Map<String, String> zyflDeployConfigMap) {
        this.zyflDeployConfigMap = zyflDeployConfigMap;
    }

    public Map<String, String> getUploadDeployConfigMap() {
        return uploadDeployConfigMap;
    }

    public void setUploadDeployConfigMap(Map<String, String> uploadDeployConfigMap) {
        this.uploadDeployConfigMap = uploadDeployConfigMap;
    }

    public void setDeployModeSelectorMap(Map<String, Boolean> map) {
        this.deployModeSelectorMap = map;
    }

    public Map<String, Boolean> getDeployModeSelectorMap() {
        return deployModeSelectorMap;
    }

    public String getHttpdUpload1TomcatChangedPath() {
        return httpdUpload1TomcatChangedPath;
    }

    public DeployConfigModel setHttpdUpload1TomcatChangedPath(String httpdUpload1TomcatChangedPath) {
        this.httpdUpload1TomcatChangedPath = httpdUpload1TomcatChangedPath;
        return this;
    }
    @Override
    public String toString() {
        return "DeployConfigModel{" +
                "cmDeployConfigMap=" + cmDeployConfigMap +
                ", zyflDeployConfigMap=" + zyflDeployConfigMap +
                ", uploadDeployConfigMap=" + uploadDeployConfigMap +
                ", deployModeSelectorMap=" + deployModeSelectorMap +
                ", deployTypeEnum=" + deployTypeEnum +
                ", isServerStart=" + isServerStart +
                ", serverStartTypeEnum=" + serverStartTypeEnum +
                ", httpdOldChangedPath='" + httpdOldChangedPath + '\'' +
                ", httpdZYFLChangedPath='" + httpdZYFLChangedPath + '\'' +
                ", httpdUploadChangedPath='" + httpdUploadChangedPath + '\'' +
                ", httpdIPMChangedPath='" + httpdIPMChangedPath + '\'' +
                ", workersOldChangedPath='" + workersOldChangedPath + '\'' +
                ", wordkersUploadChangedPath='" + wordkersUploadChangedPath + '\'' +
                '}';
    }
}
