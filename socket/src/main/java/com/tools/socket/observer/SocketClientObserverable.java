package com.tools.socket.observer;

import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class SocketClientObserverable implements Observerable {
    public  static final Logger log  = LoggerFactory.getLogger(SocketClientObserverable.class);
    private List<Process> processList = new ArrayList<>();
    ChannelHandlerContext ctx;
    @Override
    public void registerProcess(Process process) {

        if (process != null) {
            log.info("registerProcess : " + process.getClass().getName());
            processList.add(process);
        }

    }

    @Override
    public void removeProcess(Process process) {
        if (process != null) {
            processList.remove(process);
        }

    }

    @Override
    public void notifyProcess(Object message) {
        for (Process process : processList) {
            process.process(message,ctx);
        }
    }

    @Override
    public void notifyProcessState() {
        for (Process process : processList) {
            process.active(ctx);
        }
    }

    public void active(ChannelHandlerContext ctx){
        this.ctx = ctx;
        notifyProcessState();
    }
    public void receive(Object message, ChannelHandlerContext ctx){
        this.ctx = ctx;
        notifyProcess(message);
    }
}
