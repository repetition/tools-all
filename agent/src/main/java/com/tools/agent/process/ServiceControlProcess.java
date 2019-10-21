package com.tools.agent.process;

import com.tools.agent.process.listener.OnServerControlListener;
import com.tools.commons.thread.ThreadPoolManager;
import com.tools.constant.CommandMethodEnum;
import com.tools.service.context.ApplicationContext;
import com.tools.service.model.DeployConfigModel;
import com.tools.service.service.deploy.runnable.DeployModeSelectorServerControlRunnable;
import com.tools.socket.bean.Command;
import io.netty.channel.ChannelHandlerContext;

import java.util.Map;

public class ServiceControlProcess extends ProcessBase {

    @Override
    protected void processCommand(Command command, ChannelHandlerContext ctx) {


        CommandMethodEnum commandMethodEnum = CommandMethodEnum.getEnum(command.getCommandCode());

        switch (commandMethodEnum) {
            case SERVICE_CONTROL:

                DeployConfigModel deployConfigModel = (DeployConfigModel) command.getContent();
                ApplicationContext.setDeployConfigModel(deployConfigModel);

                DeployModeSelectorServerControlRunnable runnable = new DeployModeSelectorServerControlRunnable();
                runnable.setOnServerControlListener(new OnServerControlListener(ctx));
                ThreadPoolManager.getInstance().execute(runnable);

                break;
        }


    }
}
