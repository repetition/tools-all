package com.tools.gui.process;

import com.tools.socket.bean.FileUpload;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.DecimalFormat;

public class FileUploadProcess extends ProcessBase {
    private static final Logger log = LoggerFactory.getLogger(FileUploadProcess.class);

    @Override
    public void processFileUpload(FileUpload fileUpload, ChannelHandlerContext ctx) {
        //进度显示
/*        DecimalFormat df = new DecimalFormat("#.##");
        double v = (double) (fileUpload.getStarPos()) / (double) (fileUpload.getFileLength());
        v = v * 100;
        String format = df.format(v);
        log.info(( format +"%"));*/
        if (fileUpload.getState() == FileUpload.BREAKPOINT) {
            long byteSize = 1024 * 1024 * 10;
            if ((fileUpload.getFileLength() - fileUpload.getStarPos()) < byteSize) {
                byteSize = fileUpload.getFileLength() - fileUpload.getStarPos();
            }
            File file = fileUpload.getFile();
            RandomAccessFile randomAccessFile = null;
            try {
                randomAccessFile = new RandomAccessFile(file, "r");
                randomAccessFile.seek(fileUpload.getStarPos());

                byte[] bytes = new byte[Math.toIntExact(byteSize)];
                int len;
                if ((len = randomAccessFile.read(bytes)) != -1) {
                    fileUpload.setEndPos(len + fileUpload.getStarPos());
                    fileUpload.setStarPos(fileUpload.getStarPos());
                    fileUpload.setBytes(bytes);

                    log.info("正在进行断点续传.. :startPos :" + fileUpload.getStarPos() + " len:" + len + " byte.len:" +bytes.length);
                    //写入
                    ctx.channel().writeAndFlush(fileUpload);
                    randomAccessFile.close();
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (fileUpload.getState() == FileUpload.SUCCESS) {
         //   log.info("文件传输成功");
        }
    }
}
