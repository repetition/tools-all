package com.tools.agent;

import java.io.File;

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

    public static String getWarPath(){

        String userHome = System.getProperty("user.home");
        String path = "";
        String os = System.getProperty("os.name");

        if (os.toLowerCase().contains("windows")) {
            path = userHome+ File.separator+"Downloads"+ File.separator;
        }
        if (os.toLowerCase().contains("linux")) {
            path = System.getProperty("user.dir")+ File.separator+"downloads"+ File.separator;
        }

        return path;
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
