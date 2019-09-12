package com.tools.agent.process.listener;

import com.tools.agent.process.CommandMethodEnum;
import com.tools.service.model.DeployState;
import com.tools.service.service.deploy.runnable.DeployModeSelectorProcessorRunnable;
import com.tools.socket.bean.Command;
import io.netty.channel.ChannelHandlerContext;

public class OnDeployProcessorListener implements DeployModeSelectorProcessorRunnable.OnDeployProcessorListener {
    private  ChannelHandlerContext ctx;

    public OnDeployProcessorListener(ChannelHandlerContext ctx) {
        this.ctx = ctx;
    }

    @Override
    public void onDeployInit() {

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
