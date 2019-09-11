package com.tools.agent.process;

import com.tools.socket.bean.Command;
import com.tools.socket.bean.FileUpload;
import com.tools.socket.observer.Process;
import com.tools.socket.observer.ObserverManager;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ProcessBase implements Process {
    private static final Logger log = LoggerFactory.getLogger(ProcessBase.class);
    ProcessBase() {
        log.info(this.getClass().getName() + " init!");
        ObserverManager.getSocketServerObserver().registerProcess(this);
    }

    @Override
    public void process(Object obj, ChannelHandlerContext ctx) {
        if (obj instanceof Command) {
            processCommand((Command) obj, ctx);
        }
        if (obj instanceof FileUpload) {
            processFileUpload((FileUpload) obj, ctx);
        }
    }

    @Override
    public void active(ChannelHandlerContext ctx) {

    }

    protected void processCommand(Command command, ChannelHandlerContext ctx) {
    }

    protected void processFileUpload(FileUpload fileUpload, ChannelHandlerContext ctx) {
    }

}
