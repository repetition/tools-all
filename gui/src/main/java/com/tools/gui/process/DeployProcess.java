package com.tools.gui.process;

import com.tools.commons.utils.FileUtils;
import com.tools.commons.utils.IOUtils;
import com.tools.commons.utils.PropertyUtils;
import com.tools.commons.utils.StringUtils;
import com.tools.gui.config.ApplicationConfig;
import com.tools.service.context.ApplicationContext;
import com.tools.service.model.DeployConfigModel;
import com.tools.service.model.DeployState;
import com.tools.socket.bean.Command;
import com.tools.socket.bean.FileUpload;
import io.netty.channel.ChannelHandlerContext;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections.map.HashedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.time.Duration;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.tools.commons.utils.Utils.replaceAddress;

public class DeployProcess extends ProcessBase {
    private static final Logger log = LoggerFactory.getLogger(DeployProcess.class);
    //保存正在传输的文件总数
    private LinkedHashSet<CommandMethodEnum> commandMethodEnumSet = new LinkedHashSet<>();

    @Override
    protected void processCommand(Command command, ChannelHandlerContext ctx) {
        log.info(command.toString() + " , " + Thread.currentThread().getName());
        CommandMethodEnum commandMethodEnum = CommandMethodEnum.getEnum(command.getCommandCode());
        switch (commandMethodEnum) {
            case DEPLOY_INIT:
                break;
            case SYNC_CM_WAR:
                syncCMWar(command, ctx);
                break;
            case SYNC_ZYFL_WAR:
                syncZYFLWar(command, ctx);
                break;
            case SYNC_UPLOAD_WAR:
                syncUploadWar(command, ctx);
                break;
            case SYNC_APACHE_CONFIG:
                syncApacheConfig(command, ctx);
                break;

            case SYNC_RUNTIME_CHANGER_CONFIG:
                syncRuntimeChanger(command, ctx);
                break;

            case DEPLOY_START_PROGRESS:
                if (command.getContent() instanceof DeployState) {
                    onDeployProcessorListener.onDeployProcessSuccess((DeployState) command.getContent());
                }
                break;
            case DEPLOY_START_FAIL:
                if (command.getContent() instanceof DeployState) {
                    onDeployProcessorListener.onDeployProcessFail((DeployState) command.getContent());
                }
                break;
            case DEPLOY_END:
                onDeployProcessorListener.onDeployProcessorEnd();
                break;
        }

    }

    @Override
    protected void processFileUpload(FileUpload fileUpload, ChannelHandlerContext ctx) {

        if (fileUpload.getState() == FileUpload.SUCCESS) {
            log.info("文件传输成功" + fileUpload.getFileName() + " , " + fileUpload.getFileType());
            DeployState deployState = new DeployState();
            deployState.setInfo(fileUpload.getFileType() + "成功!");
            onDeployProcessorListener.onDeployProcessSuccess(deployState);

            for (CommandMethodEnum commandMethodEnum : commandMethodEnumSet) {

                log.info(commandMethodEnum + " remove:" + CommandMethodEnum.valueOf(fileUpload.getFileType()));
            }

            commandMethodEnumSet.remove(CommandMethodEnum.valueOf(fileUpload.getFileType()));
            log.info(commandMethodEnumSet.size() + "");
            if (commandMethodEnumSet.size() == 0) {
                //文件传输成功,发送开始部署指令
                Command command = new Command();
                command.setCommandCode(CommandMethodEnum.DEPLOY_START.getCode());
                command.setCommandMethod(CommandMethodEnum.DEPLOY_START.toString());
                ctx.channel().writeAndFlush(command);
            }
        }
        if (fileUpload.getState() == FileUpload.FAIL) {
            //失败也将删除这个任务
            commandMethodEnumSet.remove(CommandMethodEnum.valueOf(fileUpload.getFileType()));
            DeployState deployState = new DeployState();
            deployState.setE(CommandMethodEnum.valueOf(fileUpload.getFileType()).getDesc() + "失败!" + "\r\n" + fileUpload.getDesc());
            onDeployProcessorListener.onDeployProcessFail(deployState);
        }
    }

    private void syncRuntimeChanger(Command command, ChannelHandlerContext ctx) {
        commandMethodEnumSet.add(CommandMethodEnum.getEnum(command.getCommandCode()));

        if (command.getContent()!=null && command.getContent() instanceof String){
            String content = command.getContent().toString();
            if (content.equals("ok")) {
                commandMethodEnumSet.remove(CommandMethodEnum.getEnum(command.getCommandCode()));
            }
            if (commandMethodEnumSet.size() == 0) {
                //文件传输成功,发送开始部署指令
                command.setCommandCode(CommandMethodEnum.DEPLOY_START.getCode());
                command.setCommandMethod(CommandMethodEnum.DEPLOY_START.toString());
                ctx.channel().writeAndFlush(command);
            }
            return;
        }

        Properties properties = new PropertyUtils(ApplicationConfig.getConfigListFilePath()).getOrderedProperties();

        Set<String> propertyNames = properties.stringPropertyNames();
        List<FileUpload> changedPropertiesFiles = new ArrayList<>();
        for (String propertyName : propertyNames) {
            //获取每个配置文件的路径信息
            String filePath = properties.getProperty(propertyName);
            File file = new File(filePath);
            File changedPropertiesFile = new File(ApplicationConfig.getApplicationConfPath() + file.getName() + ".Changed.properties");
            //运行时配置文件不存在则跳过
            if (!changedPropertiesFile.exists()) {
                continue;
            }

            FileUpload fileUpload = new FileUpload();
            fileUpload.setFileName(changedPropertiesFile.getName());
            fileUpload.setBytes(FileUtils.readFileToByte(changedPropertiesFile.getAbsolutePath()));

            changedPropertiesFiles.add(fileUpload);
        }

        //配置文件列表传送给agent
        File configListFile = new File(ApplicationConfig.getConfigListFilePath());
        if (configListFile.exists()) {
            FileUpload fileUpload = new FileUpload();
            fileUpload.setFileName(configListFile.getName());
            fileUpload.setBytes(FileUtils.readFileToByte(configListFile.getAbsolutePath()));
            changedPropertiesFiles.add(fileUpload);
        }

        command.setCommandMethod(CommandMethodEnum.SYNC_RUNTIME_CHANGER_CONFIG.toString());
        command.setCommandCode(CommandMethodEnum.SYNC_RUNTIME_CHANGER_CONFIG.getCode());
        command.setContent(changedPropertiesFiles);
        ctx.channel().writeAndFlush(command);

    }

