package com.tools.service.service.deploy.impl;

import com.tools.commons.utils.FileUtils;
import com.tools.commons.utils.PropertyUtils;
import com.tools.service.constant.DeployTypeEnum;
import com.tools.service.context.ApplicationContext;
import com.tools.service.model.CommandModel;
import com.tools.service.model.DeployConfigModel;
import com.tools.service.model.DeployStatusModel;
import com.tools.service.service.deploy.IApacheDeployService;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * apache部署处理类
 */
public class ApacheDeployServiceImpl implements IApacheDeployService {

    private DeployConfigModel deployConfigModel;
    private DeployProcessorServiceImpl deployProcessorServiceIpml;
    private ServerControlServiceImpl serverControlServiceImpl;

    public ApacheDeployServiceImpl(DeployConfigModel deployConfigModel) {
        this.deployConfigModel = deployConfigModel;
        deployProcessorServiceIpml = new DeployProcessorServiceImpl();
        serverControlServiceImpl = new ServerControlServiceImpl();
    }

    @Override
    public DeployStatusModel stopService() {
        String apacheServiceName = deployConfigModel.getZyflDeployConfigMap().get("apacheServiceName");

        CommandModel commandModel = serverControlServiceImpl.stopServerForService(apacheServiceName);

        DeployStatusModel deployStatusModel = new DeployStatusModel();

        deployStatusModel.setStatus((boolean)commandModel.getProcessExcState());
        deployStatusModel.setTime(commandModel.getProcessExecTime());
        deployStatusModel.setDeployInfo(commandModel.getProcessOutputInfo());
        return deployStatusModel;
    }

    @Override
    public DeployStatusModel startService() {

        String apacheServiceName = deployConfigModel.getZyflDeployConfigMap().get("apacheServiceName");

        CommandModel commandModel = serverControlServiceImpl.startServerForService(apacheServiceName);

        DeployStatusModel deployStatusModel = new DeployStatusModel();

        deployStatusModel.setStatus((boolean)commandModel.getProcessExcState());
        deployStatusModel.setTime(commandModel.getProcessExecTime());
        deployStatusModel.setDeployInfo(commandModel.getProcessOutputInfo());

        return deployStatusModel;
    }

    @Override
    public void changeHttpdConf() {

        String apacheServiceHttpdPath = deployConfigModel.getZyflDeployConfigMap().get("apacheHttpdPath");

        DeployTypeEnum deployTypeEnum = deployConfigModel.getDeployTypeEnum();

        String httpdReplace = null;

        switch (deployTypeEnum){
            case DEPLOY_OLD:
                httpdReplace = FileUtils.readFile(ApplicationContext.getApplicationConfPath() +new File(deployConfigModel.getHttpdOldChangedPath()).getName());
                break;
            case DEPLOY_ZYFL:

                httpdReplace = FileUtils.readFile(ApplicationContext.getApplicationConfPath() +new File(deployConfigModel.getHttpdZYFLChangedPath()).getName());
                break;
            case DEPLOY_UPLOAD2IPM:

                httpdReplace = FileUtils.readFile(ApplicationContext.getApplicationConfPath() +new File(deployConfigModel.getHttpdIPMChangedPath()).getName());
                break;
            case DEPLOY_UPLOAD2CM_1TOMCAT:
                //单tomcat处理
                httpdReplace = FileUtils.readFile(ApplicationContext.getApplicationConfPath() +new File(deployConfigModel.getHttpdUpload1TomcatChangedPath()).getName());
                break;
            default:
                httpdReplace = FileUtils.readFile(ApplicationContext.getApplicationConfPath() +new File(deployConfigModel.getHttpdUploadChangedPath()).getName());
        }
        String httpdStr = FileUtils.readFile(apacheServiceHttpdPath);

        //上传组件需要将一下模块打开
        httpdStr = httpdStr.replace("#LoadModule deflate_module modules/mod_deflate.so", "LoadModule deflate_module modules/mod_deflate.so");
        httpdStr = httpdStr.replace("#LoadModule proxy_module modules/mod_proxy.so", "LoadModule proxy_module modules/mod_proxy.so");
        httpdStr = httpdStr.replace("#LoadModule proxy_ajp_module modules/mod_proxy_ajp.so", "LoadModule proxy_ajp_module modules/mod_proxy_ajp.so");
        httpdStr = httpdStr.replace("#LoadModule proxy_connect_module modules/mod_proxy_connect.so", "LoadModule proxy_connect_module modules/mod_proxy_connect.so");
        httpdStr = httpdStr.replace("#LoadModule proxy_http_module modules/mod_proxy_http.so", "LoadModule proxy_http_module modules/mod_proxy_http.so");

        httpdStr = httpdConfReplace(httpdStr, httpdReplace, apacheServiceHttpdPath);
        FileUtils.saveFile(httpdStr, apacheServiceHttpdPath);
    }


