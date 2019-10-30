package com.tools.commons.utils;

public class PlatformUtils {


    public static boolean isLinux() {

        String osName = System.getProperty("os.name").toLowerCase();
        //linux  只支持服务启动
        if (osName.contains("linux")) {

            return true;
        }
        if (osName.contains("window")) {

            return false;
        }

       return false;
    }
}
