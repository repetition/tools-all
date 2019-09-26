package com.tools.gui.process;


import com.tools.commons.utils.PropertyUtils;
import com.tools.gui.config.ApplicationConfig;
import com.tools.socket.bean.Command;
import io.netty.channel.ChannelHandlerContext;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 获取agent已有配置的处理类
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
                }else {
                    //给agent传输配置
                    Map<String,Map<String,String>> fileListMap = new HashMap<>();

                    Map<String,String> configMap = new HashMap<>();

                    PropertyUtils propertyUtils = new PropertyUtils(new File( ApplicationConfig.getDeployConfigFilePath()));
                    propertyUtils.getConfiguration2Properties();
                    Iterator<String> keys = propertyUtils.getConfigurationPropertyKeys();
                    //如果当前的配置 文件key为空,则向服务器获取配置
                    while (keys.hasNext()) {
                        String key = keys.next();
                        String value = propertyUtils.getConfigurationPropertyStringByKey(key);
                        configMap.put(key,value);
                    }
                    fileListMap.put(ApplicationConfig.DEPLOY_CONFIG_FILE_NAME,configMap);

                    propertyUtils = new PropertyUtils(new File( ApplicationConfig.getConfigListFilePath()));
                    propertyUtils.getConfiguration2Properties();

                    configMap = new HashMap<>();
                    //如果当前的配置 文件key为空,则向服务器获取配置
                    while (keys.hasNext()) {
                        String key = keys.next();
                        String value = propertyUtils.getConfigurationPropertyStringByKey(key);
                        configMap.put(key,value);
                    }
                    fileListMap.put(ApplicationConfig.CONFIG_LIST_FILE_NAME,configMap);

                    command.setContent(fileListMap);
                    ctx.channel().writeAndFlush(command);
                    onSyncConfigListener.onSyncComplete();
                }
                break;
            case SYNC_CR_CONFIG:
                //保存从部署服务器的配置
                Map<String, Map<String, String>> map = (Map<String, Map<String, String>>) command.getContent();

                for (Map.Entry<String, Map<String, String>> stringMapEntry : map.entrySet()) {

                    PropertyUtils propertyUtils = new PropertyUtils(new File(ApplicationConfig.getApplicationConfPath()+stringMapEntry.getKey()));
                    propertyUtils.getConfiguration2Properties();
                    Map<String, String> value = stringMapEntry.getValue();

                    for (Map.Entry<String, String> stringEntry : value.entrySet()) {
                        propertyUtils.setConfigurationProperty(stringEntry.getKey(), stringEntry.getValue());
                    }
                }

                break;
        }

    }

    private OnSyncConfigListener onSyncConfigListener;

    public void setOnSyncConfigListener(OnSyncConfigListener onSyncConfigListener) {
        this.onSyncConfigListener = onSyncConfigListener;
    }

    public interface OnSyncConfigListener {
        void onSyncComplete();

        void onSyncFail();
    }

}
