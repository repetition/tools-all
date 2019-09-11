package com.tools.commons.utils;

public class StringUtils {

    public static boolean isEmpty(String str){
        if (null == str){
            return true;
        }
        if (str.isEmpty()){
            return true;
        }
        return false;
    }
}
