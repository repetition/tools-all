package com.tools.agent;

public class ApplicationConfig {

    public  static  final String DEPLOY_CONFIG_FILE_NAME = "deploy.properties";

    public  static  final String CONFIG_LIST_FILE_NAME = "config_list.properties";


    public static String getApplicationBasePath(){

        return System.getProperty("dir.base");
    }

    public static String getApplicationConfPath(){

        return System.getProperty("conf.path");
    }

    public static String getApplicationHaoZipPath(){

        return System.getProperty("HaoZip.path");
    }

    public static String getWindowsDownloadsPath(){

        return System.getProperty("user.home")+"/Downloads/";
    }

    /**
     * deploy.properties路径
     */
    public static String getDeployConfigFilePath(){

        return getApplicationConfPath() + DEPLOY_CONFIG_FILE_NAME;
    }
    /**
     * config_list.properties路径
     */
    public static String getConfigListFilePath(){

        return getApplicationConfPath() +CONFIG_LIST_FILE_NAME;
    }



}
