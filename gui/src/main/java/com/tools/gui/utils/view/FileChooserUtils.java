package com.tools.gui.utils.view;

import com.tools.gui.controller.RemoteFileBrowserController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.*;

import java.io.File;
import java.io.IOException;

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


    public static void showRemoteFileBrowserWindow(Stage parentStage,RemoteFileBrowserController.FileFilter fileFilter, RemoteFileBrowserController.OnFileSelectorCallBack onFileSelectorCallBack) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(FileChooserUtils.class.getClass().getResource("/fxml/window_remote_file_browser.fxml"));
            Parent parent = fxmlLoader.load();
            Scene scene = new Scene(parent);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("远程文件浏览器");

            RemoteFileBrowserController controller = fxmlLoader.getController();
            controller.setStage(stage);
            controller.setFileFilter(fileFilter);
            controller.setOnFileSelectorCallBack(onFileSelectorCallBack);
            //设置图标
            stage.getIcons().addAll(new Image(FileChooserUtils.class.getClass().getResource("/image/icons8_logo.png").toString()));
            stage.initStyle(StageStyle.DECORATED);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(parentStage);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
