package com.tools.gui.controller;

import com.tools.commons.thread.ThreadPoolManager;
import com.tools.commons.utils.LanguageFormatUtils;
import com.tools.gui.config.Config;
import com.tools.gui.jenkins.JenkinsAutoBuild;
import com.tools.gui.jenkins.ProjectBuild;
import com.tools.gui.jenkins.bean.ModuleInfo;
import com.tools.gui.utils.view.NotificationsBuild;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Predicate;

public class DownloadInfoController extends BaseController{
    private static final Logger log = LoggerFactory.getLogger(DownloadInfoController.class);
    public ProgressIndicator mPBDownload;
    public Button mBTDownload;
    public ProgressBar mPBDownload1;
    public ListView mListView;

    public Label mLBDownloadPath;
    public Label mLBDownloadTime;
    public Label mLBCurrentSelector;
    public AnchorPane root;
    public TextField mTFSearch;

    private Task downloadTask;
    private String currentSelectorProject;
    private Stage stage;

    private List<String> searchList;

    public void onAction(ActionEvent event) {
        if (event.getSource() == mBTDownload) {
            mBTDownload.setText("下载中...");
            mBTDownload.setDisable(true);
/*        CmdUtils.executor.execute(new Runnable() {
                @Override
                public void run() {
                    ModuleInfo moduleInfo = new ModuleInfo();
                    moduleInfo.setProjectName("中油瑞飞");
                    moduleInfo.setProjectModuleName("-thinkwin-cr");
                    ProjectBuild.config(moduleInfo);
                    JenkinsAutoBuild.downloadStart();
                }
            });*/
     /*       downloadTask.cancel(true);
            mPBDownload.progressProperty().unbind();
            mPBDownload1.progressProperty().unbind();*/
            downloadTask = createTask();
            mPBDownload.progressProperty().bind(downloadTask.progressProperty());
            //  mPBDownload1.progressProperty().bind(downloadTask.progressProperty());
            ThreadPoolManager.getInstance().execute(downloadTask);


/*
            new Thread(){

                @Override
                public void run() {
                    super.run();

                    for (int i = 1; i <= 100; i++) {
                        double progress = (i*1.0) / (100 * 1.00);
                        log.info("" + progress);
                        try {
                            Thread.sleep(500L);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                mPBDownload.setProgress(progress);
                            }
                        });
                    }
                }
            }.start();*/

        }
    }

    public static void main(String[] args) {
        for (int i = 1; i <= 100; i++) {

            double progress = (i * 1.0) / (100 * 1.00);
            log.info("" + progress);
            //  mPBDownload.setProgress(progress);
        }
    }


