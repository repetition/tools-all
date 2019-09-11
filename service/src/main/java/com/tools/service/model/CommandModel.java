package com.tools.service.model;

import java.util.List;

public class CommandModel {

    /**
     * 命令执行的状态值
     */
    private int  processWaitFor;

    /**
     * 命令执行的输出
     */
    private List<String> processOutputInfo;


    /**
     * 命令执行的状态  对应业务
     */
    private Object  processExcState;

    /**
     * 耗时
     */
    private String  processExecTime;


    public int getProcessWaitFor() {
        return processWaitFor;
    }

    public void setProcessWaitFor(int processWaitFor) {
        this.processWaitFor = processWaitFor;
    }

    public List<String> getProcessOutputInfo() {
        return processOutputInfo;
    }

    public void setProcessOutputInfo(List<String> processOutputInfo) {
        this.processOutputInfo = processOutputInfo;
    }

    public Object getProcessExcState() {
        return processExcState;
    }

    public void setProcessExcState(Object processExcState) {
        this.processExcState = processExcState;
    }


    public String getProcessExecTime() {
        return processExecTime;
    }

    public void setProcessEexcTime(String processExecTime) {
        this.processExecTime = processExecTime;
    }

    @Override
    public String toString() {
        return "CommandModel{" +
                "processWaitFor=" + processWaitFor +
                ", processOutputInfo=" + processOutputInfo +
                ", processExcState=" + processExcState +
                ", processExecTime='" + processExecTime + '\'' +
                '}';
    }
}
