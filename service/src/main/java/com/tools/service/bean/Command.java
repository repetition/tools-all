package com.tools.service.bean;

public class Command {


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
}
