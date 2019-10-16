package com.tools.service.service.deploy.impl;

import com.google.gson.Gson;
import com.tools.commons.utils.PropertyUtils;
import com.tools.service.constant.ServiceStateEnum;
import com.tools.service.context.ApplicationContext;
import com.tools.service.model.CommandModel;
import com.tools.service.model.DeployConfigModel;
import com.tools.service.model.DeployStatusModel;
import com.tools.service.service.deploy.ITomcatDeployService;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

/**
 * cm部署处理类
 */
public class CMTomcatDeployServiceImpl implements ITomcatDeployService {
    private static  final Logger log = LoggerFactory.getLogger(CMTomcatDeployServiceImpl.class);

    private DeployConfigModel deployConfigModel;
    private DeployProcessorServiceImpl deployProcessorServiceImpl;
    private ServerControlServiceImpl serverControlServiceImpl;
    private final PropertiesModifyServiceImpl propertiesModifyServiceImpl;
    private final XMLModifyServiceImpl xmlModifyServiceImpl;

    public CMTomcatDeployServiceImpl(DeployConfigModel deployConfigModel) {
        this.deployConfigModel = deployConfigModel;
        deployProcessorServiceImpl = new DeployProcessorServiceImpl();
        serverControlServiceImpl = new ServerControlServiceImpl();
        propertiesModifyServiceImpl = new PropertiesModifyServiceImpl();
        xmlModifyServiceImpl = new XMLModifyServiceImpl();
    }

    @Override
    public void clearCacheForTomcat() {
        log.info("清除Tomcat缓存...");
        String cmTomcatCachePath = deployConfigModel.getCmDeployConfigMap().get("cmTomcatCachePath");
        CommandModel commandModel = deployProcessorServiceImpl.deleteOldFiles(cmTomcatCachePath);
        log.info("清除Tomcat缓存成功!");
    }


    @Override
    public DeployStatusModel deleteOldFile() {
        log.info("正在删除Tomcat旧ROOT包...");
        String cmTomcatRootPath = deployConfigModel.getCmDeployConfigMap().get("cmTomcatRootPath");
        CommandModel commandModel = deployProcessorServiceImpl.deleteOldFiles(cmTomcatRootPath);

        DeployStatusModel deployStatusModel = new DeployStatusModel();

        Object excState = commandModel.getProcessExcState();

        if (excState instanceof  Boolean) {
            deployStatusModel.setDeployInfo(commandModel.getProcessOutputInfo());
            deployStatusModel.setStatus((Boolean) excState);
            deployStatusModel.setTime(deployStatusModel.getTime());
        }

        return deployStatusModel;
    }

    @Override
    public DeployStatusModel startTomcatForConsole() {

        log.info("正在以控制台启动Tomcat...");
        String cmTomcatStartUpPath = deployConfigModel.getCmDeployConfigMap().get("cmTomcatStartUpPath");
        CommandModel commandModel = serverControlServiceImpl.startServerForCommand(cmTomcatStartUpPath);

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
        String cmTomcatServiceName = deployConfigModel.getCmDeployConfigMap().get("cmTomcatServiceName");
        CommandModel commandModel = serverControlServiceImpl.startServerForService(cmTomcatServiceName);

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
        String cmTomcatPort = deployConfigModel.getCmDeployConfigMap().get("cmTomcatPort");

        CommandModel commandModel = serverControlServiceImpl.stopServerForCommand(cmTomcatPort);
        DeployStatusModel deployStatusModel = new DeployStatusModel();

        deployStatusModel.setTime(commandModel.getProcessExecTime());
        deployStatusModel.setStatus((boolean) commandModel.getProcessExcState());
        if ((boolean) commandModel.getProcessExcState()) {
            //重新记录服务的状态
            deployConfigModel.setServerStart(false);
        }else {
            deployConfigModel.setServerStart(true);
        }

        deployStatusModel.setDeployInfo(commandModel.getProcessOutputInfo());

        return deployStatusModel;
    }

    @Override
    public DeployStatusModel stopTomcatForService() {
        log.info("停止tomcat服务...");
        String cmTomcatServiceName = deployConfigModel.getCmDeployConfigMap().get("cmTomcatServiceName");

        CommandModel commandModel = serverControlServiceImpl.stopServerForService(cmTomcatServiceName);
        DeployStatusModel deployStatusModel = new DeployStatusModel();
        deployStatusModel.setTime(commandModel.getProcessExecTime());
        Object processExcState = commandModel.getProcessExcState();

        switch ((ServiceStateEnum) processExcState) {
            case STOPPED:
                //重新记录服务的状态
                deployConfigModel.setServerStart(false);
                deployStatusModel.setStatus(true);
                break;
            case ERROR:
                deployConfigModel.setServerStart(true);
                deployStatusModel.setStatus(false);
                deployStatusModel.setDeployInfo(commandModel.getProcessOutputInfo());
                break;
        }

        return deployStatusModel;
    }

