package com.tools.gui.utils.view;

import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class FileChooserUtils {

    /**
     * 选择文件
     *
     * @param filter      过滤器
     * @param defaultPtah 默认展示的路径
     * @param stage  容器
     * @return 返回选择文件的路径
     */
    public static String showSelectFileChooser(FileChooser.ExtensionFilter filter, String defaultPtah, Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("选择文件");
        //设置选择的文件过滤器
        //fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("war(*.war)", "*.war"));
        fileChooser.getExtensionFilters().addAll(filter);
        //设置默认显示的文件目录
        // fileChooser.setInitialDirectory(new File("F:\\QQdownload"));
        File defaultFile = new File(defaultPtah);
        if (defaultFile.exists()) {
            //设置默认显示的目录
            fileChooser.setInitialDirectory(defaultFile);
        }
        //获取选择的文件
        File selectFile = fileChooser.showOpenDialog(stage);

        return null == selectFile ? "" : selectFile.getAbsoluteFile().toString();
    }

    /**
     * 选择目录
     *
     * @param filter      过滤器
     * @param defaultPtah 默认展示的路径
     * @return 返回选择文件的路径
     */
    public static String showSelectDirectoryChooser(FileChooser.ExtensionFilter filter, String defaultPtah, Stage stage) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("选择目录");
        //设置选择的文件过滤器
        // fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("war(*.war)", "*.war"));
        //设置默认显示的文件目录
        //  directoryChooser.setInitialDirectory(new File("F:\\"));
        if (!new File(defaultPtah).exists()) {
            //文件不存在就添加默认目录
            defaultPtah = "d://";
        }
        directoryChooser.setInitialDirectory(new File(defaultPtah));
        //获取选择的文件
        File selectDirectory = directoryChooser.showDialog(stage);
        return null == selectDirectory ? "" : selectDirectory.getAbsoluteFile().toString();
    }
}
