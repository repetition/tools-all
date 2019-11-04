package com.tools.agent.process.listener;

import com.tools.agent.ApplicationConfig;
import com.tools.constant.CommandMethodEnum;
import com.tools.agent.process.constant.PropertKeys;
import com.tools.commons.utils.PropertyUtils;
import com.tools.service.context.ApplicationContext;
import com.tools.service.model.DeployConfigModel;
import com.tools.service.model.DeployState;
import com.tools.service.service.deploy.runnable.DeployModeSelectorProcessorRunnable;
import com.tools.socket.bean.Command;
import io.netty.channel.ChannelHandlerContext;

import java.io.File;
import java.util.Map;

public class OnDeployProcessorListener implements DeployModeSelectorProcessorRunnable.OnDeployProcessorListener {
    private ChannelHandlerContext ctx;

    public OnDeployProcessorListener(ChannelHandlerContext ctx) {
        this.ctx = ctx;
    }

    @Override
    public void onDeployInit() {

        DeployConfigModel deployConfigModel = ApplicationContext.getDeployConfigModel();
        Map<String, String> cmDeployConfigMap = deployConfigModel.getCmDeployConfigMap();
        Map<String, String> zyflDeployConfigMap = deployConfigModel.getZyflDeployConfigMap();
        PropertyUtils propertyUtils = new PropertyUtils(new File(ApplicationConfig.getDeployConfigFilePath()));
        propertyUtils.getConfiguration2Properties();
        propertyUtils.setConfigurationProperty(PropertKeys.CM_TOMCAT_WAR_PATH,cmDeployConfigMap.get("cmWarPathWin"));
        String cmTomcatExportPath = cmDeployConfigMap.get("cmTomcatExportPath");
        String separator = "";
        String osName = System.getProperty("os.name").toLowerCase();
        //linux  只支持服务启动
        if (osName.contains("linux")) {
            separator = "/";
        }else {
            separator = "\\";
        }

        cmTomcatExportPath= cmTomcatExportPath.substring(0, cmTomcatExportPath.lastIndexOf(separator));
        propertyUtils.setConfigurationProperty(PropertKeys.CM_TOMCAT_EXPORTWAR_PATH,cmTomcatExportPath);
        propertyUtils.setConfigurationProperty(PropertKeys.CM_TOMCAT_PORT,cmDeployConfigMap.get("cmTomcatPort"));
        propertyUtils.setConfigurationProperty(PropertKeys.CM_CONFIG_DB_ADDRESS,cmDeployConfigMap.get("cmDBAddress"));
        propertyUtils.setConfigurationProperty(PropertKeys.CM_CONFIG_DB_NAME,cmDeployConfigMap.get("cmDBName"));
        propertyUtils.setConfigurationProperty(PropertKeys.CM_CONFIG_DB_USERNAME,cmDeployConfigMap.get("cmDBUserName"));
        propertyUtils.setConfigurationProperty(PropertKeys.CM_CONFIG_DB_USERPASS,cmDeployConfigMap.get("cmDBUserPass"));
        propertyUtils.setConfigurationProperty(PropertKeys.CM_CONFIG_RESOURCESPATH,cmDeployConfigMap.get("cmResourcesPath"));
        propertyUtils.setConfigurationProperty(PropertKeys.CM_SERVER_IP,cmDeployConfigMap.get("cmServerIp"));

        propertyUtils.setConfigurationProperty(PropertKeys.CM_ZYFLWAR_PATH,zyflDeployConfigMap.get("zyflWarPathWin"));
        propertyUtils.setConfigurationProperty(PropertKeys.CM_ZYFL_EXPORTWAR_PATH,zyflDeployConfigMap.get("apacheHtdocsPath"));

        Map<String, String> uploadDeployConfigMap = deployConfigModel.getUploadDeployConfigMap();
        propertyUtils.setConfigurationProperty(PropertKeys.UPLOAD_WAR_PATH,uploadDeployConfigMap.get("uploadWarPathWin"));
        String uploadTomcatExportPath = uploadDeployConfigMap.get("uploadTomcatExportPath");
        uploadTomcatExportPath= uploadTomcatExportPath.substring(0, uploadTomcatExportPath.lastIndexOf(separator));

        propertyUtils.setConfigurationProperty(PropertKeys.UPLOAD_EXPORTWAR_PATH,uploadTomcatExportPath);
        propertyUtils.setConfigurationProperty(PropertKeys.UPLOAD_TOMCAT_PORT,uploadDeployConfigMap.get("uploadTomcatPort"));
        propertyUtils.setConfigurationProperty(PropertKeys.UPLOAD_TOMCAT_SERVICENAME,uploadDeployConfigMap.get("uploadTomcatServiceName"));
        propertyUtils.setConfigurationProperty(PropertKeys.UPLOAD_TOMCAT_PROJECT_NAME,uploadDeployConfigMap.get("uploadTomcatProjectName"));
        propertyUtils.setConfigurationProperty(PropertKeys.APACHE_SERVER_IP,uploadDeployConfigMap.get("apacheServerIp"));

    }

    @Override
    public void onDeployProcessorStart() {

    }

    @Override
    public void onDeployProcessorEnd() {
        Command command = new Command();
        command.setCommandMethod(CommandMethodEnum.DEPLOY_END.toString());
        command.setCommandCode(CommandMethodEnum.DEPLOY_END.getCode());
        ctx.channel().writeAndFlush(command);
    }

    @Override
    public void onDeployProcessSuccess(DeployState deployState) {
        Command command = new Command();
        command.setCommandMethod(CommandMethodEnum.DEPLOY_START_PROGRESS.toString());
        command.setCommandCode(CommandMethodEnum.DEPLOY_START_PROGRESS.getCode());
        command.setContent(deployState);
        ctx.channel().writeAndFlush(command);
    }

    @Override
    public void onDeployProcessFail(DeployState deployState) {
        Command command = new Command();
        command.setCommandMethod(CommandMethodEnum.DEPLOY_START_FAIL.toString());
        command.setCommandCode(CommandMethodEnum.DEPLOY_START_FAIL.getCode());
        command.setContent(deployState);
        ctx.channel().writeAndFlush(command);
    }
}
