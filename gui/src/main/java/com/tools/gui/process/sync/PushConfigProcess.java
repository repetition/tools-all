package com.tools.gui.process.sync;

import com.tools.commons.utils.FileUtils;
import com.tools.gui.config.ApplicationConfig;
import com.tools.gui.process.CommandMethodEnum;
import com.tools.service.context.ApplicationContext;
import com.tools.service.model.DeployConfigModel;
import com.tools.socket.bean.Command;
import io.netty.channel.ChannelHandlerContext;
import org.apache.commons.collections.map.HashedMap;

import java.io.File;
import java.util.Map;

import static com.tools.commons.utils.Utils.replaceAddress;

/**
 * 将配置推送给指定的部署工具处理类
 */
public class PushConfigProcess extends ProcessServerBase {

    @Override
    protected void processCommand(Command command, ChannelHandlerContext ctx) {

        CommandMethodEnum commandMethodEnum = CommandMethodEnum.getEnum(command.getCommandCode());

        switch (commandMethodEnum) {
            case DOWNLOAD_DEPLOY_CONFIG:

                command.setCommandCode(CommandMethodEnum.DOWNLOAD_DEPLOY_CONFIG.getCode());
                command.setCommandMethod(CommandMethodEnum.DOWNLOAD_DEPLOY_CONFIG.name());

                apacheReplaceConfigSync(command,ctx);
                crConfigSync(command,ctx);
                ctx.channel().writeAndFlush(command);
                break;
        }
    }

    private Command apacheReplaceConfigSync(Command command, ChannelHandlerContext ctx) {

        DeployConfigModel deployConfigModel = ApplicationContext.getDeployConfigModel();

        String httpdOldChangedPath = deployConfigModel.getHttpdOldChangedPath();
        String httpdZYFLChangedPath = deployConfigModel.getHttpdZYFLChangedPath();
        String httpdUpload1TomcatChangedPath = deployConfigModel.getHttpdUpload1TomcatChangedPath();
        String httpdUploadChangedPath = deployConfigModel.getHttpdUploadChangedPath();
        String httpdIPMChangedPath = deployConfigModel.getHttpdIPMChangedPath();

        String address = ctx.channel().remoteAddress().toString();

        Map<String,String> httpdConfigInfoMap = new HashedMap();

        String httpdOld = FileUtils.readFile(httpdOldChangedPath);
        //替换ip
        httpdOld = replaceAddress(httpdOld,address);
        httpdConfigInfoMap.put(new File(httpdOldChangedPath).getName(),httpdOld);

        String httpdZYFL = FileUtils.readFile(httpdZYFLChangedPath);
        //替换ip
        httpdZYFL = replaceAddress(httpdZYFL, address);
        httpdConfigInfoMap.put(new File(httpdZYFLChangedPath).getName(),httpdZYFL);
        //单tomcat替换ip
        String httpdUpload1Tomcat = FileUtils.readFile(httpdUpload1TomcatChangedPath);
        //替换ip
        httpdUpload1Tomcat = replaceAddress(httpdUpload1Tomcat, address);
        httpdConfigInfoMap.put(new File(httpdUpload1TomcatChangedPath).getName(),httpdUpload1Tomcat);

        String httpdUpload = FileUtils.readFile(httpdUploadChangedPath);
        //替换ip
        httpdUpload = replaceAddress(httpdUpload, address);
        httpdConfigInfoMap.put(new File(httpdUploadChangedPath).getName(),httpdUpload);

        String httpdIPM = FileUtils.readFile(httpdIPMChangedPath);
        //替换ip
        httpdIPM = replaceAddress(httpdIPM, address);
        httpdConfigInfoMap.put(new File(httpdIPMChangedPath).getName(),httpdIPM);


        String workersOldChangedPath = deployConfigModel.getWorkersOldChangedPath();
        String wordkersUploadChangedPath = deployConfigModel.getWordkersUploadChangedPath();

        Map<String,String> workerConfigInfoMap = new HashedMap();
        workerConfigInfoMap.put(new File(workersOldChangedPath).getName(),FileUtils.readFile(workersOldChangedPath));
        workerConfigInfoMap.put(new File(wordkersUploadChangedPath).getName(),FileUtils.readFile(wordkersUploadChangedPath));


        Map<String,Map<String,String>> configInfoMaps = new HashedMap();
        configInfoMaps.put("httpdConfig",httpdConfigInfoMap);
        configInfoMaps.put("workerConfig",workerConfigInfoMap);

        command.setContent(configInfoMaps);

        return command;
    }


    private void crConfigSync(Command command, ChannelHandlerContext ctx){

        String crConfigStr = FileUtils.readFile(System.getProperty("conf.path") + ApplicationConfig.DEPLOY_CONFIG_FILE_NAME);
        String address = ctx.channel().remoteAddress().toString();

        crConfigStr = replaceAddress(crConfigStr,address);

        Map<String,String> crConfigInfoMap = new HashedMap();
        crConfigInfoMap.put(ApplicationConfig.DEPLOY_CONFIG_FILE_NAME,crConfigStr);

        Map<String,Map<String,String>> configInfoMaps = (Map<String, Map<String, String>>) command.getContent();
        configInfoMaps.put("crConfig",crConfigInfoMap);
    }



}
