package com.tools.service.service.deploy.impl;

import com.tools.commons.utils.PropertyUtils;
import com.tools.service.constant.DeployTypeEnum;
import com.tools.service.constant.ServiceStateEnum;
import com.tools.service.model.CommandModel;
import com.tools.service.model.DeployConfigModel;
import com.tools.service.model.DeployStatusModel;
import com.tools.service.service.deploy.ITomcatDeployService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 上传组件部署处理类
 */
public class UploadTomcatDeployServiceImpl   implements ITomcatDeployService {
    private static  final Logger log = LoggerFactory.getLogger(UploadTomcatDeployServiceImpl.class);

    private DeployConfigModel deployConfigModel;
    private DeployProcessorServiceImpl deployProcessorServiceImpl;
    private ServerControlServiceImpl serverControlServiceImpl;

    public UploadTomcatDeployServiceImpl(DeployConfigModel deployConfigModel) {
        this.deployConfigModel = deployConfigModel;
        deployProcessorServiceImpl = new DeployProcessorServiceImpl();
        serverControlServiceImpl = new ServerControlServiceImpl();
    }

    @Override
    public void clearCacheForTomcat() {
        log.info("清除Tomcat缓存...");
        String uploadTomcatCachePath = deployConfigModel.getUploadDeployConfigMap().get("uploadTomcatCachePath");
        CommandModel commandModel = deployProcessorServiceImpl.deleteOldFiles(uploadTomcatCachePath);
    }

    @Override
    public DeployStatusModel deleteOldFile() {
        log.info("正在删除Tomcat旧ROOT包...");

        String projectName = deployConfigModel.getUploadDeployConfigMap().get("uploadTomcatProjectName");
        String uploadWarPath = deployConfigModel.getUploadDeployConfigMap().get("uploadWarPath");
        String uploadTomcatExportPath = deployConfigModel.getUploadDeployConfigMap().get("uploadTomcatExportPath");
        String cmTomcatExportPath = deployConfigModel.getCmDeployConfigMap().get("cmTomcatExportPath");

        DeployTypeEnum deployTypeEnum = deployConfigModel.getDeployTypeEnum();
        CommandModel commandModel = null;
        File[] listFiles = null;
        switch (deployTypeEnum) {
            //单tomcat处理
            case DEPLOY_UPLOAD2CM_1TOMCAT:
                String absolutePath = new File(cmTomcatExportPath).getParentFile().getAbsolutePath();
                commandModel = deployProcessorServiceImpl.deleteOldFiles(absolutePath + "/" + projectName);
                break;
            default:
                commandModel = deployProcessorServiceImpl.deleteOldFiles(uploadTomcatExportPath);
                //删除单tomcat的上传组件旧文件.过滤的文件夹
                String filter = "ROOT,hhrw";
                String[] filters = filter.split(",");

                List<String> filterList;
                filterList = Arrays.asList(filters);
                File file = new File(cmTomcatExportPath);
                //过滤一部分文件
                listFiles = file.getParentFile().listFiles(pathname -> {
      /*      //将res目录过滤掉
            if (pathname.getName().equals("res")) {
                return false;
            }*/
                    //过滤文件
                    if (filterList.contains(pathname.getName())) {
                        return false;
                    }
                    //过滤文件
                    if (!pathname.isDirectory()) {
                        return false;
                    }
                    return true;
                });
                for (File listFile : listFiles) {
                    log.info(listFile.getAbsolutePath());
                }
        }

        log.info(listFiles == null ? "null" : Arrays.toString(listFiles));
        //判断单tomcat部署后 切换到其模式部署时,要把遗留的上传组件包删除
        if (null != listFiles&&listFiles.length != 0) {
            for (File listFile : listFiles) {
                deployProcessorServiceImpl.deleteOldFiles(listFile.getAbsolutePath());
            }
        }

        DeployStatusModel deployStatusModel = new DeployStatusModel();
        Object excState = commandModel.getProcessExcState();
        if (excState instanceof Boolean) {
            deployStatusModel.setDeployInfo(commandModel.getProcessOutputInfo());
            deployStatusModel.setStatus((Boolean) excState);
            deployStatusModel.setTime(deployStatusModel.getTime());
        }

        return deployStatusModel;
    }

