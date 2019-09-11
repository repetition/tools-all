package com.tools.socket.observer;

public interface Observerable {

    public void registerProcess(Process process);
    public void removeProcess(Process process);
    public void notifyProcess();
    public void notifyProcessState();

}
