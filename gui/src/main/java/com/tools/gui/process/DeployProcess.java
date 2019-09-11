package com.tools.gui.process;

import com.tools.commons.utils.IOUtils;
import com.tools.commons.utils.StringUtils;
import com.tools.service.context.ApplicationContext;
import com.tools.service.model.DeployConfigModel;
import com.tools.service.model.DeployState;
import com.tools.socket.bean.Command;
import com.tools.socket.bean.FileUpload;
import io.netty.channel.ChannelHandlerContext;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class DeployProcess extends ProcessBase {
    private static final Logger log = LoggerFactory.getLogger(DeployProcess.class);
    private List<CommandMethodEnum> commandMethodEnumList = new ArrayList<>();

    @Override
    protected void processCommand(Command command, ChannelHandlerContext ctx) {
        log.info(command.toString());
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
        }
    }

    @Override
    protected void processFileUpload(FileUpload fileUpload, ChannelHandlerContext ctx) {

        if (fileUpload.getState() == FileUpload.SUCCESS) {
            log.info("文件传输成功" + fileUpload.getFileName() + " , " + fileUpload.getFileType());
            DeployState deployState = new DeployState();
            deployState.setInfo(fileUpload.getFileType() + "成功!");
            onDeployProcessorListener.onDeployProcessSuccess(deployState);
            commandMethodEnumList.remove(CommandMethodEnum.valueOf(fileUpload.getFileType()));
            log.info(commandMethodEnumList.size() + "");
            if (commandMethodEnumList.size() == 0) {
                Command command = new Command();
                command.setCommandCode(CommandMethodEnum.DEPLOY_START.getCode());
                command.setCommandMethod(CommandMethodEnum.DEPLOY_START.toString());
                ctx.channel().writeAndFlush(command);
            }
        }
        if (fileUpload.getState() == FileUpload.FAIL) {
            DeployState deployState = new DeployState();
            deployState.setE(CommandMethodEnum.valueOf(fileUpload.getFileType()).getDesc() + "失败!");
            onDeployProcessorListener.onDeployProcessFail(deployState);
        }
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
        commandMethodEnumList.add(CommandMethodEnum.getEnum(command.getCommandCode()));
        FileUpload fileUpload = createFileUpload(warPath, warFlag, command);
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
            log.info(millis+"ms");
            fileUpload.setFile_md5(md5Hex);
        } catch (Exception e) {
            e.printStackTrace();
            StringWriter stringWriter = new StringWriter();
            e.printStackTrace(new PrintWriter(stringWriter));
            fileUpload.setState(FileUpload.FAIL).setFileType(command.getCommandMethod()).setDesc(stringWriter.toString());
        } finally {
            IOUtils.close(fis);
        }
        return fileUpload;
    }

}