    public Task createTask() {

        downloadTask = new Task() {
            @Override
            protected Object call() throws Exception {
                JenkinsAutoBuild.setOnDownloadListener(new JenkinsAutoBuild.OnDownloadListener() {
                    @Override
                    public void onDownloadSuccess(int state, String path) {

                        downloadTask.cancel(true);
                        mPBDownload.progressProperty().unbind();
                        // mPBDownload1.progressProperty().unbind();

                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                mBTDownload.setText("下载");

                                mBTDownload.setDisable(false);
                                NotificationsBuild.showBottomRightNotification("下载成功", "ROOT.war下载成功", 10.0);
                                mLBDownloadPath.setVisible(true);
                                mLBDownloadPath.setText("下载路径：" + Config.downloadFilePath);
                                String format = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss").format(new Date());
                                mLBDownloadTime.setText("下载时间：" + format);
                                mLBDownloadTime.setVisible(true);
                                mPBDownload.setVisible(false);
                            }
                        });
                    }

                    @Override
                    public void onDownloadFail(int state, String path) {
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                mBTDownload.setText("下载");
                                mBTDownload.setDisable(false);
                                NotificationsBuild.showBottomRightNotification("下载失败", "ROOT.war下载失败", 10.0);
                                mPBDownload.setVisible(true);
                            }
                        });
                    }

                    @Override
                    public void onDownloadProgress(int state, long progress, long totalFileSize) {
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                mPBDownload.setDisable(false);
                                mPBDownload.setVisible(true);

                              /*  VBox vBox = new VBox();
                                vBox.setAlignment(Pos.CENTER);
                                vBox.getChildren().add(mPBDownload);
                                stage.*/

                            }
                        });

                 /*           Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    synchronized (DownloadInfoController.class) {
                                        DecimalFormat format = new DecimalFormat("0.00");
                                        double progresss = Double.valueOf(progress) / Double.valueOf(totalFileSize);
                                        String prog = format.format(progresss);
                                        log.info("下载进度：" + prog);
                                        mPBDownload.setProgress(Double.valueOf(prog));
                                        mPBDownload1.setProgress(Double.valueOf(prog));
                                    }
                                }
                            });*/
                        DecimalFormat format = new DecimalFormat("0.00");
                        double progresss = Double.valueOf(progress) / Double.valueOf(totalFileSize);
                        String prog = format.format(progresss);
                        //  log.info("下载进度：" + prog);
                        updateMessage("下载中...");
                        updateProgress(Double.valueOf(prog), 1);
                    }
                });


                if (null != currentSelectorProject) {
                    ModuleInfo moduleInfo = new ModuleInfo();
                    if (currentSelectorProject.contains("-thinkwin-cr")) {
                        String name = currentSelectorProject.split("-thinkwin-cr")[0];
                        moduleInfo.setProjectName(name);
                        moduleInfo.setProjectModuleName("-thinkwin-cr");
                    } else {
                        moduleInfo.setProjectName(currentSelectorProject);
                        moduleInfo.setProjectModuleName("");
                    }
                    ProjectBuild.config(moduleInfo);
                    // TODO: 2019/1/31 还没有处理下载时的jenkins cookie 年后回来处理
                    JenkinsAutoBuild.downloadStart();
                } else {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            NotificationsBuild.showBottomRightNotification("错误", "请选择一个项目", 10);
                            mBTDownload.setText("下载");
                            mBTDownload.setDisable(false);
                        }
                    });
                }
                return true;
            }
        };
        return downloadTask;
    }

    @FXML
    public void initialize() {


        List<String> strings = JenkinsAutoBuild.searchProject("download");
        ObservableList<String> stringObservableList = FXCollections.observableArrayList(strings);
        mListView.setItems(stringObservableList);
        //listView 点击事件
        mListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
//                log.info("selector：" + newValue);
                currentSelectorProject = (String) newValue;
                mLBCurrentSelector.setVisible(true);
                mLBCurrentSelector.setText("当前选择：" + newValue);
            }
        });
        FilteredList<String> filteredList = new FilteredList<>(stringObservableList, new Predicate<String>() {
            @Override
            public boolean test(String s) {
                return true;
            }
        });
        searchList = new ArrayList<>();
        mTFSearch.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                log.info("search：" + newValue);

                //   searchListForJava(newValue, strings);

                //searchListForJavaFX  javaFx原生api实现
                filteredList.setPredicate(new Predicate<String>() {
                    @Override
                    public boolean test(String s) {
                        if (newValue == null || newValue.isEmpty()) {
                            return true;
                        }
                        if (LanguageFormatUtils.toHanyuPinyin(s).contains(LanguageFormatUtils.toHanyuPinyin(newValue))) {
                            return true; // Filter matches
                        }
                        return false; // Does not match
                    }
                });
                mListView.setItems(filteredList);
            }

        });





 /*       JenkinsAutoBuild.setOnDownloadListener(new JenkinsAutoBuild.OnDownloadListener() {
            @Override
            public void onDownloadSuccess(int state, String path) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        mBTDownload.setText("下载");
                        NotificationsBuild.showBottomRightNotification("下载成功", "ROOT.war下载成功", 10.0);
                    }
                });
            }

            @Override
            public void onDownloadFail(int state, String path) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        mBTDownload.setText("下载");
                        NotificationsBuild.showBottomRightNotification("下载失败", "ROOT.war下载失败", 10.0);
                    }
                });
            }

            @Override
            public void onDownloadProgress(int state, long progress, long totalFileSize) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        synchronized (DownloadInfoController.class) {
                            DecimalFormat format = new DecimalFormat("0.00");
                            double progresss = Double.valueOf(progress) / Double.valueOf(totalFileSize);
                            String prog = format.format(progresss);
                            log.info("下载进度：" + prog);
                            mPBDownload.setProgress(Double.valueOf(prog));
                            mPBDownload1.setProgress(Double.valueOf(prog));
                        }
                    }
                });
            }
        });*/

        /*           Platform.runLater(new Runnable() {
                       @Override
                       public void run() {
                           synchronized (DownloadInfoController.class) {
                               DecimalFormat format = new DecimalFormat("0.00");
                               double progresss = Double.valueOf(progress) / Double.valueOf(totalFileSize);
                               String prog = format.format(progresss);
                               log.info("下载进度：" + prog);
                               mPBDownload.setProgress(Double.valueOf(prog));
                               mPBDownload1.setProgress(Double.valueOf(prog));
                           }
                       }
                   });*/
/*        downloadTask = new Task() {
            @Override
            protected Object call() {


                return true;
            }
        };
        mPBDownload.progressProperty().unbind();
        mPBDownload1.progressProperty().unbind();
        mPBDownload.progressProperty().bind(downloadTask.progressProperty());
        mPBDownload1.progressProperty().bind(downloadTask.progressProperty());*/
    }

    /**
     * 使用java原生api实现listView搜索功能
     *
     * @param newValue
     * @param strings
     */
    public void searchListForJava(String newValue, List<String> strings) {
        if (newValue.equals("")) {
            mListView.getItems().removeAll(FXCollections.observableArrayList(searchList));
            mListView.setItems(FXCollections.observableArrayList(strings));
        }
        for (String s : strings) {
            if (s.toLowerCase().contains(newValue.toLowerCase())) {
                if (!searchList.contains(s)) {
                    searchList.add(s);
                }
            } else {
                //当列表存在 则去删除
                if (searchList.contains(s)) {
                    searchList.remove(s);
                }
            }
            mListView.getItems().setAll(FXCollections.observableArrayList(searchList));
        }
    }


    public void setStage(Stage stage) {
        this.stage = stage;
    }

}