    @Override
    public DeployStatusModel exportWarForTomcat() {
        log.info("正在解压ROOT.war包...");
        String cmWarPath = deployConfigModel.getCmDeployConfigMap().get("cmWarPath");
        String cmTomcatExportPath = deployConfigModel.getCmDeployConfigMap().get("cmTomcatExportPath");

        CommandModel commandModel = deployProcessorServiceImpl.exportWar(cmWarPath, cmTomcatExportPath);

        DeployStatusModel deployStatusModel = new DeployStatusModel();

        deployStatusModel.setStatus((boolean) commandModel.getProcessExcState());
        deployStatusModel.setDeployInfo(commandModel.getProcessOutputInfo());
        deployStatusModel.setTime(commandModel.getProcessExecTime());

        return deployStatusModel;
    }

    @Override
    public void configModifying() {
        log.info("正在修改配置文件...");
//旧版配置修改
        oldCustomPropertiesChanged();
        // TODO: 2019/9/30 部署时修改配置文件稍后修改
        propertiesChanged();
    }

    @Override
    public DeployStatusModel deleteWarFile() {
        log.info("正在删除Tomcat旧ROOT包...");
        String cmTomcatWarPath = deployConfigModel.getCmDeployConfigMap().get("cmWarPath");
        CommandModel commandModel = deployProcessorServiceImpl.deleteOldFiles(cmTomcatWarPath);

        DeployStatusModel deployStatusModel = new DeployStatusModel();

        Object excState = commandModel.getProcessExcState();

        if (excState instanceof  Boolean) {
            deployStatusModel.setDeployInfo(commandModel.getProcessOutputInfo());
            deployStatusModel.setStatus((Boolean) excState);
            deployStatusModel.setTime(deployStatusModel.getTime());
        }

        return deployStatusModel;
    }

    private void oldCustomPropertiesChanged() {

        Map<String, String> cmDeployConfigMap = deployConfigModel.getCmDeployConfigMap();
        String cmTomcatExportPath = cmDeployConfigMap.get("cmTomcatExportPath");
        String cmDBAddress = cmDeployConfigMap.get("cmDBAddress");
        String cmDBName = cmDeployConfigMap.get("cmDBName");
        String cmDBUserName = cmDeployConfigMap.get("cmDBUserName");
        String cmDBUserPass = cmDeployConfigMap.get("cmDBUserPass");
        String cmResourcesPath = cmDeployConfigMap.get("cmResourcesPath");
        setConfig(cmTomcatExportPath,cmDBAddress,cmDBName,cmDBUserName,cmDBUserPass,cmResourcesPath);
    }

    /**
     * 修改spring.properties 和 publish.cfg.xml 配置文件
     *
     * @param path ROOT路径
     */
    public void setConfig(String path, String dbAddressStr, String dbNameStr, String dbUserNameStr, String dbPassWordStr, String resourcePath) {
        dbConfig(path, dbAddressStr, dbNameStr, dbUserNameStr, dbPassWordStr);
        //本机ip设置
        String ip = deployConfigModel.getCmDeployConfigMap().get("localIp");
        streamConfig(path, resourcePath);
        publishCfg2(path, resourcePath, ip);
        integrationCfg(path, ip);

    }

    public void dbConfig(String rootPath, String dbAddressStr, String dbNameStr, String dbUserNameStr, String dbPassWordStr) {

        String dbConfigPath = rootPath + "/WEB-INF/classes/config/spring/spring.properties";
        PropertyUtils propertyUtils = new PropertyUtils(new File(dbConfigPath));
        propertyUtils.getConfiguration2Properties();

        propertyUtils.setConfigurationProperty("common.db.url", "jdbc:mysql://" + dbAddressStr + "/" + dbNameStr + "?useOldAliasMetadataBehavior=true&characterEncoding=utf-8");
        propertyUtils.setConfigurationProperty("common.db.username", dbUserNameStr);
        propertyUtils.setConfigurationProperty("common.db.password", dbPassWordStr);
    }

