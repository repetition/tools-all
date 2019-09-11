package com.tools.service.service.deploy.impl;

import com.tools.commons.utils.FileUtils;
import com.tools.commons.utils.PropertyUtils;
import com.tools.service.constant.DeployTypeEnum;
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
                httpdReplace = FileUtils.readFile(deployConfigModel.getHttpdOldChangedPath());
                break;
            case DEPLOY_ZYFL:
                httpdReplace = FileUtils.readFile(deployConfigModel.getHttpdZYFLChangedPath());
                break;
            case DEPLOY_UPLOAD2CM:
                httpdReplace = FileUtils.readFile(deployConfigModel.getHttpdUploadChangedPath());
                break;
            case DEPLOY_2UPLOAD:
                httpdReplace = FileUtils.readFile(deployConfigModel.getHttpdUploadChangedPath());
                break;
            case DEPLOY_2UPLOAD_CM:
                httpdReplace = FileUtils.readFile(deployConfigModel.getHttpdUploadChangedPath());
                break;
            case DEPLOY_UPLOAD2IPM:
                httpdReplace = FileUtils.readFile(deployConfigModel.getHttpdIPMChangedPath());
                break;
        }
        String httpdStr = FileUtils.readFile(apacheServiceHttpdPath);
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
                workersReplace = FileUtils.readFile(deployConfigModel.getWorkersOldChangedPath());
                break;
            case DEPLOY_ZYFL:
                workersReplace = FileUtils.readFile(deployConfigModel.getWorkersOldChangedPath());
                break;
            case DEPLOY_UPLOAD2CM:
                workersReplace = FileUtils.readFile(deployConfigModel.getWordkersUploadChangedPath());
                break;
            case DEPLOY_2UPLOAD:
                workersReplace = FileUtils.readFile(deployConfigModel.getWordkersUploadChangedPath());
                break;
            case DEPLOY_2UPLOAD_CM:
                workersReplace = FileUtils.readFile(deployConfigModel.getWordkersUploadChangedPath());
                break;
            case DEPLOY_UPLOAD2IPM:
                workersReplace = FileUtils.readFile(deployConfigModel.getWordkersUploadChangedPath());
                break;
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
}
