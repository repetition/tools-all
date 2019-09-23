package com.tools.gui.controller;

import com.tools.commons.thread.ThreadPoolManager;
import com.tools.commons.utils.PropertyUtils;
import com.tools.commons.utils.Utils;
import com.tools.gui.jenkins.JenkinsAutoBuild;
import com.tools.gui.jenkins.bean.ModuleInfo;
import com.tools.gui.utils.view.NotificationsBuild;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class JenkinsBuildController extends BaseController{
    private static final Logger log = LoggerFactory.getLogger(JenkinsBuildController.class);
    //构建列表
    public ListView mTVBuildList;
    //一键构建按钮
    public Button mBTBuildStart;
    //构建的总数
    public Label mLACount;
    //已经构建的总数
    public Label mLABuildingNum;
    //正在构建的模块名字
    public Label mLABuildingModuleName;
    //分支名字输入
    public TextField mTFBranch;
    //进度条
    public ProgressBar mPBBuildProgress;
    public File mModuleFile = null;
    public Button mBTModuleSelect;
    public Label mLAProgress;

    private Stage mStage;
    private ObservableList<Object> arrayList;
    private PropertyUtils propertyUtils;


    public void onBuildAction(ActionEvent actionEvent) {

        if (actionEvent.getSource() == mBTBuildStart) {
            mBTBuildStart.setText("正在构建...");
            mBTBuildStart.setDisable(true);
            ThreadPoolManager.getInstance().execute(new Runnable() {
                @Override
                public void run() {
                    JenkinsAutoBuild.moduleBuildStart();
                }
            });

        }
    }

    public void onBatchBuildAction(ActionEvent actionEvent) {
        JenkinsAutoBuild.batchBuildStart();
    }


    public void onSelectAction(ActionEvent actionEvent) {

        if (actionEvent.getSource() == mBTModuleSelect) {

            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/ModuleListWindow.fxml"));
            Parent moduleListParent = null;
            try {
                moduleListParent = fxmlLoader.load();
                Scene moduleListScene = new Scene(moduleListParent);
                Stage moduleListStage = new Stage();
                moduleListStage.setTitle("模块列表");
                moduleListStage.setScene(moduleListScene);
                moduleListStage.initStyle(StageStyle.UTILITY);
                moduleListStage.initModality(Modality.WINDOW_MODAL);

                //anotherStage.centerOnScreen();
                moduleListStage.initOwner(mStage);
                ModuleListController controller = fxmlLoader.getController();
                controller.setStage(moduleListStage);
                controller.setController(this);
                moduleListStage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void setSelect(String currentSelectorProject) {
        if (null == currentSelectorProject || currentSelectorProject.equals("")) {
            return;
        }
        // Properties properties = PropertyUtils.getOrderedProperties("JenkinsProperties.properties");
       // Properties properties = PropertyUtils.getProperties();
        propertyUtils.setOrderedProperty("jenkins.project.name", currentSelectorProject);
        //PropertyUtils.save(properties);
        mTFBranch.setText(currentSelectorProject);
    }

    @FXML
    private void initialize() {
        propertyUtils = new PropertyUtils("JenkinsProperties.properties");
        propertyUtils.getOrderedProperties();
        // Properties properties = PropertyUtils.getOrderedProperties("JenkinsProperties.properties");
       // Properties properties = PropertyUtils.getProperties();
        String property = propertyUtils.getOrderedPropertyStringByKey("jenkins.project.name");
        mTFBranch.setText(property);
        final String projectName = mTFBranch.getText();

     /*   if (projectName.equals("")) {
            return;
        }*/
        ThreadPoolManager.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                JenkinsAutoBuild.moduleBuild(projectName);
            }
        });

/*        Properties properties = PropertyUtils.getOrderedProperties("JenkinsProperties.properties");
        Set<String> names = properties.stringPropertyNames();
        List<String> moduleList = new ArrayList<>();

        StringBuilder stringBuilder = new StringBuilder();
        for (String name : names) {
            String property = properties.getProperty(name);
            //读取模块列表
            if (name.contains("jenkins.module")) {
                String substring = property.substring(property.indexOf("-"));
                property = projectName + substring;
                stringBuilder.append(property + "\r\n");
                moduleList.add(property);
            }
        }

        ObservableList<Object> arrayList = FXCollections.observableArrayList(moduleList);
        mTVBuildList.getItems().addAll(arrayList);
        arrayList.addAll("1111111111");
        mTVBuildList.setItems(arrayList);*/


        mTFBranch.textProperty().addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable observable) {
                // log.info("失去焦点" + observable);
            }
        });


        mTFBranch.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {


                ThreadPoolManager.getInstance().execute(new Runnable() {
                    @Override
                    public void run() {
                        JenkinsAutoBuild.moduleBuild(newValue);
                      //  Properties properties = PropertyUtils.getOrderedProperties("JenkinsProperties.properties");
                        propertyUtils.setOrderedProperty("jenkins.project.name", newValue);
                      //  PropertyUtils.save(properties);
                    }
                });
            }
        });


        JenkinsAutoBuild.setOnBuildListener(new JenkinsAutoBuild.OnBuildListener() {
            @Override
            public void onBuildProgress(String progress, String projectName, String projectModuleName) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        Double aDouble = Double.valueOf(progress);
                        mPBBuildProgress.setProgress(aDouble);
//                        log.info("progress "+progress+"|aDouble "+aDouble );
                        int formatInt = Utils.DoubleFormatInt(aDouble * 100);
                        mLAProgress.setText(formatInt + "%");
                    }
                });
            }

            @Override
            public void onBuildInfo(int moduleCount, int buildNum, String projectName, String projectModuleName) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        mLACount.setText("总数：" + moduleCount + "");
                        mLABuildingNum.setText("已构建：" + buildNum + "");
                        mLABuildingModuleName.setText("正在构建 " + projectModuleName);
                        mLAProgress.setVisible(true);
                        mLAProgress.setText("0%");
                    }
                });
            }

            @Override
            public void onModuleCount(int moduleCount, int buildNum, String projectName, List<ModuleInfo> moduleInfoList) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        mLACount.setText("总数：" + moduleCount + "");
                        mLABuildingNum.setText("已构建：" + buildNum + "");
                        mLABuildingModuleName.setText("正在构建 " + projectName);

                        List<String> moduleList = new ArrayList<>();
                        for (ModuleInfo moduleInfo : moduleInfoList) {
                            moduleList.add(moduleInfo.getProjectModuleBuildName());
                        }
                        arrayList = FXCollections.observableArrayList(moduleList);
                        Collections.reverse(arrayList);
                        //   mTVBuildList.getItems().removeAll();
                        mTVBuildList.getItems().setAll(arrayList);
                    }
                });
            }

            @Override
            public void onBuildState(String projectName, String projectModuleName, int State) {
                switch (State) {
                    case JenkinsAutoBuild.MODULE_BUILDING:
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                mBTBuildStart.setDisable(true);

                                mBTBuildStart.setText("正在构建...");

                                for (int i = 0; i < arrayList.size(); i++) {
                                    String moduleStr = (String) arrayList.get(i);
                                    if (moduleStr.equals(projectModuleName)) {
                                        boolean remove = arrayList.remove(moduleStr);
                                        log.info("是否删除成功！！！" + remove);
                                        arrayList.add(i, moduleStr + "\r\n 正在构建");
                                    }
                                }
                                // mTVBuildList.setItems(arrayList);
                                //  mTVBuildList.getItems().addAll(arrayList);
                                mTVBuildList.getItems().setAll(arrayList);
                                NotificationsBuild.showBottomRightNotification(projectName, "正在构建...\r\n " + projectModuleName, 10.0);
                            }
                        });
                        break;
                    case JenkinsAutoBuild.MODULE_SUCCESS:

                        //更新ListView数据
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss");

                                for (int i = 0; i < arrayList.size(); i++) {
                                    String moduleStr = (String) arrayList.get(i);
                                    if (moduleStr.contains("\r\n 正在构建")) {
                                        arrayList.remove(moduleStr);
                                        String substring = moduleStr.substring(0, moduleStr.indexOf("\r\n 正在构建"));
                                        arrayList.add(i, substring + "\r\n " + format.format(new Date()));
                                    }
                                }
                                // mTVBuildList.getItems().addAll(arrayList);
                                mTVBuildList.getItems().setAll(arrayList);
                                NotificationsBuild.showBottomRightNotification(projectName, projectModuleName + "\r\n 构建成功！", 10.0);
                                log.info("onBuildState");
                            }
                        });

                        break;
                    case JenkinsAutoBuild.MODULE_FAIL:
                        break;
                }
            }

            @Override
            public void onProjectBuildSuccess(String moduleName, String projectModuleName, int state) {
                switch (state) {
                    case JenkinsAutoBuild.MODULE_BUILDING:
                        break;
                    case JenkinsAutoBuild.MODULE_SUCCESS:
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                mBTBuildStart.setText("一键构建");
                                mBTBuildStart.setDisable(false);
                                NotificationsBuild.showBottomRightNotification(moduleName, projectModuleName + "\r\n 构建成功！", 10.0);
                            }
                        });

                        break;
                    case JenkinsAutoBuild.MODULE_FAIL:


                        break;
                }
            }
        });


    }

    public void onDebugAction(ActionEvent actionEvent) {
        // NotificationsBuild.showCenterNotification("BaseLineCenter", "BaseLineCenter", 5.0);
        // NotificationsBuild.showBottomRightNotification("BottomRight", "BottomRight", 5.0);

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/customListview.fxml"));
        Parent listViewParent = null;
        try {
            ListViewController listViewController = new ListViewController();
            listViewParent = fxmlLoader.load();
            Scene listViewScene = new Scene(listViewController.getParent());
            Stage listViewStage = new Stage();
            listViewStage.setTitle("模块列表");
            listViewStage.setScene(listViewScene);
            listViewStage.initStyle(StageStyle.UTILITY);
            listViewStage.initModality(Modality.WINDOW_MODAL);

            //anotherStage.centerOnScreen();
            listViewStage.initOwner(mStage);
   /*         ModuleListController controller = fxmlLoader.getController();
            controller.setStage(listViewStage);
            controller.setMainController(this);*/
            listViewStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public void setStage(Stage jenkinsWindowStage) {
        mStage = jenkinsWindowStage;
    }
}
