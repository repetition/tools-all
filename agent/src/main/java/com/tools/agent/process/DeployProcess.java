package com.tools.agent.process;

import com.tools.agent.ApplicationConfig;
import com.tools.agent.process.listener.OnDeployProcessorListener;
import com.tools.commons.thread.ThreadPoolManager;
import com.tools.service.context.ApplicationContext;
import com.tools.service.model.CommandModel;
import com.tools.service.model.DeployConfigModel;
import com.tools.service.service.deploy.impl.LinuxInstallRpmServiceImpl;
import com.tools.service.service.deploy.runnable.DeployModeSelectorProcessorRunnable;
import com.tools.socket.bean.Command;
import com.tools.socket.bean.FileUpload;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DeployProcess extends ProcessBase {
    private static final Logger log = LoggerFactory.getLogger(DeployProcess.class);

    @Override
    protected void processCommand(Command command, ChannelHandlerContext ctx) {

        CommandMethodEnum commandMethodEnum = CommandMethodEnum.getEnum(command.getCommandCode());
        switch (commandMethodEnum) {
            case DEPLOY_INIT:
                deploy(command,ctx);
                break;
            case DEPLOY_START:
                log.info(command.toString());
                log.info("部署开始....");
                DeployModeSelectorProcessorRunnable runnable = new DeployModeSelectorProcessorRunnable();
                runnable.setOnDeployProcessorListener(new OnDeployProcessorListener(ctx));
                ThreadPoolManager.getInstance().execute(runnable);
                break;
        }
    }

    @Override
    protected void processFileUpload(FileUpload fileUpload, ChannelHandlerContext ctx) {

        DeployConfigModel deployConfigModel = ApplicationContext.getDeployConfigModel();
        CommandMethodEnum commandMethodEnum = CommandMethodEnum.valueOf(fileUpload.getFileType());

        if (fileUpload.getAgentFile() == null) {
            return;
        }
        //重新设置war包的路径
        switch (commandMethodEnum){
            case SYNC_CM_WAR:
                deployConfigModel.getCmDeployConfigMap().put("cmWarPath",fileUpload.getAgentFile().getAbsolutePath());
                break;
            case SYNC_ZYFL_WAR:
                deployConfigModel.getZyflDeployConfigMap().put("zyflWarPath",fileUpload.getAgentFile().getAbsolutePath());
                break;
            case SYNC_UPLOAD_WAR:
                deployConfigModel.getUploadDeployConfigMap().put("uploadWarPath",fileUpload.getAgentFile().getAbsolutePath());
                break;
        }
    }

    private void deploy(Command command, ChannelHandlerContext ctx) {

        checkIsInstall();



        //保存设置
        ApplicationContext.setDeployConfigModel((DeployConfigModel) command.getContent());
        DeployConfigModel deployConfigModel = ApplicationContext.getDeployConfigModel();

        //获取当前部署的任务
        Map<String, Boolean> deployModeSelectorMap = deployConfigModel.getDeployModeSelectorMap();

        Boolean isCm = deployModeSelectorMap.get("cm");
        Boolean isZyfl = deployModeSelectorMap.get("zyfl");
        Boolean isUpload = deployModeSelectorMap.get("upload");
        Boolean isApache_config = deployModeSelectorMap.get("apache_config");

        //war包同步
        if (isCm) {
            Command syncWarCommand = new Command();
            syncWarCommand.setCommandMethod(CommandMethodEnum.SYNC_CM_WAR.toString());
            syncWarCommand.setCommandCode(CommandMethodEnum.SYNC_CM_WAR.getCode());
            log.info(syncWarCommand.toString());
            ctx.channel().writeAndFlush(syncWarCommand);
        }
        //资源分离包同步
        if (isZyfl) {
            Command syncWarCommand = new Command();
            syncWarCommand.setCommandMethod(CommandMethodEnum.SYNC_ZYFL_WAR.toString());
            syncWarCommand.setCommandCode(CommandMethodEnum.SYNC_ZYFL_WAR.getCode());
            log.info(syncWarCommand.toString());
            ctx.channel().writeAndFlush(syncWarCommand);
        }
        //上传组件包同步
        if (isUpload) {
            Command syncWarCommand = new Command();
            syncWarCommand.setCommandMethod(CommandMethodEnum.SYNC_UPLOAD_WAR.toString());
            syncWarCommand.setCommandCode(CommandMethodEnum.SYNC_UPLOAD_WAR.getCode());
            ctx.channel().writeAndFlush(syncWarCommand);
        }
        //apache映射配置同步
        if (isApache_config) {
            Command syncWarCommand = new Command();
            syncWarCommand.setCommandMethod(CommandMethodEnum.SYNC_APACHE_CONFIG.toString());
            syncWarCommand.setCommandCode(CommandMethodEnum.SYNC_APACHE_CONFIG.getCode());
            ctx.channel().writeAndFlush(syncWarCommand);
        }

        Command syncRuntimeChangerCommand = new Command();
        syncRuntimeChangerCommand.setCommandMethod(CommandMethodEnum.SYNC_RUNTIME_CHANGER_CONFIG.toString());
        syncRuntimeChangerCommand.setCommandCode(CommandMethodEnum.SYNC_RUNTIME_CHANGER_CONFIG.getCode());
        ctx.channel().writeAndFlush(syncRuntimeChangerCommand);
    }

    private void checkIsInstall() {


        LinuxInstallRpmServiceImpl linuxInstallRpmService = new LinuxInstallRpmServiceImpl();

        List<String> command = new ArrayList<>();
        command.add("unzip");
        command.add("-v");
        log.info("正在检测unzip已安装");

        CommandModel commandModel = linuxInstallRpmService.checkRpm(command);

        if ((boolean)commandModel.getProcessExcState()) {

            log.info("unzip已安装");

        }else {
            log.info("unzip未安装，正在进行安装");
            CommandModel commandModel1 = linuxInstallRpmService.installRpm(ApplicationConfig.getApplicationConfPath() + "unzip-6.0-20.el7.x86_64.rpm");

            if ((boolean)commandModel1.getProcessExcState()) {
                log.info("unzip安装成功");
            }
        }
    }
}
