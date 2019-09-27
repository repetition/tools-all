package com.tools.gui.process;


import com.tools.commons.utils.FileUtils;
import com.tools.commons.utils.PropertyUtils;
import com.tools.gui.config.ApplicationConfig;
import com.tools.socket.bean.Command;
import com.tools.socket.bean.FileUpload;
import io.netty.channel.ChannelHandlerContext;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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

                    command.setContent(fileListMap);
                    ctx.channel().writeAndFlush(command);
                    onSyncConfigListener.onSyncComplete();
                }
                break;
            case SYNC_CR_CONFIG:
                //保存从部署服务器的配置
                Map<String, String> map = (Map<String, String>) command.getContent();

                for (Map.Entry<String, String> stringMapEntry : map.entrySet()) {
                    FileUtils.saveFile(ApplicationConfig.getApplicationConfPath() + stringMapEntry.getKey(), stringMapEntry.getValue());
                }

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
