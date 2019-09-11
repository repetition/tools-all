package com.tools.socket.observer;

import io.netty.channel.ChannelHandlerContext;

import java.util.ArrayList;
import java.util.List;

public class SocketServerObserverable implements Observerable {
    private List<Process> processList = new ArrayList<>();
    Object message;
    ChannelHandlerContext ctx;
    @Override
    public void registerProcess(Process process) {

        if (process != null) {
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
    public void notifyProcess() {
        for (Process process : processList) {
            process.process(message,ctx);
        }
    }

    @Override
    public void notifyProcessState() {

    }

    public void receive(Object message, ChannelHandlerContext ctx){
        this.message = message;
        this.ctx = ctx;
        notifyProcess();
    }
}
