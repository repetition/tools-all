package com.tools.gui.config;

public class ApplicationConfig {

    public  static  final String DEPLOY_CONFIG_FILE_NAME = "deploy.properties";

    public  static  final String CONFIG_LIST_FILE_NAME = "config_list.properties";
    /**
     * deploy.properties路径
     */
    public  static  final String DEPLOY_CONFIG_FILE_PATH = System.getProperty("conf.path")+DEPLOY_CONFIG_FILE_NAME;
    /**
     * config_list.properties路径
     */
    public  static  final String CONFIG_LIST_FILE_PATH = System.getProperty("conf.path")+CONFIG_LIST_FILE_NAME;


}
