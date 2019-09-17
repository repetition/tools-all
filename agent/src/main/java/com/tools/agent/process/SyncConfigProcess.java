package com.tools.agent.process;


import com.tools.agent.ApplicationConfig;
import com.tools.commons.utils.PropertyUtils;
import com.tools.service.context.ApplicationContext;
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

        switch (methodEnum){
            case SYNC_CR_CONFIG:
                syncCRConfig(command,ctx);
                break;

            case SET_CR_CONFIG:
                setCRConfig(command,ctx);
                break;
        }

    }

    private void setCRConfig(Command command, ChannelHandlerContext ctx) {
        Map<String,String> map = (Map<String, String>) command.getContent();
        String confPath = ApplicationContext.getApplicationConfPath();
        PropertyUtils propertyUtils = new PropertyUtils(new File(confPath + ApplicationConfig.DEPLOY_CONFIG_FILE_NAME));
        propertyUtils.getConfiguration2Properties();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            propertyUtils.setConfigurationProperty(entry.getKey(),entry.getValue());
        }
        command.setContent("ok");
        ctx.channel().writeAndFlush(command);
    }

    private void syncCRConfig(Command command, ChannelHandlerContext ctx) {
        String confPath = ApplicationContext.getApplicationConfPath();

        Map<String,String> configMap = new HashMap<>();

        PropertyUtils propertyUtils = new PropertyUtils(new File(confPath + ApplicationConfig.DEPLOY_CONFIG_FILE_NAME));
        propertyUtils.getConfiguration2Properties();
        Iterator<String> keys = propertyUtils.getConfigurationPropertyKeys();
        //如果当前的配置 文件key为空,则向服务器获取配置
        if (!keys.hasNext()) {
            command.setCommandCode(CommandMethodEnum.SET_CR_CONFIG.getCode());
            command.setCommandMethod(CommandMethodEnum.SET_CR_CONFIG.toString());
        }
        while (keys.hasNext()) {
            String key = keys.next();
            String value = propertyUtils.getConfigurationPropertyStringByKey(key);
            configMap.put(key,value);
        }
        command.setContent(configMap);
        ctx.channel().writeAndFlush(command);
    }

}
