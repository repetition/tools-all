package com.tools.socket.bean;

import java.io.Serializable;
import java.util.List;

public class FileItemInfo implements Serializable {

    private static final long serialVersionUID = 10L;
    public static final String ROOT = "root";
    public static final String CHILD = "child";

    public static final String FILTER_DIRECTORY_ONLY = "directory";
    public static final String FILTER_ALL = "all";


    //文件名字
    private String fileName;
    //是否是文件夹
    private boolean isDirectory;
    //是否是文件
    private boolean isFile;
    //绝对路径
    private String absolutePath;
    /**
     * 节点类型
     */
    private String nodeType;
    /**
     * 文件过滤器
     */
    private String filter;
    /**
     * 文件后缀名过滤器
     */
    private List<String> suffixFilterList;

    public List<String> getSuffixFilterList() {
        return suffixFilterList;
    }

    public FileItemInfo setSuffixFilterList(List<String> suffixFilterList) {
        this.suffixFilterList = suffixFilterList;
        return this;
    }

    public String getFilter() {
        return filter;
    }

    public FileItemInfo setFilter(String filter) {
        this.filter = filter;
        return this;
    }

    private List<FileItemInfo> fileChilds;

    public String getNodeType() {
        return nodeType;
    }

    public FileItemInfo setNodeType(String nodeType) {
        this.nodeType = nodeType;
        return this;
    }

    public List<FileItemInfo> getFileChilds() {
        return fileChilds;
    }

    public FileItemInfo setFileChilds(List<FileItemInfo> fileChilds) {
        this.fileChilds = fileChilds;
        return this;
    }

    public String getFileName() {
        return fileName;
    }

    public FileItemInfo setFileName(String fileName) {
        this.fileName = fileName;
        return this;
    }

    public boolean getIsDirectory() {
        return isDirectory;
    }

    public FileItemInfo setIsDirectory(boolean isDirectory) {
        this.isDirectory = isDirectory;
        return this;
    }

    public boolean getIsFile() {
        return isFile;
    }

    public FileItemInfo setIsFile(boolean isFile) {
        this.isFile = isFile;
        return this;
    }

    public String getAbsolutePath() {
        return absolutePath;
    }

    public FileItemInfo setAbsolutePath(String absolutePath) {
        this.absolutePath = absolutePath;
        return this;
    }
}
