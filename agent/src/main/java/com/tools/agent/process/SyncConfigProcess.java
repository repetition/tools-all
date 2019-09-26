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

        switch (methodEnum) {
            case SYNC_CR_CONFIG:
                syncDeployConfig(command, ctx);
                break;

            case SET_CR_CONFIG:
                setDeployConfig(command, ctx);
                break;
        }

    }

    /**
     * 保存回传的配置
     *
     * @param command 指令
     * @param ctx     tcp连接
     */
    private void setDeployConfig(Command command, ChannelHandlerContext ctx) {
        Map<String, Map<String, String>> map = (Map<String, Map<String, String>>) command.getContent();

        for (Map.Entry<String, Map<String, String>> stringMapEntry : map.entrySet()) {

            PropertyUtils propertyUtils = new PropertyUtils(new File(ApplicationConfig.getApplicationConfPath()+stringMapEntry.getKey()));
            propertyUtils.getConfiguration2Properties();
            Map<String, String> value = stringMapEntry.getValue();

            for (Map.Entry<String, String> stringEntry : value.entrySet()) {
                propertyUtils.setConfigurationProperty(stringEntry.getKey(), stringEntry.getValue());
            }
        }
        command.setContent("ok");
        ctx.channel().writeAndFlush(command);
    }

    /**
     * 将本地的配置回传
     *
     * @param command 指令
     * @param ctx     tcp连接
     */
    private void syncDeployConfig(Command command, ChannelHandlerContext ctx) {
        Map<String, Map<String, String>> fileListMap = new HashMap<>();
        Map<String, String> configMap = new HashMap<>();
//deploy.properties
        PropertyUtils propertyUtils = new PropertyUtils(new File(ApplicationConfig.getDeployConfigFilePath()));
        propertyUtils.getConfiguration2Properties();
        Iterator<String> keys = propertyUtils.getConfigurationPropertyKeys();
        //如果当前的配置 文件key为空,则向服务器获取配置
        if (!keys.hasNext()) {
            command.setCommandCode(CommandMethodEnum.SET_CR_CONFIG.getCode());
            command.setCommandMethod(CommandMethodEnum.SET_CR_CONFIG.toString());
            ctx.channel().writeAndFlush(command);
            return;
        }
        while (keys.hasNext()) {
            String key = keys.next();
            String value = propertyUtils.getConfigurationPropertyStringByKey(key);
            configMap.put(key, value);
        }
        fileListMap.put(ApplicationConfig.DEPLOY_CONFIG_FILE_NAME, configMap);
//config_list.properties
        propertyUtils = new PropertyUtils(new File(ApplicationConfig.getConfigListFilePath()));
        propertyUtils.getConfiguration2Properties();

        keys = propertyUtils.getConfigurationPropertyKeys();
        configMap = new HashMap<>();
        while (keys.hasNext()) {
            String key = keys.next();
            String value = propertyUtils.getConfigurationPropertyStringByKey(key);
            configMap.put(key, value);
        }
        fileListMap.put(ApplicationConfig.CONFIG_LIST_FILE_NAME, configMap);

        command.setContent(fileListMap);
        ctx.channel().writeAndFlush(command);
    }

}
