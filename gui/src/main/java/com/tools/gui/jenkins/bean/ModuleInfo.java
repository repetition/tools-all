package com.tools.gui.jenkins.bean;

/**
 * 每个模块的信息包装
 */
public class ModuleInfo {
    //分支项目名
    private String projectName;
    //模块名字
    private String projectModuleName;
    //获取模块详情
    private String projectModuleDetail;
    //触发构建
    private String projectModuleBuild;
    //获取构建进度
    private String projectModuleBuildProgress;

    private String projectModuleBuildName;


    private int lastSuccessfulBuildNum;
    private int lastBuildNum;

    public String getProjectModuleBuildName() {
        return projectModuleBuildName;
    }

    public void setProjectModuleBuildName(String projectModuleBuildName) {
        this.projectModuleBuildName = projectModuleBuildName;
    }

    public int getLastSuccessfulBuildNum() {
        return lastSuccessfulBuildNum;
    }

    public void setLastSuccessfulBuildNum(int lastSuccessfulBuildNum) {
        this.lastSuccessfulBuildNum = lastSuccessfulBuildNum;
    }

    public int getLastBuildNum() {
        return lastBuildNum;
    }

    public void setLastBuildNum(int lastBuildNum) {
        this.lastBuildNum = lastBuildNum;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getProjectModuleName() {
        return projectModuleName;
    }

    public void setProjectModuleName(String projectModuleName) {
        this.projectModuleName = projectModuleName;
    }
}