    public void streamConfig(String rootPath, String resourcePath) {
        String streamConfigPath = rootPath + "/WEB-INF/classes/stream-config.properties";
        File file = new File(streamConfigPath);
        if (file.exists()) {
            PropertyUtils propertyUtils = new PropertyUtils(file);
            propertyUtils.getConfiguration2Properties();
            propertyUtils.setConfigurationProperty("local_base_path", resourcePath.replace("/", "/"));
        } else {
        }
    }
    public void publishCfg2(String rootPath, String resourcePath, String ip) {
        try {
            String publishXml = rootPath + "/WEB-INF/classes/config/publish.cfg.xml";

            SAXReader saxReader = new SAXReader();
            org.dom4j.Document document = saxReader.read(new File(publishXml));
            org.dom4j.Element rootElement = document.getRootElement();

            List listPwd = rootElement.selectNodes("/config/baseResourcePath");
            org.dom4j.Element elementResPath = (org.dom4j.Element) listPwd.get(0);

            String resPath = elementResPath.getName();
            String resPathText = elementResPath.getText();

            elementResPath.setText(resourcePath);


            List listIntranetIp = rootElement.selectNodes("/config/intranetIp");
            if (!listIntranetIp.isEmpty()) {
                org.dom4j.Node nodeIntranetIp = (org.dom4j.Node) listIntranetIp.get(0);
                String intranetIpName = nodeIntranetIp.getName();
                String intranetIpText = nodeIntranetIp.getText();
                //获取内网ip
                nodeIntranetIp.setText(ip);

            }

            List listSignUrl = rootElement.selectNodes("/config/signUrl");
            if (!listSignUrl.isEmpty()) {
                org.dom4j.Node nodeSignUrl = (org.dom4j.Node) listSignUrl.get(0);
                String signUrlName = nodeSignUrl.getName();
                String signUrlText = nodeSignUrl.getText();

                nodeSignUrl.setText(ip);
            }
            xmlModifyServiceImpl.saveXml(publishXml, document);
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void integrationCfg(String rootPath, String ip) {
        String integrationXml = rootPath + "/WEB-INF/classes/config/integration.cfg.xml";

        SAXReader saxReader = new SAXReader();
        org.dom4j.Document document = null;
        try {
            document = saxReader.read(new File(integrationXml));
            org.dom4j.Element rootElement = document.getRootElement();
            List listMissionH5Path = rootElement.selectNodes("/config/missionH5Path");

            if (!listMissionH5Path.isEmpty()) {
                org.dom4j.Element elementMissionH5Path = (org.dom4j.Element) listMissionH5Path.get(0);
                String h5PathName = elementMissionH5Path.getName();
                String h5PathText = elementMissionH5Path.getText();

                elementMissionH5Path.setText("http://" + ip + "/www");
            }
            xmlModifyServiceImpl.saveXml(integrationXml, document);
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void propertiesChanged() {
        // TODO: 2019/9/12  需要添加配置文件列表
        Properties properties = new PropertyUtils(ApplicationContext.getConfigListFilePath()).getOrderedProperties();
        //获取配置文件列表
        Set<String> propertyNames = properties.stringPropertyNames();

        for (String propertyName : propertyNames) {
            //获取每个配置文件的路径信息
            String filePath = properties.getProperty(propertyName);
            File file = new File(filePath);
            File changedPropertiesFile = new File(ApplicationContext.getApplicationConfPath() + file.getName() + ".Changed.properties");
            //运行时配置文件不存在则跳过
            if (!changedPropertiesFile.exists()) {
                continue;
            }
            //获取每个配置文件要修改的内容
            PropertyUtils propertyUtils = new PropertyUtils(changedPropertiesFile);
            propertyUtils.getOrderedProperties();
            String changed = propertyUtils.getOrderedPropertyStringByKey("changed");
            Gson gson = new Gson();
            LinkedHashMap<String, String> linkedHashMap = gson.fromJson(changed, LinkedHashMap.class);
            //运行时配置文件内容为空时跳过
            if (null == linkedHashMap) {
                continue;
            }
            //获取后缀名
            String suffixName = file.getName().substring(file.getName().lastIndexOf("."), file.getName().length());

            if (suffixName.equals(".properties")) {
                propertiesModifyServiceImpl.configModifying(filePath, linkedHashMap);
            }
            if (suffixName.equals(".xml")) {
                xmlModifyServiceImpl.configModifying(filePath, linkedHashMap);
            }
        }
    }
}
