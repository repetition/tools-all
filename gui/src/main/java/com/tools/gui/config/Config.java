package com.tools.gui.config;

public class Config {

    public static final boolean isDebug = false;
    public static final boolean isHome = false;
    public static final boolean isModeSelector = true;
    public static final boolean isConfigSync = true;

    /**
     * 默认的Tomcat服务名
     */
    public static final String defaultTomcatServerName = "ThinkWinCRTomcat";
    /**
     * 默认的Tomcat端口
     */
    public static final String defaultTomcatPort = "8080";

    public static final String debugConfigPath;

    public static String downloadFilePath = null;
    /**
     * CR配置文件CR.properties
     */
    public static String CRConfigFileName = "CR.properties";
    /**
     * 工具部署时，记录的CR的配置文件列表  ConfigList.properties
     */
    public static String CRConfigListFileName = "ConfigList.properties";
    /**
     *  Jenkins配置文件JenkinsProperties.properties
     */
    public static String JenkinsPropertiesFileName = "JenkinsProperties.properties";


    static {
        if (isHome) {
            debugConfigPath = "E:\\Idea WorkSpace\\JAVAFX\\out\\artifacts\\JAVAFX_jar\\Tools";
            downloadFilePath="D:\\ROOT.war";
        } else {
          //  debugConfigPath = "F:\\idea\\IdeaProjects\\JAVAFX\\out\\artifacts\\JAVAFX_jar\\Tools";
            debugConfigPath = "F:\\ToolsDeveloper\\Tools";
            downloadFilePath="D:\\ROOT.war";
        }
    }
}
