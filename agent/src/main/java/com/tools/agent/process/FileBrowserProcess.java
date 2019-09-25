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
import java.util.Comparator;
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
            List<String> suffixFilterList = fileItemInfo.getSuffixFilterList();
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
                    //文件夹不过滤
                    if (pathname.isDirectory()) {
                        return true;
                    }
                    //判断后缀过滤不能为空
                    if (null==suffixFilterList || suffixFilterList.size()==0) {
                        //没有后缀过滤器默认不过滤,返回所有类型文件
                        return true;
                    }

                    //获取文件名
                    String fileName = pathname.getName();

                    int lastIndexOf = fileName.lastIndexOf(".");
                    //文件如果没有格式,则过滤
                    if (lastIndexOf == -1) {
                        return false;
                    }
                    //截取后缀
                    String suffix = fileName.substring(lastIndexOf, fileName.length());
                    //和文件过滤器集合进行对比
                    if (suffixFilterList.contains(suffix)) {
                        return true;
                    }
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
        //增加排序,将文件夹排序在上方,文件在下方
        fileItemInfoList.sort(new Comparator<FileItemInfo>() {
            @Override
            public int compare(FileItemInfo fileItemInfo1, FileItemInfo fileItemInfo2) {

                if (fileItemInfo2.getIsDirectory()) {
                    return 1;
                }
                if (fileItemInfo2.getIsFile()){
                    return -1;
                }
                return 0;
            }
        });
        command.setContent(fileItemInfoList);
        //发送消息
        ctx.channel().writeAndFlush(command);
    }
}
