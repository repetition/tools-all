package com.tools.gui.jenkins;

import com.tools.gui.jenkins.bean.ModuleInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class Api {
    private static final Logger log = LoggerFactory.getLogger(Api.class);

    //Jenkins地址
    private static final String Jenkins_Host = "http://10.10.11.58:8080";

    //触发构建
    private static final String JENKINS_LOGIN = "/j_acegi_security_check";

    //获取模块详情
    private static final String PROJECT_MODULE_DETAIL = "/job/api/json?pretty=true";
    //触发构建
    private static final String PROJECT_MODULE_BUILD = "/job/build?delay=0sec";
    //获取构建进度
    private static final String PROJECT_MODULE_BUILD_PROGRESS = "/job/buildHistory/ajax";

    //获取构建的模块详情
    private static final String projectModuleBuildDetail = "/job/api/json?pretty=true";

    private static final String allProject = Jenkins_Host+"/api/json?pretty=true";

    private static ModuleInfo mModuleInfo = null;

    /**
     * 获取Jenkins地址
     *
     * @return
     */
    public static void config(ModuleInfo moduleInfo) {
        mModuleInfo = moduleInfo;
    }


    /**
     * 获取Jenkins地址
     *
     * @return
     */
    public static String getBaseUrl() {
        try {
            return Jenkins_Host + URLEncoder.encode(mModuleInfo.getProjectName(), "utf-8").replace("+","%20") + mModuleInfo.getProjectModuleName();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            log.info("运行异常", e);
        }
        return null;
    }

    public static String getAllProject() {
        return allProject;
    }

    public static String getJenkinsLogin() {
        return Jenkins_Host+JENKINS_LOGIN;
    }

    public static String getModuleLastBuildNumDetail() {
        return getBaseUrl() + "/" + mModuleInfo.getLastBuildNum() + projectModuleBuildDetail;
    }

    /**
     * 获取模块构建详情
     *
     * @return
     */
    public static String getModuleDetailUrl() {
        return getBaseUrl() + PROJECT_MODULE_DETAIL;
    }

    /**
     * 触发构建地址
     *
     * @return
     */
    public static String moduleBuildUrl() {
        return getBaseUrl() + PROJECT_MODULE_BUILD;
    }

    /**
     * 获取构建进度
     *
     * @return 返回构建的进度地址
     */
    public static String getProjectModuleBuildProgressUrl() {
        return getBaseUrl() + PROJECT_MODULE_BUILD_PROGRESS;
    }


    /**
     * 获取ROOT.war下载地址
     *
     * @return 返回ROOT.war下载地址
     */
    public static String getLastSuccessfulBuildDownloadUrl() {
        return getBaseUrl() + "/lastSuccessfulBuild/artifact/target/ROOT.war";
    }


    public static ModuleInfo getModuleInfo() {
        return mModuleInfo;
    }

    public static void updataModileInfo(ModuleInfo moduleInfo) {
        mModuleInfo = moduleInfo;
    }
}
