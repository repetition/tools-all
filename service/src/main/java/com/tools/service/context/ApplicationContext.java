package com.tools.service.context;

import com.tools.service.model.DeployConfigModel;

public class ApplicationContext {
    public  static  final String CONFIG_LIST_FILE_NAME = "config_list.properties";

    private static DeployConfigModel deployConfigModel= null;

    public static DeployConfigModel getDeployConfigModel(){
        if (deployConfigModel==null) {
            deployConfigModel = new DeployConfigModel();
        }
       return deployConfigModel;
    }

    public static void setDeployConfigModel(DeployConfigModel deployConfigModel) {
        ApplicationContext.deployConfigModel = deployConfigModel;
    }

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
     * config_list.properties路径
     */
    public static String getConfigListFilePath(){

        return getApplicationConfPath() +CONFIG_LIST_FILE_NAME;
    }

}
