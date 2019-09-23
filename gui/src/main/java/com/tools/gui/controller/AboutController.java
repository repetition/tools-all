package com.tools.gui.controller;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DecimalFormat;
import java.util.Timer;
import java.util.TimerTask;


public class AboutController extends BaseController{
    private static final Logger log = LoggerFactory.getLogger(AboutController.class);
    public Stage mStage;
    public Label mLBMaxMemory;
    public Label mLBFreeMemory;
    public Label mLBTotalMemory;
    public Label mLBAvailableProcessors;
    public Label mLBDes;
    public Timer timer;
    public Label mLBVersion;


    @FXML
    public void initialize() {
        DecimalFormat df = new DecimalFormat("#.0");
        double maxMemory = Runtime.getRuntime().maxMemory() / 1024.00 / 1024.00;
        double freeMemory = Runtime.getRuntime().freeMemory() / 1024.00 / 1024.00;
        double totalMemory = Runtime.getRuntime().totalMemory() / 1024.00 / 1024.00;
        double availableProcessors = Runtime.getRuntime().availableProcessors();

        mLBMaxMemory.setText(df.format(maxMemory) + "MB");
        mLBFreeMemory.setText(df.format(freeMemory) + "MB");
        mLBTotalMemory.setText(df.format(totalMemory) + "MB");
        mLBAvailableProcessors.setText(availableProcessors + "");

        mLBDes.setText("使用javaFX开发，ROOT包一键部署启动服务工具。");
        mLBDes.setWrapText(true);
        mLBVersion.setText("2.0 (部署模式自选择版)");

        timer = new Timer();

        timer.schedule(new TimerTask() {
            @Override
            public void run() {

                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {

                        double maxMemory = Runtime.getRuntime().maxMemory() / 1024.00 / 1024.00;
                        double freeMemory = Runtime.getRuntime().freeMemory() / 1024.00 / 1024.00;
                        double totalMemory = Runtime.getRuntime().totalMemory() / 1024.00 / 1024.00;
                        double availableProcessors = Runtime.getRuntime().availableProcessors();

                        mLBMaxMemory.setText(df.format(maxMemory) + "MB");
                        mLBFreeMemory.setText(df.format(freeMemory) + "MB");
                        mLBTotalMemory.setText(df.format(totalMemory) + "MB");
                        mLBAvailableProcessors.setText(availableProcessors + "");

/*                        //重新获取焦点
                        if (!mStage.isFocused()) {
                            mStage.requestFocus();
                        }*/
log.info("定时器执行中..."+Thread.currentThread().getId());
                    }
                });
            }
        }, 500L, 1000L);
    }

    public void setStage(Stage anotherStage) {
        this.mStage = anotherStage;
        System.out.println(mStage + "2");
        //放到setStage里面，不会出现空指针。initialize方法执行比setStage早
        mStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                timer.cancel();
                log.info("定时器退出!");
            }
        });
    }
}