    @Override
    public DeployStatusModel startTomcatForConsole() {
        log.info("正在以控制台启动Tomcat...");

        String uploadTomcatStartUpPath = deployConfigModel.getUploadDeployConfigMap().get("uploadTomcatStartUpPath");
        CommandModel commandModel = serverControlServiceImpl.startServerForCommand(uploadTomcatStartUpPath);

        Object processExcState = commandModel.getProcessExcState();
        DeployStatusModel deployStatusModel = new DeployStatusModel();
        deployStatusModel.setTime(commandModel.getProcessExecTime());
        deployStatusModel.setDeployInfo(commandModel.getProcessOutputInfo());
        if ((boolean) processExcState) {
            deployStatusModel.setStatus(true);
        } else {
            deployStatusModel.setStatus(false);
        }
        return deployStatusModel;
    }

    @Override
    public DeployStatusModel startTomcatForService() {
        log.info("正在以服务启动Tomcat...");

        String uploadTomcatServiceName = deployConfigModel.getUploadDeployConfigMap().get("uploadTomcatServiceName");
        CommandModel commandModel = serverControlServiceImpl.startServerForService(uploadTomcatServiceName);

        Object processExcState = commandModel.getProcessExcState();
        DeployStatusModel deployStatusModel = new DeployStatusModel();
        deployStatusModel.setTime(commandModel.getProcessExecTime());

        deployStatusModel.setDeployInfo(commandModel.getProcessOutputInfo());

        if ((boolean)processExcState) {
            deployStatusModel.setStatus(true);
        }else {
            deployStatusModel.setStatus(false);
        }
        return deployStatusModel;
    }

    @Override
    public DeployStatusModel stopTomcatForConsole() {
        log.info("停止tomcat服务...");
        String uploadTomcatPort;
        DeployTypeEnum deployTypeEnum = deployConfigModel.getDeployTypeEnum();

        switch (deployTypeEnum) {
            //单tomcat处理
            case DEPLOY_UPLOAD2CM_1TOMCAT:
                uploadTomcatPort = deployConfigModel.getCmDeployConfigMap().get("cmTomcatPort");
                break;
            default:
                uploadTomcatPort = deployConfigModel.getUploadDeployConfigMap().get("uploadTomcatPort");
        }


        CommandModel commandModel = serverControlServiceImpl.stopServerForCommand(uploadTomcatPort);
        DeployStatusModel deployStatusModel = new DeployStatusModel();

        deployStatusModel.setTime(commandModel.getProcessExecTime());
        deployStatusModel.setStatus((boolean) commandModel.getProcessExcState());
        deployStatusModel.setDeployInfo(commandModel.getProcessOutputInfo());

        return deployStatusModel;
    }

    @Override
    public DeployStatusModel stopTomcatForService() {
        log.info("停止tomcat服务...");

        String uploadTomcatServiceName = deployConfigModel.getUploadDeployConfigMap().get("uploadTomcatServiceName");

        CommandModel commandModel = serverControlServiceImpl.stopServerForService(uploadTomcatServiceName);
        DeployStatusModel deployStatusModel = new DeployStatusModel();
        deployStatusModel.setTime(commandModel.getProcessExecTime());
        Object processExcState = commandModel.getProcessExcState();

        switch ((ServiceStateEnum) processExcState) {
            case STOPPED:
                deployStatusModel.setStatus(true);
                break;
            case ERROR:
                deployStatusModel.setStatus(false);
                deployStatusModel.setDeployInfo(commandModel.getProcessOutputInfo());
                break;
        }

        return deployStatusModel;
    }

