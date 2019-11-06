package com.tools.gui.config;

public class Config {

    public static final boolean isDebug = true;
    public static final boolean isModeSelector = true;
    public static final boolean isConfigSync = true;


    public static final String debugConfigPath;

    public static String downloadFilePath = null;
    /**
     *  Jenkins配置文件JenkinsProperties.properties
     */
    public static String JenkinsPropertiesFileName = "JenkinsProperties.properties";


    static {
            debugConfigPath = "F:\\ToolsDeveloper\\Tools";
            downloadFilePath="D:\\ROOT.war";
    }
}
