package com.tools.gui.process.sync;

import com.tools.commons.utils.FileUtils;
import com.tools.constant.CommandMethodEnum;
import com.tools.socket.bean.Command;
import io.netty.channel.ChannelHandlerContext;

import java.util.Map;

/**
 * 从指定的部署工具下载配置处理器
 */
public class DownloadConfigProcess extends ProcessClientBase {

    @Override
    protected void processCommand(Command command, ChannelHandlerContext ctx) {

        CommandMethodEnum commandMethodEnum = CommandMethodEnum.getEnum(command.getCommandCode());

        switch (commandMethodEnum) {
            case DOWNLOAD_DEPLOY_CONFIG:
                saveConfig(command,ctx);
                break;
        }
    }

    /**
     * 保存从服务器获取的配置
     * @param command
     * @param ctx
     */
    private void saveConfig(Command command, ChannelHandlerContext ctx) {

        Object content = command.getContent();

        if (content instanceof Map) {

            Map<String, Map<String, String>> contentMaps = ((Map) content);

            Map<String, String> httpdConfigMap = contentMaps.get("httpdConfig");

            for (Map.Entry<String, String> stringEntry : httpdConfigMap.entrySet()) {
                //保存配置
                FileUtils.saveFile(stringEntry.getValue(), System.getProperty("conf.path") + stringEntry.getKey());
            }
            Map<String, String> workerConfigMap = contentMaps.get("workerConfig");
            for (Map.Entry<String, String> stringEntry : workerConfigMap.entrySet()) {
                //保存配置
                FileUtils.saveFile(stringEntry.getValue(), System.getProperty("conf.path") + stringEntry.getKey());
            }

            Map<String, String> crConfigMap = contentMaps.get("crConfig");
            for (Map.Entry<String, String> stringEntry : crConfigMap.entrySet()) {
                //保存配置
                FileUtils.saveFile(stringEntry.getValue(), System.getProperty("conf.path") + stringEntry.getKey());
            }

            onDownloadListener.onSuccess();
        }
    }
    public OnDownloadListener onDownloadListener;

    public void setOnDownloadListener(OnDownloadListener onDownloadListener) {
        this.onDownloadListener = onDownloadListener;
    }
    public interface OnDownloadListener{
        void onSuccess();
    }
}
