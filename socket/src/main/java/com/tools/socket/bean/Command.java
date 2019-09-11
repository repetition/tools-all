package com.tools.socket.bean;

import java.io.Serializable;

public class Command implements Serializable {
    //netty使用ObjectEncoder编解码器时,必须继承Serializable接口 声明一个uid
    private static final long serialVersionUID = 2L;

    private int commandCode;
    private String commandMethod;

    private Object content;

    public int getCommandCode() {
        return commandCode;
    }

    public void setCommandCode(int commandCode) {
        this.commandCode = commandCode;
    }

    public String getCommandMethod() {
        return commandMethod;
    }

    public void setCommandMethod(String commandMethod) {
        this.commandMethod = commandMethod;
    }

    public Object getContent() {
        return content;
    }

    public void setContent(Object content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "Command{" +
                "commandCode=" + commandCode +
                ", commandMethod='" + commandMethod + '\'' +
                ", content=" + content +
                '}';
    }
}
