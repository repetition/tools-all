package com.tools.gui.process;


import com.tools.commons.utils.FileUtils;
import com.tools.commons.utils.PropertyUtils;
import com.tools.constant.CommandMethodEnum;
import com.tools.gui.config.ApplicationConfig;
import com.tools.service.context.ApplicationContext;
import com.tools.service.model.DeployConfigModel;
import com.tools.socket.bean.Command;
import com.tools.socket.bean.FileUpload;
import io.netty.channel.ChannelHandlerContext;
import org.apache.commons.collections.map.HashedMap;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.tools.commons.utils.Utils.replaceAddress;

/**
 * 与agent服务 配置文件 同步处理类
 */
public class SyncConfigProcess extends ProcessBase {

    @Override
    public void processCommand(Command command, ChannelHandlerContext ctx) {
        int code = command.getCommandCode();
        CommandMethodEnum methodEnum = CommandMethodEnum.getEnum(code);
        switch (methodEnum) {
            case SET_CR_CONFIG:
                if (command.getContent() != null) {
                    String str = (String) command.getContent();
                    if (str.equals("ok")) {
                        onSyncConfigListener.onSyncComplete();
                    }
                    if (str.equals("fail")) {
                        onSyncConfigListener.onSyncFail();
                    }
                } else {
                    //给agent传输配置
                    Map<String, String> fileListMap = new HashMap<>();

                    String deploy_Config_str = FileUtils.readFile(ApplicationConfig.getDeployConfigFilePath());
                    fileListMap.put(ApplicationConfig.DEPLOY_CONFIG_FILE_NAME, deploy_Config_str);

                    String config_List_str = FileUtils.readFile(ApplicationConfig.getConfigListFilePath());
                    fileListMap.put(ApplicationConfig.CONFIG_LIST_FILE_NAME, config_List_str);

                    //将apache替换模板传送
                 //   command = apacheReplaceConfigSync(command,ctx);

                    command.setContent(fileListMap);
                    ctx.channel().writeAndFlush(command);
                    onSyncConfigListener.onSyncComplete();
                }
                break;
            case SYNC_DEPLOY_CONFIG:
                //保存从部署服务器的配置
                Map<String, String> map = (Map<String, String>) command.getContent();

                for (Map.Entry<String, String> stringMapEntry : map.entrySet()) {
                    FileUtils.saveFile( stringMapEntry.getValue(),ApplicationConfig.getApplicationConfPath() + stringMapEntry.getKey());
                }
                onSyncConfigListener.onSyncComplete();
                break;

            case GET_CONFIG_FILE:
                FileUpload fileUpload = (FileUpload) command.getContent();
                if (fileUpload.getState() == FileUpload.SUCCESS) {
                    byte[] bytes = fileUpload.getBytes();
                    String fileName = fileUpload.getFileName();
                    FileUtils.saveFileForBytes(bytes, ApplicationConfig.getApplicationConfPath() + fileName);
                    onFileGetListener.onFile(ApplicationConfig.getApplicationConfPath() + fileName);
                }
                break;
        }

    }

    private Command apacheReplaceConfigSync(Command command, ChannelHandlerContext ctx) {

        DeployConfigModel deployConfigModel = ApplicationContext.getDeployConfigModel();
        Map<String, String> fileListMap = (Map<String, String>) command.getContent();

        String httpdOldChangedPath = deployConfigModel.getHttpdOldChangedPath();
        String httpdZYFLChangedPath = deployConfigModel.getHttpdZYFLChangedPath();
        String httpdUpload1TomcatChangedPath = deployConfigModel.getHttpdUpload1TomcatChangedPath();
        String httpdUploadChangedPath = deployConfigModel.getHttpdUploadChangedPath();
        String httpdIPMChangedPath = deployConfigModel.getHttpdIPMChangedPath();

        String address = ctx.channel().remoteAddress().toString();

        String httpdOld = FileUtils.readFile(httpdOldChangedPath);
        //替换ip
        httpdOld = replaceAddress(httpdOld,address);
        fileListMap.put(new File(httpdOldChangedPath).getName(),httpdOld);

        String httpdZYFL = FileUtils.readFile(httpdZYFLChangedPath);
        //替换ip
        httpdZYFL = replaceAddress(httpdZYFL, address);
        fileListMap.put(new File(httpdZYFLChangedPath).getName(),httpdZYFL);
        //单tomcat替换ip
        String httpdUpload1Tomcat = FileUtils.readFile(httpdUpload1TomcatChangedPath);
        //替换ip
        httpdUpload1Tomcat = replaceAddress(httpdUpload1Tomcat, address);
        fileListMap.put(new File(httpdUpload1TomcatChangedPath).getName(),httpdUpload1Tomcat);

        String httpdUpload = FileUtils.readFile(httpdUploadChangedPath);
        //替换ip
        httpdUpload = replaceAddress(httpdUpload, address);
        fileListMap.put(new File(httpdUploadChangedPath).getName(),httpdUpload);

        String httpdIPM = FileUtils.readFile(httpdIPMChangedPath);
        //替换ip
        httpdIPM = replaceAddress(httpdIPM, address);
        fileListMap.put(new File(httpdIPMChangedPath).getName(),httpdIPM);


        String workersOldChangedPath = deployConfigModel.getWorkersOldChangedPath();
        String wordkersUploadChangedPath = deployConfigModel.getWordkersUploadChangedPath();


        fileListMap.put(new File(workersOldChangedPath).getName(),FileUtils.readFile(workersOldChangedPath));
        fileListMap.put(new File(wordkersUploadChangedPath).getName(),FileUtils.readFile(wordkersUploadChangedPath));

        command.setContent(fileListMap);

        return command;
    }

    @Override
    protected void error() {
        super.error();
        onFileGetListener.onFail();
    }

    private OnSyncConfigListener onSyncConfigListener;

    public void setOnSyncConfigListener(OnSyncConfigListener onSyncConfigListener) {
        this.onSyncConfigListener = onSyncConfigListener;
    }

    public interface OnSyncConfigListener {
        void onSyncComplete();

        void onSyncFail();
    }

    private OnFileGetListener onFileGetListener;

    public void setOnFileGetListener(OnFileGetListener onFileGetListener) {
        this.onFileGetListener = onFileGetListener;
    }

    public interface OnFileGetListener {
        void onFile(String filePath);
        void onFail();
    }

}
