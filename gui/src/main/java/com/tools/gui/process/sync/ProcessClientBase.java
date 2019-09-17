package com.tools.gui.process.sync;

import com.tools.socket.bean.Command;
import com.tools.socket.observer.ObserverManager;
import com.tools.socket.observer.Process;
import io.netty.channel.ChannelHandlerContext;

public class ProcessClientBase implements Process {
    public ProcessClientBase() {
        ObserverManager.getSocketClientObserverable().registerProcess(this);

    }

    @Override
    public void process(Object obj, ChannelHandlerContext ctx) {
        if (obj instanceof Command) {
            processCommand((Command)obj, ctx);
        }
    }

    protected void processCommand(Command command, ChannelHandlerContext ctx) { }

    @Override
    public void active(ChannelHandlerContext ctx) {

    }
}
