package com.tools.agent.process;

import com.tools.service.context.ApplicationContext;
import com.tools.service.model.DeployConfigModel;
import com.tools.socket.bean.Command;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
                break;
        }
    }
    private void deploy(Command command, ChannelHandlerContext ctx) {
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

        if (isZyfl) {
            Command syncWarCommand = new Command();
            syncWarCommand.setCommandMethod(CommandMethodEnum.SYNC_ZYFL_WAR.toString());
            syncWarCommand.setCommandCode(CommandMethodEnum.SYNC_ZYFL_WAR.getCode());
            log.info(syncWarCommand.toString());
            ctx.channel().writeAndFlush(syncWarCommand);
        }

        if (isUpload) {
            Command syncWarCommand = new Command();
            syncWarCommand.setCommandMethod(CommandMethodEnum.SYNC_UPLOAD_WAR.toString());
            syncWarCommand.setCommandCode(CommandMethodEnum.SYNC_UPLOAD_WAR.getCode());
            ctx.channel().writeAndFlush(syncWarCommand);
        }
/*
        DeployModeSelectorProcessorRunnable runnable = new DeployModeSelectorProcessorRunnable();
        //执行部署
        ThreadPoolManager.getInstance().execute(runnable);*/
    }
}
