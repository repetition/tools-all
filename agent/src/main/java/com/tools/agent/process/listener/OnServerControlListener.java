package com.tools.agent.process.listener;

import com.tools.constant.CommandMethodEnum;
import com.tools.service.model.DeployState;
import com.tools.service.service.deploy.runnable.DeployModeSelectorServerControlRunnable;
import com.tools.socket.bean.Command;
import io.netty.channel.ChannelHandlerContext;

public class OnServerControlListener implements DeployModeSelectorServerControlRunnable.OnServerControlListener {
    private final ChannelHandlerContext ctx;

    public OnServerControlListener(ChannelHandlerContext ctx) {
        this.ctx = ctx;
    }

    @Override
    public void onServerStart() {
        Command command = new Command();
        command.setCommandMethod(CommandMethodEnum.SERVICE_CONTROL_STARTING.toString());
        command.setCommandCode(CommandMethodEnum.SERVICE_CONTROL_STARTING.getCode());
        ctx.channel().writeAndFlush(command);
    }

    @Override
    public void onServerStarted(DeployState deployState) {
        Command command = new Command();
        command.setCommandMethod(CommandMethodEnum.SERVICE_CONTROL_STARTED.toString());
        command.setCommandCode(CommandMethodEnum.SERVICE_CONTROL_STARTED.getCode());
        command.setContent(deployState);
        ctx.channel().writeAndFlush(command);
    }

    @Override
    public void onServerStoping() {
        Command command = new Command();
        command.setCommandMethod(CommandMethodEnum.SERVICE_CONTROL_STOPING.toString());
        command.setCommandCode(CommandMethodEnum.SERVICE_CONTROL_STOPING.getCode());
        ctx.channel().writeAndFlush(command);
    }

    @Override
    public void onServerStoped(DeployState deployState) {
        Command command = new Command();
        command.setCommandMethod(CommandMethodEnum.SERVICE_CONTROL_STOPED.toString());
        command.setCommandCode(CommandMethodEnum.SERVICE_CONTROL_STOPED.getCode());
        command.setContent(deployState);
        ctx.channel().writeAndFlush(command);
    }

    @Override
    public void onServerFail(DeployState deployState) {
        Command command = new Command();
        command.setCommandMethod(CommandMethodEnum.SERVICE_CONTROL_ERROR.toString());
        command.setCommandCode(CommandMethodEnum.SERVICE_CONTROL_ERROR.getCode());
        command.setContent(deployState);
        ctx.channel().writeAndFlush(command);
    }
}
