package com.tools.gui.jenkins;

import com.google.gson.Gson;
import com.tools.commons.utils.PropertyUtils;
import com.tools.commons.utils.RequestUtils;
import com.tools.commons.utils.Utils;
import com.tools.gui.config.Config;
import com.tools.gui.jenkins.bean.AllProjectBean;
import com.tools.gui.jenkins.bean.LastBuildBean;
import com.tools.gui.jenkins.bean.ModuleInfo;
import com.tools.gui.jenkins.bean.ProjectDetailBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProjectBuild {
    private static final Logger log = LoggerFactory.getLogger(ProjectBuild.class);

    private static ModuleInfo moduleInfo = null;

    private static String cookie = null;


    /**
     * 配置模块信息
     *
     * @param moduleInfo 模块信息
     */
    public static void config(ModuleInfo moduleInfo) {
        Api.config(moduleInfo);
    }

    /**
     * 获取模块构建详情
     *
     * @return 返回构建实体对象
     */
    public static ProjectDetailBean getModuleDetails() {
        log.info(Api.getModuleDetailUrl());
        String module_build_detail = RequestUtils.post(Api.getModuleDetailUrl(), null,cookie);
        ProjectDetailBean projectDetailBean = buildGson().fromJson(module_build_detail, ProjectDetailBean.class);
        int lastSuccessfulBuildNum = projectDetailBean.getLastSuccessfulBuild().getNumber();
        int lastBuildNum = projectDetailBean.getLastBuild().getNumber();

        //判断是否已经构建
        if (lastBuildNum == lastSuccessfulBuildNum) {
            log.info("没有构建！");
        } else {
            //设置构建
/*            ModuleInfo moduleInfo = Api.getModuleInfo();
            moduleInfo.setLastBuildNum(lastBuildNum);
            moduleInfo.setLastSuccessfulBuildNum(lastSuccessfulBuildNum);
            Api.updataModileInfo(moduleInfo);*/
            Api.getModuleInfo().setLastBuildNum(lastBuildNum);
            Api.getModuleInfo().setLastSuccessfulBuildNum(lastSuccessfulBuildNum);
        }
        return projectDetailBean;
    }

    /**
     * 查找模块是否存在
     *
     * @return true 存在  false 不存在
     */
    public static boolean isExist() {
        //查找次模块是否存在
        String result = RequestUtils.post(Api.getModuleDetailUrl(), null,cookie);
        if (result.equals("not_found")) {
        //    log.info(result + " | " + Api.getModuleDetailUrl());
            return false;
        } else {
            return true;
        }
    }

    /**
     * 获取最后一次构建的详情
     *
     * @return 构建的进度
     */
    public static LastBuildBean getProjectModuleLastBuild() {
//        log.info(Api.getModuleLastBuildNumDetail());
        String module_last_build = RequestUtils.post(Api.getModuleLastBuildNumDetail(), null,cookie);
        LastBuildBean lastBuildBean = buildGson().fromJson(module_last_build, LastBuildBean.class);
        return lastBuildBean;
    }


    /**
     * 触发构建
     *
     * @return true 构建成功 false  构建失败
     */
    public static boolean moduleBuild() {
        log.info(Api.moduleBuildUrl());
        //触发构建
        String module_building = RequestUtils.post(Api.moduleBuildUrl(), null,cookie);

        if (module_building.equals("")) {
            return true;
        }
        if (module_building.equals("fail")) {
            return false;
        }
        return false;
    }

    /**
     * 获取构建进度
     *
     * @return 构建的进度
     */
    public static String getProjectModuleBuildProgress() {
//        log.info("getProjectModuleBuildProgress:" + Api.getProjectModuleBuildProgressUrl());
        //构建Jenkins项目
        String module_build_progress = RequestUtils.post(Api.getProjectModuleBuildProgressUrl(), null,cookie);
        String progress = getBuildProgress(module_build_progress);
        return progress;
    }

    /**
     * 获取模块构建详情
     *
     * @return true 下载成功  false 下载失败
     */
    public static String downloadLastSuccessfulROOT() {
        // TODO: 2018/5/31 root包现在待实现
        String lastSuccessfulBuildDonwloadUrl = Api.getLastSuccessfulBuildDownloadUrl();
        log.info(lastSuccessfulBuildDonwloadUrl);
/*        long fileTotalSize = DownloadMain.getFileTotalSize(lastSuccessfulBuildDonwloadUrl);
        DownloadMain.createTempFile(fileTotalSize);*/

        // lastSuccessfulBuildDonwloadUrl = "";
        return lastSuccessfulBuildDonwloadUrl;
    }

    /**
     * 通过正则截取进度
     *
     * @param progressStr 截取的字符串
     * @return 返回截取的内容
     */
    private static String getBuildProgress(String progressStr) {
        //通过正则获取进度信息
        Pattern pattern = Pattern.compile("<td style=\"width:(.+?)%;\" class");
        Matcher matcher = pattern.matcher(progressStr);

        if (!matcher.find()) {
            log.info("没有找到内容");
            return "";
        }
        String progress_find = matcher.group(1);
//        log.info(progress_find);
        return progress_find;
    }


    /**
     * 获取所有的构建模块
     *
     * @return 构建的进度
     */
    public static AllProjectBean getAllProjects() {
        log.info(Api.getAllProject());
        String module_last_build = RequestUtils.post(Api.getAllProject(), null,cookie);
//        log.info(module_last_build);
        AllProjectBean allProjectBean = buildGson().fromJson(module_last_build, AllProjectBean.class);
        return allProjectBean;
    }

    /**
     * 登录jenkins
     * @param pwdStr 用户名
     * @param userStr  密码
     * @return  是否登录成功
     */
    public static boolean login(String userStr, String pwdStr) {

        PropertyUtils propertyUtils = new PropertyUtils(Config.JenkinsPropertiesFileName);
        propertyUtils.getConfiguration2Properties();

        cookie = propertyUtils.getConfigurationPropertyStringByKey("jenkins.cookie");
        //如果没有cookie就登录
        if ("".equals(cookie)){
            boolean isLogin = loginForJenkins(userStr, pwdStr, propertyUtils);
            if (!isLogin){
                return false;
            }
        }else {
            String module_last_build = RequestUtils.post(Api.getAllProject(), null,cookie);
            //如果cookie过期，需要重新登录
            if (module_last_build.equals("403")||module_last_build.equals("")){
                boolean isLogin = loginForJenkins(userStr, pwdStr, propertyUtils);
                if (!isLogin){
                    return false;
                }
            }
        }
        return true;
    }

    private static boolean loginForJenkins(String userStr, String pwdStr, PropertyUtils propertyUtils) {
        Map<String,String> map = new HashMap<>();
        map.put("j_username",userStr);
        map.put("j_password",pwdStr);
        map.put("from","/");
        map.put("remember_me","on");
        String md5 = Utils.getMD5(new Date().toString());
        map.put("jenkins-Crumb",md5);
        String json = buildGson().toJson(map);

        map.put("json", json);
        map.put("Submit","登录");
        String parameters = RequestUtils.formatParameters(map);
        Map<String, Object> loginMap = RequestUtils.postAsMap(Api.getJenkinsLogin(), parameters);
        String location = (String) loginMap.get("location");

        if (location.contains("loginError")){
            return false;
        }
        cookie = (String) loginMap.get("cookie");
        propertyUtils.setConfigurationProperty("jenkins.cookie",cookie);

        return true;
    }

    private static Gson buildGson() {
        Gson gson = new Gson();
        return gson;
    }

}
