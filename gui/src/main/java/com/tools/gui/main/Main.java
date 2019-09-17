package com.tools.gui.main;

import com.tools.gui.config.Config;
import com.tools.gui.controller.MainController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import static com.tools.gui.config.Config.debugConfigPath;

public class Main extends Application {
    private static final Logger log = LoggerFactory.getLogger(Main.class);
    public  static Stage mainStage;

    private static boolean isAddTray = false;

    @Override
    public void start(Stage primaryStage) throws Exception {
        mainStage = primaryStage;
        //隐士关闭，解决方式双击托盘不执行
        Platform.setImplicitExit(false);

        // SwingUtilities.invokeLater(this::addTray);
        //添加系统托盘
        if (!isAddTray) {
            addTray();
        }
        //TODO 需要查找fxml路径
        // FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("fxml/main_view.fxml"));
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/main_view.fxml"));
        Parent root = fxmlLoader.load();
        primaryStage.getIcons().addAll(new Image(this.getClass().getResource("/image/icons8_logo.png").toString()));
        primaryStage.setTitle("Tools");
        Scene scene = new Scene(root, 800, 610);
        ObservableList<String> stylesheets = scene.getStylesheets();
        stylesheets.addAll(getClass().getResource("/css/tools-css.css").toExternalForm());
      //  stylesheets.add(Main.class.getResource("/jmetro8/JMetroLightTheme.css").toExternalForm());
        primaryStage.setScene(scene);
    //    primaryStage.initModality(Modality.WINDOW_MODAL);
      //  primaryStage.initStyle(StageStyle.UTILITY);
        //最小化到任务栏
        //primaryStage.setIconified(true);
        primaryStage.setResizable(false);
        VBox vbox = (VBox) root.lookup("#mVBox");
        VBox anchorPaneRoot = (VBox) root.lookup("#anchorPaneRoot");
        javafx.scene.control.MenuBar menuBar = (javafx.scene.control.MenuBar) root.lookup("#menuBar");
        FlowPane mFlowPaneCfgRoot = (FlowPane) root.lookup("#mFlowPaneCfgRoot");
        GridPane gridPaneRoot = (GridPane) root.lookup("#gridPaneRoot");
        log.info(anchorPaneRoot.getHeight()+"");
        primaryStage.show();
        MainController mainController = fxmlLoader.getController();
        mainController.setStage(primaryStage);
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                //不退出应用，隐藏
                event.consume();
                primaryStage.close();
            }
        });
        log.info(anchorPaneRoot.getHeight()+"");
        log.info(menuBar.getHeight()+"");
        log.info("mFlowPaneCfgRoot.getHeight():"+mFlowPaneCfgRoot.getHeight());
        log.info(primaryStage.getHeight()+"");
        primaryStage.setHeight(580+mFlowPaneCfgRoot.getHeight());
        vbox.setPrefHeight(vbox.getHeight()+mFlowPaneCfgRoot.getHeight());
        gridPaneRoot.setPrefHeight(gridPaneRoot.getHeight()+mFlowPaneCfgRoot.getHeight());
        //  primaryStage.getScene().getWindow().setHeight(anchorPaneRoot.getHeight()+menuBar.getHeight());
        log.info("程序启动...");
    }

    /**
     * 添加系统托盘
     */
    private void addTray() {
        if (SystemTray.isSupported()) {
            //系统托盘
            SystemTray systemTray = SystemTray.getSystemTray();
            URL url = getClass().getResource("/image/icons8_logo.png");
            log.info("image_path: "+url);
            try {
                BufferedImage image = ImageIO.read(url);
                TrayIcon trayIcon = new TrayIcon(image);
                trayIcon.setImageAutoSize(true);
                //设置提示信息
                trayIcon.setToolTip("一键部署工具");
                //添加托盘图标
                systemTray.add(trayIcon);
                //添加成功后的提示
                trayIcon.displayMessage("提示", "一键部署工具启动成功", TrayIcon.MessageType.INFO);

                trayIcon.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                open();
                            }
                        });
                    }
                });
                //添加菜单
                PopupMenu popupMenu = new PopupMenu();
                MenuItem closeItem = new MenuItem("退出");
                MenuItem showItem = new MenuItem("显示");

                popupMenu.add(showItem);
                popupMenu.add(closeItem);

                showItem.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        Platform.runLater(() -> open());
                    }
                });

                closeItem.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        log.info("程序退出！");
                        System.exit(0);
                    }
                });
                trayIcon.setPopupMenu(popupMenu);
                isAddTray = true;
            } catch (IOException e) {
                e.printStackTrace();
                isAddTray =false;
            } catch (AWTException e) {
                e.printStackTrace();
                isAddTray =false;
            }
        }

    }

    /**
     * 窗口显示隐藏
     */
    private void open() {

        if (mainStage.isShowing()) {
            //是否在任务栏
            if (mainStage.isIconified()) {
                mainStage.setIconified(false);
            } else {
                mainStage.toFront();
            }
        } else {
            mainStage.show();
            mainStage.toFront();
        }
    }

    public static void main(String[] args) {
        // TODO: 2019/8/20 制作成exe后,使用计划任务启动时,user.dir路径不准确需要改成 install4j.exeDir
        String rootPath = System.getProperty("user.dir");
       // String rootPath = System.getProperty("install4j.exeDir");


/*        System.setProperty("dir.base", rootPath);
        System.out.println("log.path:"+System.getProperty("dir.base") + "/conf/log4j.properties");
        System.out.println("dir.base: "+System.getProperty("dir.base"));*/


        for (String name : System.getProperties().stringPropertyNames()) {

            System.out.println(name+ " : "+ System.getProperty(name));
        }

        if (Config.isDebug) {
            /*------------------------------------------------*/
            //调试状态下程序路径
            System.setProperty("dir.base", debugConfigPath);
            //调试状态下配置文件路径
            System.setProperty("conf.path", System.getProperty("dir.base") + "/conf/");

            System.setProperty("HaoZip.path", System.getProperty("dir.base") + "/HaoZipC");
            /*------------------------------------------------*/
        }else {

            System.setProperty("dir.base", rootPath);
            //解压工具路径
            System.setProperty("HaoZip.path", System.getProperty("dir.base") + "/HaoZipC");
            //配置文件路径
            System.setProperty("conf.path", System.getProperty("dir.base") + "/conf/");
        }


        /*------------------------------------------------*/

        //HaoZip.path
        System.err.println("HaoZip.path: "+System.getProperty("HaoZip.path"));
        //程序工作目录
        System.err.println("dir.base: "+System.getProperty("dir.base"));

        System.err.println("conf.path: "+System.getProperty("conf.path"));


        // TODO: 2018/8/14  log4j日志输入存在异常

        PropertyConfigurator.configure(System.getProperty("conf.path") + "/log4j.properties");

 /*       //动态加载配置文件
        if (Config.isDebug) {
          //  System.setProperty("dir.base", new File(debugConfigPath).getParentFile().getAbsolutePath());
            PropertyConfigurator.configure(System.getProperty("conf.path") + "/log4j.properties");
        } else {
            //System.setProperty("dir.base", rootPath);
            PropertyConfigurator.configure(System.getProperty("conf.path") + "/log4j.properties");
        }*/


        launch(args);

    }

}
