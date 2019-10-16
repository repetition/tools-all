package com.tools.agent.process;

import com.tools.agent.process.listener.OnServerControlListener;
import com.tools.commons.thread.ThreadPoolManager;
import com.tools.constant.CommandMethodEnum;
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
                Map<String, Boolean> map = (Map<String, Boolean>) command.getContent();
                DeployModeSelectorServerControlRunnable runnable = new DeployModeSelectorServerControlRunnable(map);
                runnable.setOnServerControlListener(new OnServerControlListener(ctx));
                ThreadPoolManager.getInstance().execute(runnable);

                break;
        }


    }
}
