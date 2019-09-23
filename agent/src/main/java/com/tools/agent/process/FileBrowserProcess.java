package com.tools.agent.process;

import com.tools.socket.bean.Command;
import com.tools.socket.bean.FileItemInfo;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class FileBrowserProcess extends ProcessBase {
    private static final Logger log = LoggerFactory.getLogger(FileBrowserProcess.class);

    @Override
    protected void processCommand(Command command, ChannelHandlerContext ctx) {

        int commandCode = command.getCommandCode();
        CommandMethodEnum commandMethodEnum = CommandMethodEnum.getEnum(commandCode);
        switch (commandMethodEnum) {
            case GET_FILE_DIRECTORY:

                fileBrowser(command, ctx);

                break;
        }
    }

    private void fileBrowser(Command command, ChannelHandlerContext ctx) {
        List<FileItemInfo> fileItemInfoList = new ArrayList<>();
        FileItemInfo fileItemInfo = (FileItemInfo) command.getContent();

        if (fileItemInfo.getNodeType().equals(FileItemInfo.ROOT)) {
            //第一级用计算机名作为顶级阶段
            FileItemInfo rootItem;
            rootItem = new FileItemInfo();
            rootItem.setNodeType(FileItemInfo.ROOT);
            String hostName = null;
            try {
                hostName = InetAddress.getLocalHost().getHostName();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
            rootItem.setFileName(hostName);
            fileItemInfoList.add(rootItem);

            //获取最顶级的计算机目录 作为第二节点 必须传数据
            Iterable<Path> rootDirectories = FileSystems.getDefault().getRootDirectories();
            List<FileItemInfo> childList = new ArrayList<>();
            for (Path directory : rootDirectories) {
                FileItemInfo rootDirectorItem = new FileItemInfo();
                rootDirectorItem.setNodeType(FileItemInfo.CHILD);
                rootDirectorItem.setIsDirectory(true);
                rootDirectorItem.setIsFile(false);
                rootDirectorItem.setAbsolutePath(directory.toAbsolutePath().toString());
                rootDirectorItem.setFileName(directory.toString());
                childList.add(rootDirectorItem);
            }
            rootItem.setFileChilds(childList);
        }
        //获取子目录
        if (fileItemInfo.getNodeType().equals(FileItemInfo.CHILD)) {
            String filter = fileItemInfo.getFilter();
            String absolutePath = fileItemInfo.getAbsolutePath();
            File file = new File(absolutePath);
            File[] listFiles = file.listFiles(pathname -> {

                //过滤隐藏文件
                if (pathname.isHidden()) {
                    return false;
                }
                if (filter.equals(FileItemInfo.FILTER_DIRECTORY_ONLY)) {
                    if (pathname.isDirectory()) {
                        return true;
                    }
                }

                if (filter.equals(FileItemInfo.FILTER_ALL)) {
                    return true;
                }

                return false;
            });

            if (null != listFiles) {
                for (File listFile : listFiles) {
                    FileItemInfo childItem = new FileItemInfo();
                    childItem.setNodeType(FileItemInfo.CHILD);
                    childItem.setIsDirectory(listFile.isDirectory());
                    childItem.setIsFile(listFile.isFile());
                    childItem.setAbsolutePath(listFile.getAbsolutePath());
                    childItem.setFileName(listFile.getName());
                    fileItemInfoList.add(childItem);
                }
            }
        }
        command.setContent(fileItemInfoList);
        //发送消息
        ctx.channel().writeAndFlush(command);
    }
}
