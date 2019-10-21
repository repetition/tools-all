package com.tools.gui.process;

import com.tools.constant.CommandMethodEnum;
import com.tools.gui.process.sync.ProcessServerBase;
import com.tools.service.model.DeployState;
import com.tools.socket.bean.Command;
import io.netty.channel.ChannelHandlerContext;

public class ServiceControlProcess extends ProcessBase {

    @Override
    protected void processCommand(Command command, ChannelHandlerContext ctx) {

        CommandMethodEnum commandMethodEnum = CommandMethodEnum.getEnum(command.getCommandCode());

        DeployState deployState;
        switch (commandMethodEnum) {
            case SERVICE_CONTROL_ERROR:
                deployState = (DeployState) command.getContent();
                super.onServerControlListener.onServerFail(deployState);
                break;
            case SERVICE_CONTROL_STARTING:
                super.onServerControlListener.onServerStart();
                break;
            case SERVICE_CONTROL_STOPING:
                super.onServerControlListener.onServerStoping();
                break;
            case SERVICE_CONTROL_STARTED:
                deployState = (DeployState) command.getContent();
                super.onServerControlListener.onServerStarted(deployState);
                break;
            case SERVICE_CONTROL_STOPED:
                deployState = (DeployState) command.getContent();
                super.onServerControlListener.onServerStoped(deployState);
                break;
        }

    }
}
