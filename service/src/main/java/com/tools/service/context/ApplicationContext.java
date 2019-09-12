package com.tools.service.context;

import com.tools.service.model.DeployConfigModel;

public class ApplicationContext {


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

}
