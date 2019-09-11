package com.tools.service.service.deploy.runnable;


import com.tools.service.constant.DeployTypeEnum;
import com.tools.service.constant.TaskEnum;
import com.tools.service.context.ApplicationContext;
import com.tools.service.model.DeployConfigModel;
import com.tools.service.model.DeployState;
import com.tools.service.model.DeployStatusModel;
import com.tools.service.service.deploy.impl.ApacheDeployServiceImpl;
import com.tools.service.service.deploy.impl.CMTomcatDeployServiceImpl;
import com.tools.service.service.deploy.impl.UploadTomcatDeployServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.tools.service.constant.DeployTypeEnum.*;

/**
 * 部署处理的线程
 */
public class DeployProcessorRunnable implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(DeployProcessorRunnable.class);

    private ApacheDeployServiceImpl apacheDeployServiceImpl;
    private DeployConfigModel deployConfigModel;
    private CMTomcatDeployServiceImpl cmTomcatDeployServiceImpl;
    private UploadTomcatDeployServiceImpl uploadTomcatDeployServiceImpl;

    public DeployProcessorRunnable() {
        init();
    }

    private void init() {
        deployConfigModel = ApplicationContext.getDeployConfigModel();
        //apache部署实现类
        apacheDeployServiceImpl = new ApacheDeployServiceImpl(deployConfigModel);
        //cm部署实现类
        cmTomcatDeployServiceImpl = new CMTomcatDeployServiceImpl(deployConfigModel);
        //上传组件部署实现类
        uploadTomcatDeployServiceImpl = new UploadTomcatDeployServiceImpl(deployConfigModel);
    }

    @Override
    public void run() {
        //部署初始化
        onDeployProcessorListener.onDeployInit();
        onDeployProcessorListener.onDeployProcessorStart();
        DeployStatusModel deployStatusModel = apacheDeployServiceImpl.stopService();
        DeployState deployState = new DeployState();

        DeployTypeEnum deployTypeEnum = deployConfigModel.getDeployTypeEnum();
        if (!deployTypeEnum.equals(DEPLOY_2UPLOAD_CM)) {
            //apache部署 --------------------------------
            if (!deployStatusModel.status()) {
                onDeployProcessorListener.onDeployProcessFail(deployState.setState(false).setE(String.join(",", deployStatusModel.getDeployInfo())).setTaskEnum(TaskEnum.APACHE));
                return;
            } else {
                onDeployProcessorListener.onDeployProcessSuccess(deployState.setState(true).setInfo("Apache停止服务成功!").setTaskEnum(TaskEnum.APACHE));
            }

            deployStatusModel = apacheDeployServiceImpl.deleteFiles();

            if (!deployStatusModel.status()) {
                onDeployProcessorListener.onDeployProcessFail(deployState.setState(false).setE(String.join(",", deployStatusModel.getDeployInfo())).setTaskEnum(TaskEnum.APACHE));
                return;
            } else {
                onDeployProcessorListener.onDeployProcessSuccess(deployState.setState(true).setInfo("Apache Htdocs删除成功!").setTaskEnum(TaskEnum.APACHE));
            }
            //部署模式为旧版时,  不解压资源分离war包
            if (!deployTypeEnum.equals(DEPLOY_OLD)) {
                deployStatusModel = apacheDeployServiceImpl.exportWar();

                if (!deployStatusModel.status()) {
                    onDeployProcessorListener.onDeployProcessFail(deployState.setState(false).setE(String.join(",", deployStatusModel.getDeployInfo())).setTaskEnum(TaskEnum.APACHE));
                    return;
                } else {
                    onDeployProcessorListener.onDeployProcessSuccess(deployState.setState(true).setInfo("Apache 解压文件成功!").setTaskEnum(TaskEnum.APACHE));
                }
            }
            //修改替换配置文件
            apacheDeployServiceImpl.changeHttpdConf();
            apacheDeployServiceImpl.changeWorkers();

            deployStatusModel = apacheDeployServiceImpl.startService();

            if (!deployStatusModel.status()) {
                onDeployProcessorListener.onDeployProcessFail(deployState.setState(false).setE(String.join(",", deployStatusModel.getDeployInfo())).setTaskEnum(TaskEnum.APACHE));
            } else {
                onDeployProcessorListener.onDeployProcessSuccess(deployState.setState(true).setInfo("Apache 启动成功").setTaskEnum(TaskEnum.APACHE));
            }

        }
        if (!deployTypeEnum.equals(DEPLOY_2UPLOAD)) {
            //cm Tomcat部署 --------------------------------

            deployStatusModel = cmTomcatDeployServiceImpl.stopTomcatForConsole();

            if (!deployStatusModel.status()) {
                onDeployProcessorListener.onDeployProcessFail(deployState.setState(false).setE(String.join(",", deployStatusModel.getDeployInfo())).setTaskEnum(TaskEnum.CM_TOMCAT));
            } else {
                onDeployProcessorListener.onDeployProcessSuccess(deployState.setState(true).setInfo("CM_TOMCAT 服务停止成功!").setTaskEnum(TaskEnum.CM_TOMCAT));
            }

            cmTomcatDeployServiceImpl.clearCacheForTomcat();
            deployStatusModel = cmTomcatDeployServiceImpl.deleteOldFile();

            if (!deployStatusModel.status()) {
                onDeployProcessorListener.onDeployProcessFail(deployState.setState(false).setE(String.join(",", deployStatusModel.getDeployInfo())).setTaskEnum(TaskEnum.CM_TOMCAT));
            } else {
                onDeployProcessorListener.onDeployProcessSuccess(deployState.setState(true).setInfo("CM_TOMCAT 删除文件成功!").setTaskEnum(TaskEnum.CM_TOMCAT));
            }

            deployStatusModel = cmTomcatDeployServiceImpl.exportWarForTomcat();

            if (!deployStatusModel.status()) {
                onDeployProcessorListener.onDeployProcessFail(deployState.setState(false).setE(String.join(",", deployStatusModel.getDeployInfo())).setTaskEnum(TaskEnum.CM_TOMCAT));
            } else {
                onDeployProcessorListener.onDeployProcessSuccess(deployState.setState(true).setInfo("CM_TOMCAT 解压成功!").setTaskEnum(TaskEnum.CM_TOMCAT));
            }
            cmTomcatDeployServiceImpl.configModifying();
        }

        if (deployTypeEnum.equals(DEPLOY_2UPLOAD)||deployTypeEnum.equals(DEPLOY_UPLOAD2CM)) {
            //upload Tomcat部署 --------------------------------

            deployStatusModel = uploadTomcatDeployServiceImpl.stopTomcatForConsole();

            if (!deployStatusModel.status()) {
                onDeployProcessorListener.onDeployProcessFail(deployState.setState(false).setE(String.join(",", deployStatusModel.getDeployInfo())).setTaskEnum(TaskEnum.UPLOAD_TOMCAT));
            } else {
                onDeployProcessorListener.onDeployProcessSuccess(deployState.setState(true).setInfo("UPLOAD_TOMCAT 服务停止成功!").setTaskEnum(TaskEnum.UPLOAD_TOMCAT));
            }
            uploadTomcatDeployServiceImpl.clearCacheForTomcat();
            deployStatusModel = uploadTomcatDeployServiceImpl.deleteOldFile();

            if (!deployStatusModel.status()) {
                onDeployProcessorListener.onDeployProcessFail(deployState.setState(false).setE(String.join(",", deployStatusModel.getDeployInfo())).setTaskEnum(TaskEnum.UPLOAD_TOMCAT));
            } else {
                onDeployProcessorListener.onDeployProcessSuccess(deployState.setState(true).setInfo("UPLOAD_TOMCAT 删除文件成功!").setTaskEnum(TaskEnum.UPLOAD_TOMCAT));
            }
            deployStatusModel = uploadTomcatDeployServiceImpl.exportWarForTomcat();

            if (!deployStatusModel.status()) {
                onDeployProcessorListener.onDeployProcessFail(deployState.setState(false).setE(String.join(",", deployStatusModel.getDeployInfo())).setTaskEnum(TaskEnum.UPLOAD_TOMCAT));
            } else {
                onDeployProcessorListener.onDeployProcessSuccess(deployState.setState(true).setInfo("UPLOAD_TOMCAT 文件解压成功!").setTaskEnum(TaskEnum.UPLOAD_TOMCAT));
            }
        }

        onDeployProcessorListener.onDeployProcessorEnd();
    }

    private OnDeployProcessorListener onDeployProcessorListener;

    public void setOnDeployProcessorListener(OnDeployProcessorListener onDeployProcessorListener) {
        this.onDeployProcessorListener = onDeployProcessorListener;
    }

    public interface OnDeployProcessorListener {
        /**
         * 部署开始之前,进行初始化操作
         */
        void onDeployInit();

        /**
         * 部署开始
         */
        void onDeployProcessorStart();

        /**
         * 部署结束
         */
        void onDeployProcessorEnd();

        /**
         * 分阶段部署成功
         * @param deployState 部署的详细信息
         */
        void onDeployProcessSuccess(DeployState deployState);

        /**
         * 部署失败
         * @param deployState 失败的详细信息
         */
        void onDeployProcessFail(DeployState deployState);
    }




}
