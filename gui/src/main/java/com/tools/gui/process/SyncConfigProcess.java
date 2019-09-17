package com.tools.gui.process;


import com.tools.commons.utils.PropertyUtils;
import com.tools.gui.config.ApplicationConfig;
import com.tools.socket.bean.Command;
import io.netty.channel.ChannelHandlerContext;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

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
                    Map<String,String> configMap = new HashMap<>();

                    PropertyUtils propertyUtils = new PropertyUtils(new File(System.getProperty("conf.path")  + ApplicationConfig.DEPLOY_CONFIG_FILE_NAME));
                    propertyUtils.getConfiguration2Properties();
                    Iterator<String> keys = propertyUtils.getConfigurationPropertyKeys();
                    //如果当前的配置 文件key为空,则向服务器获取配置
                    while (keys.hasNext()) {
                        String key = keys.next();
                        String value = propertyUtils.getConfigurationPropertyStringByKey(key);
                        configMap.put(key,value);
                    }
                    command.setContent(configMap);
                    ctx.channel().writeAndFlush(command);
                    onSyncConfigListener.onSyncComplete();
                }
                break;
            case SYNC_CR_CONFIG:
                //保存从部署服务器的配置
                Map<String,String> map = (Map<String, String>) command.getContent();
                PropertyUtils propertyUtils = new PropertyUtils(new File(System.getProperty("conf.path") + ApplicationConfig.DEPLOY_CONFIG_FILE_NAME));
                propertyUtils.getConfiguration2Properties();
                for (Map.Entry<String, String> entry : map.entrySet()) {
                    propertyUtils.setConfigurationProperty(entry.getKey(),entry.getValue());
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