    private void syncApacheConfig(Command command, ChannelHandlerContext ctx) {

        commandMethodEnumSet.add(CommandMethodEnum.getEnum(command.getCommandCode()));

        if (command.getContent()!=null && command.getContent() instanceof String){
            String content = command.getContent().toString();
            if (content.equals("ok")) {
                commandMethodEnumSet.remove(CommandMethodEnum.getEnum(command.getCommandCode()));
            }
            if (commandMethodEnumSet.size() == 0) {
                //文件传输成功,发送开始部署指令
                command.setCommandCode(CommandMethodEnum.DEPLOY_START.getCode());
                command.setCommandMethod(CommandMethodEnum.DEPLOY_START.toString());
                ctx.channel().writeAndFlush(command);
            }
            return;
        }
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
        super.sendMessage(command);
    }

    private void syncCMWar(Command command, ChannelHandlerContext ctx) {
        DeployConfigModel deployConfigModel = ApplicationContext.getDeployConfigModel();
        String warFlag = deployConfigModel.getCmDeployConfigMap().get("cmWarFlag");
        String cmWarPath = deployConfigModel.getCmDeployConfigMap().get("cmWarPath");
        //传送文件
        syncFile(ctx, warFlag, cmWarPath, command);
    }

    private void syncZYFLWar(Command command, ChannelHandlerContext ctx) {
        DeployConfigModel deployConfigModel = ApplicationContext.getDeployConfigModel();
        String warFlag = deployConfigModel.getZyflDeployConfigMap().get("zyflWarFlag");
        String cmWarPath = deployConfigModel.getZyflDeployConfigMap().get("zyflWarPath");
        //传送文件
        syncFile(ctx, warFlag, cmWarPath, command);
    }

    private void syncUploadWar(Command command, ChannelHandlerContext ctx) {
        DeployConfigModel deployConfigModel = ApplicationContext.getDeployConfigModel();
        String warFlag = deployConfigModel.getUploadDeployConfigMap().get("uploadWarFlag");
        String cmWarPath = deployConfigModel.getUploadDeployConfigMap().get("uploadWarPath");
        //传送文件
        syncFile(ctx, warFlag, cmWarPath, command);
    }

    private void syncFile(ChannelHandlerContext ctx, String warFlag, String warPath, Command command) {
        if (StringUtils.isEmpty(warFlag)) {
            DeployState deployState = new DeployState();
            deployState.setE(CommandMethodEnum.valueOf(command.getCommandMethod()).getDesc() + " 失败!请检查路径是否为空或者为null.");
            onDeployProcessorListener.onDeployProcessFail(deployState);
            return;
        }
        commandMethodEnumSet.add(CommandMethodEnum.getEnum(command.getCommandCode()));

        FileUpload fileUpload = createFileUpload(warPath, warFlag, command);

        if (fileUpload == null) {
            return;
        }

        File file = fileUpload.getFile();
        try {
            RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r");
            randomAccessFile.seek(fileUpload.getStarPos());
            byte[] bytes = new byte[1024 * 1024 * 10];//10mb
            int len;
            if ((len = randomAccessFile.read(bytes)) != -1) {
                fileUpload.setEndPos(len);
                fileUpload.setBytes(bytes);

                // fileUpload.setBytes(null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //写入
            ctx.channel().writeAndFlush(fileUpload);
        }
    }

    /**
     * 生成文件实体
     *
     * @param filePath 文件路径
     * @param warFlag  文件标记
     * @param command
     * @return 实体
     */
    private FileUpload createFileUpload(String filePath, String warFlag, Command command) {
        FileUpload fileUpload = new FileUpload();
        FileInputStream fis = null;
        try {
            File file = new File(filePath);
            fileUpload.setFile(file);
            fileUpload.setFileName(file.getName());
            fileUpload.setStarPos(0);
            fileUpload.setFileLength(file.length());
            fileUpload.setSuffixName(file.getName().substring(file.getName().lastIndexOf(".") + 1));
            fileUpload.setFileFlag(warFlag);
            fileUpload.setState(FileUpload.INIT);
            fileUpload.setFileType(command.getCommandMethod());
            fis = new FileInputStream(file);
            LocalTime now = LocalTime.now();
            String md5Hex = DigestUtils.md5Hex(fis);
            long millis = Duration.between(now, LocalTime.now()).toMillis();
            log.info(millis + "ms");
            fileUpload.setFile_md5(md5Hex);
        } catch (Exception e) {
            e.printStackTrace();
            StringWriter stringWriter = new StringWriter();
            e.printStackTrace(new PrintWriter(stringWriter));
            DeployState deployState = new DeployState();
            deployState.setE(CommandMethodEnum.valueOf(command.getCommandMethod()).getDesc() + "\r\n" + stringWriter.toString());
            onDeployProcessorListener.onDeployProcessFail(deployState);
            fileUpload = null;
        } finally {
            IOUtils.close(fis);
        }
        return fileUpload;
    }

}
