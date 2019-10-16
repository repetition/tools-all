package com.tools.agent.process.listener;

import com.tools.service.model.DeployState;
import com.tools.service.service.deploy.runnable.DeployModeSelectorServerControlRunnable;
import io.netty.channel.ChannelHandlerContext;

public class OnServerControlListener implements DeployModeSelectorServerControlRunnable.OnServerControlListener {
    private final ChannelHandlerContext ctx;

    public OnServerControlListener(ChannelHandlerContext ctx) {
        this.ctx = ctx;
    }

    @Override
    public void onServerStart() {

    }

    @Override
    public void onServerStarted(DeployState deployState) {

    }

    @Override
    public void onServerStoping() {

    }

    @Override
    public void onServerStoped(DeployState deployState) {

    }

    @Override
    public void onServerFail(DeployState deployState) {

    }
}