    private String httpdConfReplace(String httpdStr, String httpdReplace, String filePath) {
        //([.|\s\S][\s\S]*?)  加上？是惰性匹配值匹配一次

        //<VirtualHost \*:\d+[>]([.|\s\S][\s\S]*?)</VirtualHost>
        Pattern pattern = Pattern.compile("<VirtualHost \\*:\\d+[>]([.|\\s\\S][\\s\\S]*?)</VirtualHost>");
        Matcher matcher = pattern.matcher(httpdStr);
        if (matcher.find()) {
            String group = matcher.group(1);
            //将apache配置文件修改
            //第一行换行，解决httpd文件替换后与 VirtualHost  存在一行的情况
            httpdStr = httpdStr.replace(group, "\r\n" + httpdReplace);
        }
        return httpdStr;
    }

    @Override
    public void changeWorkers() {

        String apacheServiceWorkersPath = deployConfigModel.getZyflDeployConfigMap().get("apacheWorkersPath");

        DeployTypeEnum deployTypeEnum = deployConfigModel.getDeployTypeEnum();

        String workersReplace = null;

        switch (deployTypeEnum){
            case DEPLOY_OLD:

                workersReplace = FileUtils.readFile(ApplicationContext.getApplicationConfPath() +new File(deployConfigModel.getWorkersOldChangedPath()).getName());
                break;
            case DEPLOY_ZYFL:
                workersReplace = FileUtils.readFile(ApplicationContext.getApplicationConfPath() +new File(deployConfigModel.getWorkersOldChangedPath()).getName());
                break;
            default:
                //其他模式统一是资源分离
                workersReplace = FileUtils.readFile(ApplicationContext.getApplicationConfPath() +new File(deployConfigModel.getWordkersUploadChangedPath()).getName());

        }
        FileUtils.saveFile(workersReplace, apacheServiceWorkersPath);
        //分布部署
        if (deployTypeEnum.equals(DeployTypeEnum.DEPLOY_2UPLOAD)) {

            String cmServerIp = deployConfigModel.getCmDeployConfigMap().get("cmServerIp");
            PropertyUtils propertyUtils = new PropertyUtils(new File(apacheServiceWorkersPath));
            propertyUtils.getConfiguration2Properties();
            propertyUtils.setConfigurationProperty("worker.cm.host", cmServerIp);
        }
    }

    @Override
    public DeployStatusModel exportWar() {

        String warPath = deployConfigModel.getZyflDeployConfigMap().get("zyflWarPath");
        //解压的路径
        String apacheExportWarPath = deployConfigModel.getZyflDeployConfigMap().get("apacheHtdocsPath");

        CommandModel commandModel = deployProcessorServiceIpml.exportWar(warPath, apacheExportWarPath);

        DeployStatusModel deployStatusModel = new DeployStatusModel();
        deployStatusModel.setStatus((boolean)commandModel.getProcessExcState());
        deployStatusModel.setTime(commandModel.getProcessExecTime());
        deployStatusModel.setDeployInfo(commandModel.getProcessOutputInfo());

        return deployStatusModel;

    }

    @Override
    public DeployStatusModel deleteFiles() {

        String apacheHtdocsPath = deployConfigModel.getZyflDeployConfigMap().get("apacheHtdocsPath");

        String apacheHtdocsFilter = deployConfigModel.getZyflDeployConfigMap().get("apacheHtdocsFilter");

        String[] filters = apacheHtdocsFilter.split(",");
        System.out.println("日志");
        List<String> filterList = Arrays.asList(filters);
        File file = new File(apacheHtdocsPath);
        //过滤一部分文件
        File[] listFiles = file.listFiles(pathname -> {
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

        DeployStatusModel deployStatusModel = new DeployStatusModel();
        if (listFiles.length==0){
            deployStatusModel.setStatus(true);
        }
        for (File listFile : listFiles) {
            CommandModel commandModel = deployProcessorServiceIpml.deleteOldFiles(listFile.getAbsolutePath());
            Object excState = commandModel.getProcessExcState();
            if (excState instanceof  Boolean&&!(boolean)excState){
                deployStatusModel.setStatus(false);
                deployStatusModel.setDeployInfo(commandModel.getProcessOutputInfo());
                break;
            }
            deployStatusModel.setStatus(true);
            deployStatusModel.setTime(commandModel.getProcessExecTime());
        }
        return deployStatusModel;
    }

    @Override
    public DeployStatusModel deleteWarFile() {
        //log.info("正在删除zyfl War包...");
        String zyflWarPath = deployConfigModel.getZyflDeployConfigMap().get("zyflWarPath");
        CommandModel commandModel = deployProcessorServiceIpml.deleteOldFile(zyflWarPath);

        DeployStatusModel deployStatusModel = new DeployStatusModel();

        Object excState = commandModel.getProcessExcState();

        if (excState instanceof  Boolean) {
            deployStatusModel.setDeployInfo(commandModel.getProcessOutputInfo());
            deployStatusModel.setStatus((Boolean) excState);
            deployStatusModel.setTime(deployStatusModel.getTime());
        }

        return deployStatusModel;
    }
}
