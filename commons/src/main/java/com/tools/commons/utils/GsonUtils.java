package com.tools.commons.utils;

import com.google.gson.Gson;

import java.lang.reflect.Type;

public class GsonUtils {

    public static String toJson(Object obj){

        String json = new Gson().toJson(obj);
        return json;
    }


    public static <T> T  fromJson(String json , Class classz){

        T t = new Gson().fromJson(json, (Type)classz);
        return t;
    }

}
