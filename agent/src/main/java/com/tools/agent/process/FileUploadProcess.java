package com.tools.agent.process;

import com.tools.service.context.ApplicationContext;
import com.tools.service.model.DeployConfigModel;
import com.tools.socket.bean.Command;
import com.tools.socket.bean.FileUpload;
import io.netty.channel.ChannelHandlerContext;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

public class FileUploadProcess extends ProcessBase {
    private static final Logger log = LoggerFactory.getLogger(FileUploadProcess.class);

    @Override
    public void processFileUpload(FileUpload fileUpload, ChannelHandlerContext ctx) {
        downloadFile(fileUpload, ctx);
    }

    private void downloadFile(FileUpload fileUpload, ChannelHandlerContext ctx) {

        int state = fileUpload.getState();

        switch (state) {
            case FileUpload.BREAKPOINT:
                upload(fileUpload, ctx);
                break;
            case FileUpload.FAIL:
                Command command = new Command();
                command.setContent(fileUpload.getFileName()+"传输失败");
                command.setCommandCode(CommandMethodEnum.valueOf(fileUpload.getFileType()).getCode());
                command.setCommandMethod(fileUpload.getFileType());
                ctx.channel().writeAndFlush(command);
                break;
            case FileUpload.INIT:
                upload(fileUpload, ctx);
                break;
        }
    }

    private void upload(FileUpload fileUpload, ChannelHandlerContext ctx) {
        File file = new File(System.getProperty("user.home") + "\\Downloads\\" + fileUpload.getFileFlag() + "." + fileUpload.getSuffixName());
        RandomAccessFile randomAccessFile = null;
        try {
            if (!file.exists()) {
                file.createNewFile();
                fileUpload.setAgentFile(file);
            }
            randomAccessFile = new RandomAccessFile(file, "rw");
            //移动到指定位置
            randomAccessFile.seek(fileUpload.getStarPos());
            //将流写入到文件
            randomAccessFile.write(fileUpload.getBytes());
            //更新当前写入的位置
            fileUpload.setStarPos(randomAccessFile.length());
            fileUpload.setBytes(null);
            //判断文件是否传输完毕
            if (randomAccessFile.length() == fileUpload.getFileLength()) {
                FileInputStream fileInputStream = new FileInputStream(file);
                String md5Hex = DigestUtils.md5Hex(fileInputStream);
                fileInputStream.close();
                log.info(fileUpload.getFileName() + " md5 : " + fileUpload.getFile_md5() + "  , downloadFile md5 :" + md5Hex);
                if (fileUpload.getFile_md5().equals(md5Hex)) {
                    fileUpload.setState(FileUpload.SUCCESS);
                }else {
                    fileUpload.setDesc(fileUpload.getFile_md5() +" : " +md5Hex + " , " +fileUpload.getFileName() +" 文件校验不通过!");
                    fileUpload.setState(FileUpload.FAIL);
                }

            } else {
                //没有传输完毕,就断点续传
                fileUpload.setState(FileUpload.BREAKPOINT);
            }
            randomAccessFile.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            fileUpload.setState(FileUpload.FAIL);
        } catch (IOException e) {
            e.printStackTrace();
            fileUpload.setState(FileUpload.FAIL);
        } finally {
            fileUpload.setBytes(null);
            //写入
            ctx.channel().writeAndFlush(fileUpload);
        }
    }
}
