package com.tools.service.model;

import java.util.List;

public class DeployStatusModel {

    private boolean status;

    private String  time;
    private List<String> deployInfo;

    public boolean status() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public List<String>  getDeployInfo() {
        return deployInfo;
    }

    public void setDeployInfo(List<String>  deployInfo) {
        this.deployInfo = deployInfo;
    }

}
