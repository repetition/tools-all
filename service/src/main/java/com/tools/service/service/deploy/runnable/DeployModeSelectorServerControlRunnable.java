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

import java.util.Map;

/**
 * 处理服务的选择性启动和停止.
 */
public class DeployModeSelectorServerControlRunnable implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(DeployModeSelectorServerControlRunnable.class);
    private DeployConfigModel deployConfigModel;
    private final CMTomcatDeployServiceImpl cmTomcatDeployServiceImpl;
    private final UploadTomcatDeployServiceImpl uploadTomcatDeployServiceImpl;
    private Map<String, Boolean> deployModeSelectorMap;

    public DeployModeSelectorServerControlRunnable(Map<String, Boolean> map) {
        deployConfigModel = ApplicationContext.getDeployConfigModel();
        cmTomcatDeployServiceImpl = new CMTomcatDeployServiceImpl(deployConfigModel);
        uploadTomcatDeployServiceImpl = new UploadTomcatDeployServiceImpl(deployConfigModel);
        deployModeSelectorMap = map;
    }

    @Override
    public void run() {
        ServerStartTypeEnum serverStartTypeEnum = deployConfigModel.getServerStartTypeEnum();
        onServerControlListener.onServerStart();
        if (deployConfigModel.isServerStart()) {
            stopServer();
        } else {
            // stopServer();
            DeployStatusModel deployStatusModel = null;
            DeployStatusModel uploadDeployStatusModel = null;
            Boolean isCM = deployModeSelectorMap.get("cm");
            Boolean isUpload = deployModeSelectorMap.get("upload");
            if (isCM) {
                deployStatusModel = cmTomcatDeployServiceImpl.stopTomcatForConsole();
            }
            if (isUpload) {

                uploadDeployStatusModel = uploadTomcatDeployServiceImpl.stopTomcatForConsole();
            }

            if (null != uploadDeployStatusModel) {
                if (uploadDeployStatusModel.status()) {
                    deployConfigModel.setServerStart(false);
                } else {
                    onServerControlListener.onServerFail(new DeployState().setTaskEnum(TaskEnum.UPLOAD_TOMCAT).setE(String.join("\n", uploadDeployStatusModel.getDeployInfo())));
                }
            }
            if (null != deployStatusModel) {
                if (deployStatusModel.status()) {
                    deployConfigModel.setServerStart(false);
                } else {
                    onServerControlListener.onServerFail(new DeployState().setTaskEnum(TaskEnum.CM_TOMCAT).setE(String.join("\n", deployStatusModel.getDeployInfo())));
                }
            }

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
        DeployStatusModel deployStatusModel = null;
        DeployStatusModel uploadDeployStatusModel = null;
        Boolean isCM = deployModeSelectorMap.get("cm");
        Boolean isUpload = deployModeSelectorMap.get("upload");

        if (isCM) {
            deployStatusModel = cmTomcatDeployServiceImpl.stopTomcatForConsole();
        }
        if (isUpload) {
            uploadDeployStatusModel = uploadTomcatDeployServiceImpl.stopTomcatForConsole();
        }
        if (null != uploadDeployStatusModel) {

            if (uploadDeployStatusModel.status()) {
                onServerControlListener.onServerStoped(new DeployState().setTaskEnum(TaskEnum.UPLOAD_TOMCAT).setInfo(String.join("\n", uploadDeployStatusModel.getDeployInfo())));
                deployConfigModel.setServerStart(false);
            } else {
                onServerControlListener.onServerFail(new DeployState().setTaskEnum(TaskEnum.UPLOAD_TOMCAT).setE(String.join("\n", deployStatusModel.getDeployInfo())));
            }
        }
        if (null != deployStatusModel) {
            if (deployStatusModel.status()) {
                onServerControlListener.onServerStoped(new DeployState().setTaskEnum(TaskEnum.CM_TOMCAT).setInfo(String.join("\n", deployStatusModel.getDeployInfo())));
                deployConfigModel.setServerStart(false);
            } else {
                onServerControlListener.onServerFail(new DeployState().setTaskEnum(TaskEnum.CM_TOMCAT).setE(String.join("\n", deployStatusModel.getDeployInfo())));
            }
        }
    }

    private void startServerForService() {

        DeployTypeEnum deployTypeEnum = deployConfigModel.getDeployTypeEnum();
        DeployStatusModel deployStatusModel = null;
        DeployStatusModel uploadDeployStatusModel = null;

        Boolean isCM = deployModeSelectorMap.get("cm");
        Boolean isUpload = deployModeSelectorMap.get("upload");

        if (isCM) {
            deployStatusModel = cmTomcatDeployServiceImpl.startTomcatForService();
        }
        if (isUpload) {
            uploadDeployStatusModel = uploadTomcatDeployServiceImpl.startTomcatForService();
        }

        if (null != deployStatusModel) {
            if (deployStatusModel.status()) {
                onServerControlListener.onServerStarted(new DeployState().setTaskEnum(TaskEnum.CM_TOMCAT).setInfo(String.join(",", deployStatusModel.getDeployInfo())));
                deployConfigModel.setServerStart(true);
            } else {
                onServerControlListener.onServerFail(new DeployState().setTaskEnum(TaskEnum.CM_TOMCAT));
            }
        }
        if (null != uploadDeployStatusModel) {
            if (uploadDeployStatusModel.status()) {
                onServerControlListener.onServerStarted(new DeployState().setTaskEnum(TaskEnum.UPLOAD_TOMCAT).setInfo(String.join(",", uploadDeployStatusModel.getDeployInfo())));
                deployConfigModel.setServerStart(true);
            } else {
                onServerControlListener.onServerFail(new DeployState().setTaskEnum(TaskEnum.UPLOAD_TOMCAT));
            }
        }
    }

    private void startServerForConsole() {
        DeployTypeEnum deployTypeEnum = deployConfigModel.getDeployTypeEnum();
        DeployStatusModel deployStatusModel = null;
        DeployStatusModel uploadDeployStatusModel = null;

        Boolean isCM = deployModeSelectorMap.get("cm");
        Boolean isUpload = deployModeSelectorMap.get("upload");

        if (isCM) {
            deployStatusModel = cmTomcatDeployServiceImpl.startTomcatForConsole();
        }
        if (isUpload) {
            uploadDeployStatusModel = uploadTomcatDeployServiceImpl.startTomcatForConsole();
        }
        if (null != uploadDeployStatusModel) {
            if (uploadDeployStatusModel.status()) {
                onServerControlListener.onServerStarted(new DeployState().setTaskEnum(TaskEnum.UPLOAD_TOMCAT).setInfo(String.join(",", uploadDeployStatusModel.getDeployInfo())));
                deployConfigModel.setServerStart(true);
            } else {
                onServerControlListener.onServerFail(new DeployState().setTaskEnum(TaskEnum.UPLOAD_TOMCAT));
            }
        }
        if (null != deployStatusModel) {
            if (deployStatusModel.status()) {
                onServerControlListener.onServerStarted(new DeployState().setTaskEnum(TaskEnum.CM_TOMCAT).setInfo(String.join(",", deployStatusModel.getDeployInfo())));
                deployConfigModel.setServerStart(true);
            } else {
                onServerControlListener.onServerFail(new DeployState().setTaskEnum(TaskEnum.CM_TOMCAT));
            }
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
