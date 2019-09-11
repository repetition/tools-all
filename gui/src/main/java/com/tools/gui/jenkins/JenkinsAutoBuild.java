package com.tools.gui.jenkins;

import com.tools.commons.utils.PropertyUtils;
import com.tools.gui.config.Config;
import com.tools.gui.download.ChunkInfo;
import com.tools.gui.download.DownloadMain;
import com.tools.gui.download.DownloadThread;
import com.tools.gui.jenkins.bean.AllProjectBean;
import com.tools.gui.jenkins.bean.LastBuildBean;
import com.tools.gui.jenkins.bean.ModuleInfo;
import com.tools.gui.jenkins.bean.ProjectDetailBean;
import com.tools.gui.utils.view.NotificationsBuild;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;

public class JenkinsAutoBuild {

    private static final Logger log = LoggerFactory.getLogger(JenkinsAutoBuild.class);
    public static List<ModuleInfo> moduleInfoList = new ArrayList<>();

    public static final int MODULE_BUILDING = 0;
    public static final int MODULE_SUCCESS = 1;
    public static final int MODULE_FAIL = 2;
    private static String lastProgress;

    public static void main(String[] args) {
        moduleBuildStart();
    }

    public static void moduleBuildStart() {

        log.info("总数：" + moduleInfoList.size());
        int building = 0;
        for (ModuleInfo moduleInfo : moduleInfoList) {
            building++;
            if (building > moduleInfoList.size()) {
                building = 1;
            }
            //回调
            if (null != mOnBuildListener) {
                mOnBuildListener.onBuildInfo(moduleInfoList.size(), building, moduleInfo.getProjectName(), moduleInfo.getProjectModuleBuildName());
                mOnBuildListener.onBuildState(moduleInfo.getProjectName(), moduleInfo.getProjectModuleBuildName(), MODULE_BUILDING);
            }
            log.info("正在构建 ......." + moduleInfo.getProjectName() + moduleInfo.getProjectModuleName());
            log.info("总数：" + building + "/" + moduleInfoList.size());
            //配置信息
            ProjectBuild.config(moduleInfo);

            boolean isBuild = ProjectBuild.moduleBuild();
            try {
                Thread.sleep(2000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (isBuild) {
                //获取Jenkins打包详情页
                ProjectDetailBean moduleDetails = ProjectBuild.getModuleDetails();
      /*          //最后成功的构建
                int lastSuccessfulBuildNum = moduleDetails.getLastSuccessfulBuild().getNumber();
                //最新的一次构建
                int lastBuildNum = moduleDetails.getLastBuild().getNumber();

                if (lastBuildNum == lastSuccessfulBuildNum) {

                }*/
                //获取最新构建编号
                int lastBuildNum = moduleDetails.getLastBuild().getNumber();
                String aClass = moduleDetails.getLastSuccessfulBuild().get_class();
                String url = moduleDetails.getLastSuccessfulBuild().getUrl();
                int lastSuccessfulBuildNum = moduleDetails.getLastSuccessfulBuild().getNumber();

                log.info("---------------------------");
                log.info(url);
                log.info("lastSuccessfulBuildNum:" + lastSuccessfulBuildNum);
                log.info("lastBuildNum:" + lastBuildNum);
                log.info("---------------------------");

                while (true) {
                    try {
                        Thread.sleep(1000L);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    String progress = ProjectBuild.getProjectModuleBuildProgress();
                    //回调进度
                    //记录上次progress
                    if (!progress.equals("")) {
                        lastProgress = progress;
                    }
                    //如果不为空，上次有进度 则直接为打包成功，将进度改为100
                    if (progress.equals("") && !lastProgress.equals("")) {
                        progress = "100";
                    }
                    Double progressDouble = Double.valueOf(progress);
                    progressDouble = progressDouble / 100;
                    if (null != mOnBuildListener) {
                        mOnBuildListener.onBuildProgress(String.valueOf(progressDouble), moduleInfo.getProjectName(), moduleInfo.getProjectModuleName());
                    }
                    // log.info(progress);
                    log.info(progressDouble + "");
                    LastBuildBean moduleLastBuild = ProjectBuild.getProjectModuleLastBuild();
                    //   log.info("moduleLastBuild.isBuilding():"+moduleLastBuild.isBuilding());
                    //构建成功
                    if (!moduleLastBuild.isBuilding()) {
                        for (LastBuildBean.ArtifactsBean artifactsBean : moduleLastBuild.getArtifacts()) {
                  /*          log.info(artifactsBean.getFileName());
                            log.info(artifactsBean.getDisplayPath());*/
                            log.info(artifactsBean.getRelativePath());
                        }
                        log.info("构建成功 " + moduleInfo.getProjectName() + moduleInfo.getProjectModuleName());
                        if (null != mOnBuildListener) {
                            mOnBuildListener.onBuildProgress(progress, moduleInfo.getProjectName(), moduleInfo.getProjectModuleName());
                            mOnBuildListener.onBuildState(moduleInfo.getProjectName(), moduleInfo.getProjectModuleBuildName(), MODULE_SUCCESS);
                        }

                        String fullDisplayName = moduleLastBuild.getFullDisplayName();

                        if (fullDisplayName.contains("thinkwin-cr")) {
                            //"fullDisplayName": "中油瑞飞-thinkwin-cr #9",   将fullDisplayName截取
                            fullDisplayName = fullDisplayName.substring(0, fullDisplayName.indexOf(" #" + moduleLastBuild.getId()));
                            fullDisplayName = fullDisplayName.substring(0, fullDisplayName.indexOf("-"));
                            fullDisplayName = fullDisplayName + "-thinkwin-cr";
                        }
                        if (fullDisplayName.contains("thinkwin-cm")) {
                            //"fullDisplayName": "中油瑞飞-thinkwin-cr #9",   将fullDisplayName截取
                            fullDisplayName = fullDisplayName.substring(0, fullDisplayName.indexOf(" #" + moduleLastBuild.getId()));
                            fullDisplayName = fullDisplayName.substring(0, fullDisplayName.indexOf("-"));
                            fullDisplayName = fullDisplayName + "-thinkwin-cm";
                        }
                        log.info("fullDisplayName:" + fullDisplayName);
                        if (moduleLastBuild.getResult().equals("SUCCESS") && moduleInfo.getProjectModuleBuildName().equals(fullDisplayName)) {
                            mOnBuildListener.onProjectBuildSuccess(moduleInfo.getProjectModuleName(), moduleInfo.getProjectModuleBuildName(), MODULE_SUCCESS);
                            //building = moduleInfoList.size();
                        }
                        /*if (moduleLastBuild.get)*/
                        break;
                    }
                }
            } else {
                log.info("构建失败");
                break;
            }
        }
    }


    public static List<ModuleInfo> batchBuildList(){
        List<ModuleInfo> batchBuildList = new ArrayList<>();

        try {
            FileInputStream fis = new FileInputStream("D:\\Desktop\\nexus-exchange-base.txt");
            InputStreamReader reader = new InputStreamReader(fis, Charset.forName("utf-8"));
            BufferedReader bufferedReader = new BufferedReader(reader);
            String line = "";
            String result = "";
            StringBuilder builder = new StringBuilder();
            while ((line = bufferedReader.readLine()) != null) {
                String moduleStr = line;
                String moduleName = moduleStr.substring(moduleStr.indexOf("-"));

                ModuleInfo moduleInfo = new ModuleInfo();
                moduleInfo.setProjectName("nexus");
                moduleInfo.setProjectModuleName(moduleName);
                moduleInfo.setProjectModuleBuildName("nexus" + moduleName);
                ProjectBuild.config(moduleInfo);
                boolean exist = ProjectBuild.isExist();
                if (exist) {
                  //  moduleInfoList.add(moduleInfo);
                    batchBuildList.add(moduleInfo);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return batchBuildList;
    }


    public static void  batchBuildStart(){
        List<ModuleInfo> moduleInfos = batchBuildList();
        log.info("构建总数："+moduleInfos.size());
        int i = 0;
        Collections.reverse(moduleInfos);
        for (ModuleInfo moduleInfo : moduleInfos) {
            ProjectBuild.config(moduleInfo);
            boolean isBuild = ProjectBuild.moduleBuild();
            if (isBuild) {
                log.info("构建总数："+moduleInfos.size()+" , 已经发起构建："+(i++));
            }
        }
        NotificationsBuild.showBottomRightNotification("构建成功", "nexus" + "\r\n 构建成功！", 10.0);

    }

    /**
     * 获取构建列表
     *
     * @param projectName
     * @return
     */
    public static void moduleBuild(String projectName) {
        moduleInfoList.clear();
        // moduleConfig();

        Properties properties = new PropertyUtils(Config.JenkinsPropertiesFileName).getOrderedProperties();
        Set<String> propertyNames = properties.stringPropertyNames();
        for (String name : propertyNames) {
            if (name.contains("jenkins.module")) {
                String moduleStr = properties.getProperty(name);
                String moduleName = moduleStr.substring(moduleStr.indexOf("-"));
                ModuleInfo moduleInfo = new ModuleInfo();
                moduleInfo.setProjectName(projectName);
                moduleInfo.setProjectModuleName(moduleName);
                moduleInfo.setProjectModuleBuildName(projectName + moduleName);

                ProjectBuild.config(moduleInfo);

                boolean exist = ProjectBuild.isExist();
                if (exist) {
                    moduleInfoList.add(moduleInfo);
                }
            }
        }
        //将list倒序
        Collections.reverse(moduleInfoList);
        //回调
        mOnBuildListener.onModuleCount(moduleInfoList.size(), 0, projectName, moduleInfoList);
    }


    public static void downloadStart() {
        DownloadThread.setOnDownloadListener(new DownloadThread.OnDownloadListener() {
            @Override
            public void onDownloadSuccess(int state, String path) {
                mOnDownloadListener.onDownloadSuccess(state, path);
            }

            @Override
            public void onDownloadFail(int state, String path) {
                mOnDownloadListener.onDownloadFail(state, path);

            }

            @Override
            public void onDownloadProgress(int state, long progress, long totalFileSize) {
                mOnDownloadListener.onDownloadProgress(state, progress, totalFileSize);
            }
        });

        String downloadLastSuccessfulROOTUrl = ProjectBuild.downloadLastSuccessfulROOT();

        log.info(downloadLastSuccessfulROOTUrl);
        long totalSize = DownloadMain.getFileTotalSize(downloadLastSuccessfulROOTUrl);
        DownloadMain.createTempFile(totalSize);

        List<ChunkInfo> chunkInfos = DownloadMain.buildChunkInfos(totalSize, downloadLastSuccessfulROOTUrl);
        // List<ChunkInfo> chunkInfos = DownloadMain.buildChunkInfos2((int) totalSize);
        // List<ChunkInfo> chunkInfos = DownloadMain.buildChunkInfos3((int) totalSize);
        DownloadMain.downloadStart(chunkInfos);
    }

    /**
     * 搜索所有的ROOT项目
     *
     * @param type 搜索的类型
     * @return
     */
    public static List<String> searchProject(String type) {
        List<String> searchList = new ArrayList<>();
        AllProjectBean allProjects = ProjectBuild.getAllProjects();
        List<AllProjectBean.JobsBean> jobs = allProjects.getJobs();
        log.info("模块总数：" + jobs.size());
        for (AllProjectBean.JobsBean job : jobs) {
            if (job.getColor().equals("disabled")) {
                continue;
            }
            //过滤获取所有cr包
            String jobName = job.getName();
            if (jobName.contains("thinkwin-cr")) {
                String[] split = jobName.split("thinkwin-cr");
                //  log.info("jobName:" + jobName + "| length:" + split.length + "");
                if (split.length == 1) {
                    searchList.add(jobName);
                }
            }
            //过滤获取所有cm包
            if (jobName.contains("thinkwin-cm")) {
                String[] split = jobName.split("thinkwin-cm");
                //  log.info("jobName:" + jobName + "| length:" + split.length + "");
                if (split.length == 1) {
                    searchList.add(jobName);
                }
            }
            if (type.equals("build")) {

            }
            if (type.equals("download")){
                //主线
                if (jobName.contains("thinkwin-cm-all-tomcat") || jobName.contains("thinkwin-cr-all-tomcat")) {
                    searchList.add(jobName);
                    continue;
                }
            }
      /*      //自定义搜索条件
            if (null != type) {
                if (jobName.contains(type)) {
                    searchList.add(jobName);
                }
            }*/
        }
        return searchList;
    }

    private static Properties moduleRead() {
        FileInputStream fileInputStream = null;
        try {
            File file = new File("E:\\JenkinsModuleS.properties");
            if (!file.exists()) {
                file.createNewFile();
            }
            Properties properties = new Properties();
            fileInputStream = new FileInputStream(file);
            properties.load(new FileInputStream(file));
            return properties;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }finally {
            if (null!=fileInputStream){
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void moduleConfig() {
        FileOutputStream fileOutputStream = null;
        FileInputStream fileInputStream = null;
        try {
            File file = new File("E:\\JenkinsModuleS.properties");
            if (!file.exists()) {
                file.createNewFile();
            }
            // TODO: 2019/1/17 暂没有使用最新封装配置文件，以后使用再进行添加
            Properties properties = new Properties();
            fileInputStream = new FileInputStream(file);
            properties.load(fileInputStream);
            properties.setProperty("1", "失败重试机制-thinkwin-cr");
            properties.setProperty("2", "失败重试机制-thinkwin-cr-base");
            properties.setProperty("3", "失败重试机制-thinkwin-publish");
            properties.setProperty("4", "失败重试机制-thinkwin-base-old");
            properties.setProperty("5", "失败重试机制-thinkwin-extjs-ui");
            properties.setProperty("6", "失败重试机制-thinkwin-base-user");
            fileOutputStream = new FileOutputStream("E:\\JenkinsModuleS.properties");
            properties.store(fileOutputStream, "comment");
            fileInputStream.close();
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (null != fileInputStream) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (null!=fileOutputStream){
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static OnBuildListener mOnBuildListener;

    public static void setOnBuildListener(OnBuildListener onBuildListener) {
        mOnBuildListener = onBuildListener;
    }

    public interface OnBuildListener {
        void onBuildProgress(String progress, String projectName, String projectModuleName);

        void onBuildInfo(int moduleCount, int buildNum, String projectName, String projectModuleName);

        /**
         * 返回构建的总数，构建的列表
         *
         * @param moduleCount    构建的模块总数
         * @param buildNum       正在构建的数
         * @param projectName    构建的项目名字
         * @param moduleInfoList 构建的列表详情
         */
        void onModuleCount(int moduleCount, int buildNum, String projectName, List<ModuleInfo> moduleInfoList);

        void onBuildState(String projectName, String projectModuleName, int State);

        void onProjectBuildSuccess(String projectModuleName, String projectModuleBuildName, int state);
    }


    private static OnDownloadListener mOnDownloadListener;

    public static void setOnDownloadListener(OnDownloadListener onDownloadListener) {
        mOnDownloadListener = onDownloadListener;
    }

    public interface OnDownloadListener {

        void onDownloadSuccess(int state, String path);

        void onDownloadFail(int state, String path);

        void onDownloadProgress(int state, long progress, long totalFileSize);
    }

}
