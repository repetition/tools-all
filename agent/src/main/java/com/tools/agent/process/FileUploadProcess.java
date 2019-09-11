package com.tools.agent.process;

import com.tools.socket.bean.Command;
import com.tools.socket.bean.FileUpload;
import io.netty.channel.ChannelHandlerContext;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class FileUploadProcess extends ProcessBase {

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
                fileUpload.setState(FileUpload.SUCCESS);
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