    @Override
    public DeployStatusModel exportWarForTomcat() {
        log.info("正在解压ROOT.war包...");

        String projectName = deployConfigModel.getUploadDeployConfigMap().get("uploadTomcatProjectName");
        String uploadWarPath = deployConfigModel.getUploadDeployConfigMap().get("uploadWarPath");
        String uploadTomcatExportPath = deployConfigModel.getUploadDeployConfigMap().get("uploadTomcatExportPath");
        String cmTomcatExportPath = deployConfigModel.getCmDeployConfigMap().get("cmTomcatExportPath");

        DeployTypeEnum deployTypeEnum = deployConfigModel.getDeployTypeEnum();
        CommandModel commandModel = null;

        switch (deployTypeEnum) {
            //单tomcat处理
            case DEPLOY_UPLOAD2CM_1TOMCAT:
                String absolutePath = new File(cmTomcatExportPath).getParentFile().getAbsolutePath();
                commandModel = deployProcessorServiceImpl.exportWar(uploadWarPath, absolutePath+"/"+projectName);
                break;
            default:
                commandModel = deployProcessorServiceImpl.exportWar(uploadWarPath, uploadTomcatExportPath);
        }

        DeployStatusModel deployStatusModel = new DeployStatusModel();
        deployStatusModel.setStatus((boolean) commandModel.getProcessExcState());
        deployStatusModel.setDeployInfo(commandModel.getProcessOutputInfo());
        deployStatusModel.setTime(commandModel.getProcessExecTime());

        return deployStatusModel;
    }

    @Override
    public void configModifying() {
        log.info("正在修改配置文件...");
        Map<String, String> uploadDeployConfigMap = deployConfigModel.getUploadDeployConfigMap();

        // 上传组件 配置文件修改
        String uploadTomcatExportPath = uploadDeployConfigMap.get("uploadTomcatExportPath");
        String cmTomcatExportPath = deployConfigModel.getCmDeployConfigMap().get("cmTomcatExportPath");
        String projectName = deployConfigModel.getUploadDeployConfigMap().get("uploadTomcatProjectName");

        DeployTypeEnum deployTypeEnum = deployConfigModel.getDeployTypeEnum();

        switch (deployTypeEnum) {
            //单tomcat处理
            case DEPLOY_UPLOAD2CM_1TOMCAT:
                String absolutePath = new File(cmTomcatExportPath).getParentFile().getAbsolutePath();
                uploadTomcatExportPath = absolutePath+File.separator+projectName;
                break;
            default:
        }
        PropertyUtils propertyUtils;
        propertyUtils = new PropertyUtils(new File(uploadTomcatExportPath + "/WEB-INF/classes/stream-config.properties"));
        String apacheServerIp = uploadDeployConfigMap.get("apacheServerIp");
        propertyUtils.getConfiguration2Properties();
        //这个配置 要设置apache的访问ip
        Map<String, String> cmDeployConfigMap = deployConfigModel.getCmDeployConfigMap();
        String cmResourcesPath = cmDeployConfigMap.get("cmResourcesPath");
        propertyUtils.setConfigurationProperty("STREAM_CROSS_SERVER", "http://" + apacheServerIp);
        propertyUtils.setConfigurationProperty("local_base_path", cmResourcesPath.replace("/", "/"));

        String cmDBAddress = cmDeployConfigMap.get("cmDBAddress");
        String cmDBName = cmDeployConfigMap.get("cmDBName");
        String cmDBUserName = cmDeployConfigMap.get("cmDBUserName");
        String cmDBUserPass = cmDeployConfigMap.get("cmDBUserPass");
        dbConfig(uploadTomcatExportPath, cmDBAddress, cmDBName, cmDBUserName, cmDBUserPass);

    }


    private void dbConfig(String rootPath, String dbAddressStr, String dbNameStr, String dbUserNameStr, String dbPassWordStr) {

        String dbConfigPath = rootPath + "/WEB-INF/classes/config/spring/spring.properties";
        PropertyUtils propertyUtils = new PropertyUtils(new File(dbConfigPath));
        propertyUtils.getConfiguration2Properties();

        propertyUtils.setConfigurationProperty("common.db.url", "jdbc:mysql://" + dbAddressStr + "/" + dbNameStr + "?useOldAliasMetadataBehavior=true&characterEncoding=utf-8");
        propertyUtils.setConfigurationProperty("common.db.username", dbUserNameStr);
        propertyUtils.setConfigurationProperty("common.db.password", dbPassWordStr);
    }

}
