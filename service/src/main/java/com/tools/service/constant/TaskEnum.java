package com.tools.service.constant;

public enum TaskEnum{

        APACHE("apache"),
        CM_TOMCAT("cmTomcat"),
        UPLOAD_TOMCAT("upload_Tomcat"),
        MEEECO_TOMCAT("MeeEco_Tomcat");

        String desc;

        TaskEnum(String desc) {
            this.desc = desc;
        }
    }