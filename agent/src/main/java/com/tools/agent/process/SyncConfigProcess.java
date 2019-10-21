package com.tools.agent.process;


import com.tools.agent.ApplicationConfig;
import com.tools.commons.utils.FileUtils;
import com.tools.commons.utils.PropertyUtils;
import com.tools.constant.CommandMethodEnum;
import com.tools.socket.bean.Command;
import com.tools.socket.bean.FileUpload;
import io.netty.channel.ChannelHandlerContext;

import java.io.File;
import java.util.*;

public class SyncConfigProcess extends ProcessBase {

    @Override
    public void processCommand(Command command, ChannelHandlerContext ctx) {

        int code = command.getCommandCode();
        CommandMethodEnum methodEnum = CommandMethodEnum.getEnum(code);

        switch (methodEnum) {
            case SYNC_DEPLOY_CONFIG:
                syncDeployConfig(command, ctx);
                break;

            case SET_CR_CONFIG:
                setDeployConfig(command, ctx);
                break;

            case SYNC_APACHE_CONFIG:
                syncApacheConfig(command, ctx);
                break;
            case SYNC_RUNTIME_CHANGER_CONFIG:
                syncRuntimeChanger(command, ctx);
                break;

            case GET_CONFIG_FILE:

                getConfigFile(command, ctx);

                break;
            case SAVE_CONFIG_FILE:

                saveConfigFile(command, ctx);

                break;
        }

    }

    /**
     * 保存从gui回传的 部署时的配置修改
     * @param command 命令
     * @param ctx 通道
     */
    private void syncRuntimeChanger(Command command, ChannelHandlerContext ctx) {

        List<FileUpload> changedPropertiesFiles = (List<FileUpload>) command.getContent();

        for (FileUpload fileUpload : changedPropertiesFiles) {

            FileUtils.saveFileForBytes(fileUpload.getBytes(),ApplicationConfig.getApplicationConfPath()+fileUpload.getFileName());
        }

        command.setContent("ok");
        ctx.channel().writeAndFlush(command);

    }

    /**
     * 回传单次修改的配置文件
     * @param command
     * @param ctx
     */
    private void saveConfigFile(Command command, ChannelHandlerContext ctx) {

        FileUpload fileUpload = (FileUpload) command.getContent();
        if (fileUpload.getState() == FileUpload.SUCCESS) {
            byte[] bytes = fileUpload.getBytes();
            FileUtils.saveFileForBytes(bytes, fileUpload.getFileFlag());
        }
    }

    private void getConfigFile(Command command, ChannelHandlerContext ctx) {

        Map<String, String> content = (Map<String, String>) command.getContent();

        String filePath = content.get("filePath");
        FileUpload fileUpload = new FileUpload();
        File file = new File(filePath);
        if (!file.exists()) {
            fileUpload.setState(FileUpload.FAIL);
            fileUpload.setDesc("文件不存在!");
            command.setContent(fileUpload);
            ctx.channel().writeAndFlush(command);
            return;
        }
        byte[] bytes = FileUtils.readFileToByte(filePath);
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
            FileUtils.saveFile( stringMapEntry.getValue(),ApplicationConfig.getApplicationConfPath() + stringMapEntry.getKey());
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

        //运行时修改的配置文件更改
        Properties properties = new PropertyUtils(ApplicationConfig.getConfigListFilePath()).getOrderedProperties();

        Set<String> propertyNames = properties.stringPropertyNames();
        for (String propertyName : propertyNames) {
            //获取每个配置文件的路径信息
            String filePath = properties.getProperty(propertyName);
            File file = new File(filePath);
            File changedPropertiesFile = new File(ApplicationConfig.getApplicationConfPath() + file.getName() + ".Changed.properties");
            //运行时配置文件不存在则跳过
            if (!changedPropertiesFile.exists()) {
                continue;
            }
            String changerStr = FileUtils.readFile(changedPropertiesFile.getAbsolutePath());
            fileListMap.put(changedPropertiesFile.getName(),changerStr);
        }

        command.setContent(fileListMap);
        ctx.channel().writeAndFlush(command);
    }

}
