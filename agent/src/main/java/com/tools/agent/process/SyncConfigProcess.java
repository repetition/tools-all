package com.tools.agent.process;


import com.tools.agent.ApplicationConfig;
import com.tools.commons.utils.FileUtils;
import com.tools.commons.utils.PropertyUtils;
import com.tools.service.context.ApplicationContext;
import com.tools.socket.bean.Command;
import com.tools.socket.bean.FileUpload;
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

            case SYNC_APACHE_CONFIG:
                syncApacheConfig(command, ctx);
                break;

            case GET_CONFIG_FILE:

                getConfigFile(command, ctx);

                break;
        }

    }

    private void getConfigFile(Command command, ChannelHandlerContext ctx) {

        Map<String, String> content = (Map<String, String>) command.getContent();

        String filePath = content.get("filePath");

        byte[] bytes = FileUtils.readFileToByte(filePath);

        File file = new File(filePath);
        FileUpload fileUpload = new FileUpload();

        fileUpload.setFile(file);
        fileUpload.setFileName(file.getName());
        fileUpload.setBytes(bytes);
        fileUpload.setState(FileUpload.SUCCESS);
        command.setContent(fileUpload);
        ctx.channel().writeAndFlush(command);
    }

    private void syncApacheConfig(Command command, ChannelHandlerContext ctx) {

        Object content = command.getContent();

        if (content instanceof Map) {

            Map<String, Map<String, String>> contentMaps = ((Map) content);

            Map<String, String> httpdConfigMap = contentMaps.get("httpdConfig");

            for (Map.Entry<String, String> stringEntry : httpdConfigMap.entrySet()) {
                //保存配置
                FileUtils.saveFile(stringEntry.getValue(), ApplicationConfig.getApplicationConfPath() + stringEntry.getKey());
            }
            Map<String, String> workerConfigMap = contentMaps.get("workerConfig");
            for (Map.Entry<String, String> stringEntry : workerConfigMap.entrySet()) {
                //保存配置
                FileUtils.saveFile(stringEntry.getValue(), ApplicationConfig.getApplicationConfPath() + stringEntry.getKey());
            }

            command.setContent("ok");
            ctx.channel().writeAndFlush(command);
        }

    }

    /**
     * 保存回传的配置
     *
     * @param command 指令
     * @param ctx     tcp连接
     */
    private void setDeployConfig(Command command, ChannelHandlerContext ctx) {
        Map<String, String> map = (Map<String, String>) command.getContent();

        for (Map.Entry<String, String> stringMapEntry : map.entrySet()) {
            FileUtils.saveFile(ApplicationConfig.getApplicationConfPath() + stringMapEntry.getKey(), stringMapEntry.getValue());
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
        Map<String, String> fileListMap = new HashMap<>();
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

        String deploy_Config_str = FileUtils.readFile(ApplicationConfig.getDeployConfigFilePath());
        fileListMap.put(ApplicationConfig.DEPLOY_CONFIG_FILE_NAME, deploy_Config_str);

        String config_List_str = FileUtils.readFile(ApplicationConfig.getConfigListFilePath());
        fileListMap.put(ApplicationConfig.CONFIG_LIST_FILE_NAME, config_List_str);

        command.setContent(fileListMap);
        ctx.channel().writeAndFlush(command);
    }

}
