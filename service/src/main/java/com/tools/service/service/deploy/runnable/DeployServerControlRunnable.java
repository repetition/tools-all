package com.tools.service.service.deploy.runnable;

import com.tools.service.constant.DeployTypeEnum;
import com.tools.service.constant.ServerStartTypeEnum;
import com.tools.service.constant.TaskEnum;
import com.tools.service.context.ApplicationContext;
import com.tools.service.model.DeployConfigModel;
import com.tools.service.model.DeployState;
import com.tools.service.model.DeployStatusModel;
import com.tools.service.service.deploy.impl.CMTomcatDeployServiceImpl;
import com.tools.service.service.deploy.impl.UploadTomcatDeployServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 服务的启动和关闭
 */
public class DeployServerControlRunnable implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(DeployServerControlRunnable.class);
    private DeployConfigModel deployConfigModel;
    private final CMTomcatDeployServiceImpl cmTomcatDeployServiceImpl;
    private final UploadTomcatDeployServiceImpl uploadTomcatDeployServiceImpl;

    public DeployServerControlRunnable() {
        deployConfigModel = ApplicationContext.getDeployConfigModel();
        cmTomcatDeployServiceImpl = new CMTomcatDeployServiceImpl(deployConfigModel);
        uploadTomcatDeployServiceImpl = new UploadTomcatDeployServiceImpl(deployConfigModel);
    }

    @Override
    public void run() {
        ServerStartTypeEnum serverStartTypeEnum = deployConfigModel.getServerStartTypeEnum();
        onServerControlListener.onServerStart();
        if (deployConfigModel.isServerStart()) {
            stopServer();
        } else {
            stopServer();
            switch (serverStartTypeEnum) {
                case CONSOLE:
                    startServerForConsole();
                    break;
                case SERVICE:
                    startServerForService();
                    break;
                default:
                    log.warn("错误的条件:" + serverStartTypeEnum.toString());
            }
        }
    }

    private void stopServer() {
        onServerControlListener.onServerStoping();
        DeployStatusModel deployStatusModel = cmTomcatDeployServiceImpl.stopTomcatForConsole();
        DeployStatusModel uploadDeployStatusModel = uploadTomcatDeployServiceImpl.stopTomcatForConsole();

        if (uploadDeployStatusModel.status()) {
            onServerControlListener.onServerStoped(new DeployState().setTaskEnum(TaskEnum.UPLOAD_TOMCAT).setInfo(String.join(",",uploadDeployStatusModel.getDeployInfo())));
            deployConfigModel.setServerStart(false);
        } else {
            onServerControlListener.onServerFail(new DeployState().setTaskEnum(TaskEnum.UPLOAD_TOMCAT).setInfo(String.join(",",uploadDeployStatusModel.getDeployInfo())));
        }
        if (deployStatusModel.status()) {
            onServerControlListener.onServerStoped(new DeployState().setTaskEnum(TaskEnum.CM_TOMCAT).setInfo(String.join(",",deployStatusModel.getDeployInfo())));
            deployConfigModel.setServerStart(false);
        } else {
            onServerControlListener.onServerFail(new DeployState().setTaskEnum(TaskEnum.CM_TOMCAT));
        }

    }

    private void startServerForService() {

        DeployTypeEnum deployTypeEnum = deployConfigModel.getDeployTypeEnum();
        DeployStatusModel deployStatusModel = null;
        DeployStatusModel uploadDeployStatusModel = null;
        switch (deployTypeEnum) {
            case DEPLOY_OLD:
                deployStatusModel = cmTomcatDeployServiceImpl.startTomcatForService();
                break;
            case DEPLOY_ZYFL:
                deployStatusModel = cmTomcatDeployServiceImpl.startTomcatForService();
                break;
            case DEPLOY_UPLOAD2CM:
                deployStatusModel = cmTomcatDeployServiceImpl.startTomcatForService();
                uploadDeployStatusModel = uploadTomcatDeployServiceImpl.startTomcatForService();
                break;
            case DEPLOY_2UPLOAD:
                uploadDeployStatusModel = uploadTomcatDeployServiceImpl.startTomcatForService();
                break;
            case DEPLOY_2UPLOAD_CM:
                deployStatusModel = cmTomcatDeployServiceImpl.startTomcatForService();
                break;
            case DEPLOY_UPLOAD2IPM:
                deployStatusModel = cmTomcatDeployServiceImpl.startTomcatForService();
                uploadDeployStatusModel = uploadTomcatDeployServiceImpl.startTomcatForService();
                break;
        }

        if (null != uploadDeployStatusModel) {
            if (uploadDeployStatusModel.status()) {
                onServerControlListener.onServerStarted(new DeployState().setTaskEnum(TaskEnum.UPLOAD_TOMCAT).setInfo(String.join(",",uploadDeployStatusModel.getDeployInfo())));
                deployConfigModel.setServerStart(true);
            } else {
                onServerControlListener.onServerFail(new DeployState().setTaskEnum(TaskEnum.UPLOAD_TOMCAT));
            }
        }
        if (deployStatusModel.status()) {
            onServerControlListener.onServerStarted(new DeployState().setTaskEnum(TaskEnum.CM_TOMCAT).setInfo(String.join(",",deployStatusModel.getDeployInfo())));
            deployConfigModel.setServerStart(true);
        } else {
            onServerControlListener.onServerFail(new DeployState().setTaskEnum(TaskEnum.CM_TOMCAT));
        }
    }

    private void startServerForConsole() {
        DeployTypeEnum deployTypeEnum = deployConfigModel.getDeployTypeEnum();
        DeployStatusModel deployStatusModel = null;
        DeployStatusModel uploadDeployStatusModel = null;
        switch (deployTypeEnum) {
            case DEPLOY_OLD:
                deployStatusModel = cmTomcatDeployServiceImpl.startTomcatForConsole();
                break;
            case DEPLOY_ZYFL:
                deployStatusModel = cmTomcatDeployServiceImpl.startTomcatForConsole();
                break;
            case DEPLOY_UPLOAD2CM:
                deployStatusModel = cmTomcatDeployServiceImpl.startTomcatForConsole();
                uploadDeployStatusModel = uploadTomcatDeployServiceImpl.startTomcatForConsole();
                break;
            case DEPLOY_2UPLOAD:
                uploadDeployStatusModel = uploadTomcatDeployServiceImpl.startTomcatForConsole();
                break;
            case DEPLOY_2UPLOAD_CM:
                deployStatusModel = cmTomcatDeployServiceImpl.startTomcatForConsole();
                break;
            case DEPLOY_UPLOAD2IPM:
                deployStatusModel = cmTomcatDeployServiceImpl.startTomcatForConsole();
                uploadDeployStatusModel = uploadTomcatDeployServiceImpl.startTomcatForConsole();
                break;
        }

        if (null != uploadDeployStatusModel) {
            if (uploadDeployStatusModel.status()) {
                onServerControlListener.onServerStarted(new DeployState().setTaskEnum(TaskEnum.UPLOAD_TOMCAT).setInfo(String.join(",",uploadDeployStatusModel.getDeployInfo())));
                deployConfigModel.setServerStart(true);
            } else {
                onServerControlListener.onServerFail(new DeployState().setTaskEnum(TaskEnum.UPLOAD_TOMCAT));
            }
        }
        if (deployStatusModel.status()) {
            onServerControlListener.onServerStarted(new DeployState().setTaskEnum(TaskEnum.CM_TOMCAT).setInfo(String.join(",",deployStatusModel.getDeployInfo())));
            deployConfigModel.setServerStart(true);
        } else {
            onServerControlListener.onServerFail(new DeployState().setTaskEnum(TaskEnum.CM_TOMCAT));
        }

    }

    private OnServerControlListener onServerControlListener;

    public void setOnServerControlListener(OnServerControlListener onServerControlListener) {
        this.onServerControlListener = onServerControlListener;
    }

    public interface OnServerControlListener {
        void onServerStart();

        void onServerStarted(DeployState deployState);

        void onServerStoping();

        void onServerStoped(DeployState deployState);

        void onServerFail(DeployState deployState);
    }
}
