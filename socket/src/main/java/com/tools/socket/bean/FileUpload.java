package com.tools.socket.bean;

import java.io.File;
import java.io.Serializable;

public class FileUpload implements Serializable {
    public static final int SUCCESS = 0;
    public static final int BREAKPOINT = 1;
    public static final int FAIL = 2;
    public static final int INIT = 3;


    private static final long serialVersionUID = 1L;
    private File file;// 文件
    private String file_md5;// 文件md5
    private String fileName;// 文件名
    private String fileType; //文件类型
    private String suffixName;//后缀名
    private String fileFlag;//文件标记
    private int state;//文件标记
    private String desc;//文件标记


    private long starPos;// 开始位置
    private byte[] bytes;// 文件字节数组
    private long endPos;// 结尾位置
    private long fileLength;

    public int getState() {
        return state;
    }

    public FileUpload setState(int state) {
        this.state = state;
        return this;
    }

    public String getDesc() {
        return desc;
    }

    public FileUpload setDesc(String desc) {
        this.desc = desc;
        return this;
    }

    public String getFileFlag() {
        return fileFlag;
    }

    public FileUpload setFileFlag(String fileFlag) {
        this.fileFlag = fileFlag;
        return this;
    }

    public String getSuffixName() {
        return suffixName;
    }

    public FileUpload setSuffixName(String suffixName) {
        this.suffixName = suffixName;
        return this;
    }

    public long getFileLength() {
        return fileLength;
    }

    public FileUpload setFileLength(long fileLength) {
        this.fileLength = fileLength;
        return this;
    }

    public String getFileType() {
        return fileType;
    }

    public FileUpload setFileType(String fileType) {
        this.fileType = fileType;
        return this;
    }

    public String getFileName() {
        return fileName;
    }

    public FileUpload setFileName(String fileName) {
        this.fileName = fileName;
        return this;
    }

    public File getFile() {
        return file;
    }

    public FileUpload setFile(File file) {
        this.file = file;
        return this;
    }

    public String getFile_md5() {
        return file_md5;
    }

    public FileUpload setFile_md5(String file_md5) {
        this.file_md5 = file_md5;
        return this;
    }

    public long getStarPos() {
        return starPos;
    }

    public FileUpload setStarPos(long starPos) {
        this.starPos = starPos;
        return this;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public FileUpload setBytes(byte[] bytes) {
        this.bytes = bytes;
        return this;
    }

    public long getEndPos() {
        return endPos;
    }

    public FileUpload setEndPos(long endPos) {
        this.endPos = endPos;
        return this;
    }
}
