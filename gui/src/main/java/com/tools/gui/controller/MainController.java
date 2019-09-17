package com.tools.gui.controller;

import com.tools.commons.thread.ThreadPoolManager;
import com.tools.commons.utils.MySqlHelper;
import com.tools.commons.utils.PropertyUtils;
import com.tools.commons.utils.Utils;
import com.tools.gui.config.ApplicationConfig;
import com.tools.gui.config.Config;
import com.tools.gui.jenkins.ProjectBuild;
import com.tools.gui.main.Main;
import com.tools.gui.process.*;
import com.tools.gui.process.sync.PushConfigProcess;
import com.tools.gui.utils.view.AlertUtils;
import com.tools.gui.utils.view.JFXSnackbarUtils;
import com.tools.gui.utils.view.ProgressUtils;
import com.tools.gui.utils.view.RestartUtils;
import com.tools.service.constant.DeployTypeEnum;
import com.tools.service.constant.ServerStartTypeEnum;
import com.tools.service.constant.TaskEnum;
import com.tools.service.context.ApplicationContext;
import com.tools.service.model.DeployConfigModel;
import com.tools.service.model.DeployState;
import com.tools.socket.bean.Command;
import com.tools.socket.client.SocketClient;
import com.tools.socket.manager.SocketManager;
import io.netty.channel.Channel;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.web.WebView;
import javafx.stage.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.*;

import static com.tools.gui.utils.view.AlertUtils.showAlert;
import static com.tools.gui.utils.view.FileChooserUtils.showSelectDirectoryChooser;
import static com.tools.gui.utils.view.FileChooserUtils.showSelectFileChooser;
import static com.tools.service.constant.DeployTypeEnum.DEPLOY_2UPLOAD;
import static com.tools.service.constant.DeployTypeEnum.DEPLOY_UPLOAD2CM;


public class MainController {
    private static final Logger log = LoggerFactory.getLogger(MainController.class);

    private static Boolean isStart = false;
    public VBox mVBox;
    //资源路径
    public TextField mTFResourcePath;
    //数据库用户名
    public TextField mTFDBUserName;
    //数据库密码
    public TextField mTFDBPassword;
    //数据库地址
    public TextField mTFDBAddress;
    //数据库名字
    public TextField mTFDBName;
    public Button mBTStart;
    //tomcat端口
    public TextField mTFTomcatPort;
    //结束进程
    public Button mBTKillPort;
    public Button mBTStartTomcat;
    //配置文件是否勾选
    public CheckBox mCBPublishConfig;
    public CheckBox mCBStreamConfig;
    //执行输出
    public TextArea mTAConsole;
    //关于菜单
    public MenuItem mMenuItemAbout;
    //从服务获取配置
    public MenuItem mMenuItemConfigPull;
    //CR设置菜单
    public MenuItem mMenuItemCRProperties;
    //Jenkins自动打包配置
    public MenuItem mMenuItemJenkinsProperties;
    /**
     * 添加配置文件列表菜单
     */
    public MenuItem mMenuItemConfigList;
    /**
     * 资源分离配置菜单
     */
    public MenuItem mMenuItemZYFLConfig;
    public MenuItem mMenuItemWorkersProperties;
    /**
     * 模式切换菜单
     */
    public MenuItem mMenuItemWindows;
    public MenuItem mMenuItemLinux;

    //启动方式
    public RadioButton mRBService;
    public RadioButton mRBConsole;
    /*配置文件*/
    public Button mBTCmCfg;
    public Button mBTIntegrationCfg;
    public Button mBTSpring;
    public Button mBTPublishCfg;
    public FlowPane mFlowPaneCfgRoot;

    //Jenkins按钮
    public Button mBTJenkinsBuild;
    public Button mBTDownload;
    //选择下载还是自选择
    public RadioButton mRBSelect;
    public RadioButton mRBSelectDownload;
    public ChoiceBox mDBChoiceBox;
    //root布局
    //public AnchorPane anchorPaneRoot;
    public VBox anchorPaneRoot;
    //RadioButton 删除或者不删除
    public RadioButton mRBUnDelete;
    public RadioButton mRBDelete;
    //ROOT.war路径
    public TextField mTFWarPath;
    //ROOT解压路径 tomcat\webapps
    public TextField mTFWarUnPath;
    //选择解压路径
    public Button mBTSelectUnZIP;
    //选择war按钮
    public Button mBTSelectWar;

    //ROOT包静态文件路径
    public TextField mTFStaticWarPath;
    //选择ROOT包静态文件路径按钮
    public Button mBTSelectStaticWar;
    //ROOT包静态文件解压路径
    public TextField mTFStaticWarUnPath;
    //ROOT包静态文件解压路径按钮
    public Button mBTSelectStaticUnZIP;
    //选择资源路径
    public Button mBTSelectResource;
    //解压按钮
    public Button mBTDeployStart;
    //进度任务描述
    public String dbAddressStr = "localhost:3306";
    public String dbNameStr = "cr_4_0_6_100";
    public String dbUserNameStr = "root";
    public String dbPassWordStr = "root";
    public String resourcePath = "E:\\ThinkWin\\ThinkWinCRV3.5.0\\apache/htdocs/res/";
    public Button mBTDebug;
    public Button mBTTest;
    public GridPane gridPaneRoot;

    public Button mBTDeployType;

    private Stage stage;
    private String warUnPath;
    private String tomcatServiceName;
    //默认的ROOT.war路径。默认为空
    private String defaultWarPath = "";
    private String currentWarPath = null;
    private String defaultUnZIPWarPath = "";
    //默认的ROOT静态解压资源路径。默认为空
    private String defaultStaticUnZIPWarPath = "";

    //默认的ROOT静态资源路径。默认为空
    private String defaultStaticWarPath = "";

    private String defaultResourcePath = "";
    private ChoiceBox<String> mDBPopupChoiceBox;
    private Popup mDBPopup;
    private Stage mDBProgressPopup;
    private List<String> dbList;
    private ObservableList<String> observableList;
    private PropertyUtils propertyUtils;
    private JenkinsLoginController jenkinsLoginController;
    private String type;
    private DeployProcess deployProcess;
    private SyncConfigProcess syncConfigProcess;

    /**
     * button点击事件
     *
     * @param actionEvent
     */
    public void onAction(ActionEvent actionEvent) {
        //选择war文件
        if (actionEvent.getSource() == mBTSelectWar) {
            String filePathStr = "";
            if (defaultWarPath.equals("")) {
                filePathStr = showSelectFileChooser(new FileChooser.ExtensionFilter("war(*.war)", "*.war"), "C:\\", stage);
            } else {
                File file = new File(defaultWarPath);
                if (file.isDirectory()) {
                    filePathStr = showSelectFileChooser(new FileChooser.ExtensionFilter("war(*.war)", "*.war"), file.getAbsolutePath(), stage);
                } else {
                    filePathStr = showSelectFileChooser(new FileChooser.ExtensionFilter("war(*.war)", "*.war"), file.getParent(), stage);
                }
            }
            if (filePathStr.trim().equals("")) {
                return;
            }
            defaultWarPath = filePathStr;
            currentWarPath = filePathStr;
            //设置war路径
            mTFWarPath.setText(filePathStr);
            log.info("war:" + filePathStr);
            appendText("war:" + filePathStr);
        }
        //选择解压路径
        if (actionEvent.getSource() == mBTSelectUnZIP) {
            String dirPathStr = "";
            if (defaultUnZIPWarPath.equals("")) {
                dirPathStr = showSelectDirectoryChooser(null, "d:\\", stage);
            } else {
                dirPathStr = showSelectDirectoryChooser(null, defaultUnZIPWarPath, stage);
            }
            if (dirPathStr.trim().equals("")) {
                return;
            }
            defaultUnZIPWarPath = dirPathStr;

            mTFWarUnPath.setText(dirPathStr);
            log.info("UnPath:" + dirPathStr);
            appendText("UnPath:" + dirPathStr);
        }

        //选择ROOT静态资源war文件
        if (actionEvent.getSource() == mBTSelectStaticWar) {
            String filePathStr = "";
            if (defaultStaticWarPath.equals("")) {
                filePathStr = showSelectFileChooser(new FileChooser.ExtensionFilter("war(*.war)", "*.war"), "D:\\Downloads", stage);
            } else {
                File file = new File(defaultStaticWarPath);
                if (file.isDirectory()) {
                    filePathStr = showSelectFileChooser(new FileChooser.ExtensionFilter("war(*.war)", "*.war"), file.getAbsolutePath(), stage);
                } else {
                    filePathStr = showSelectFileChooser(new FileChooser.ExtensionFilter("war(*.war)", "*.war"), file.getParent(), stage);
                }
            }
            if (filePathStr.trim().equals("")) {
                return;
            }
            defaultStaticWarPath = filePathStr;
            //选择删除和
            // currentWarPath = filePathStr;
            //设置静态资源war路径
            mTFStaticWarPath.setText(filePathStr);
            log.info("war:" + filePathStr);
            appendText("StaticWar:" + filePathStr);
        }

        //选择静态资源解压路径
        if (actionEvent.getSource() == mBTSelectStaticUnZIP) {
            String dirPathStr = "";
            if (defaultStaticUnZIPWarPath.equals("")) {
                dirPathStr = showSelectDirectoryChooser(null, "d:\\", stage);
            } else {
                dirPathStr = showSelectDirectoryChooser(null, defaultStaticUnZIPWarPath, stage);
            }
            if (dirPathStr.trim().equals("")) {
                return;
            }
            defaultStaticUnZIPWarPath = dirPathStr;

            mTFStaticWarUnPath.setText(dirPathStr);
            log.info("StaticUnPath:" + dirPathStr);
            appendText("StaticUnPath:" + dirPathStr);
        }


        //选择资源路径
        if (actionEvent.getSource() == mBTSelectResource) {
            String selectDirPath = "";
            if (defaultResourcePath.equals("")) {
                selectDirPath = showSelectDirectoryChooser(null, "D:\\", stage);
            } else {
                selectDirPath = showSelectDirectoryChooser(null, defaultResourcePath, stage);
            }
            if (selectDirPath.trim().equals("")) {
                return;
            }
            defaultResourcePath = selectDirPath;
            mTFResourcePath.setText(selectDirPath);
            appendText("ResourcePath:" + selectDirPath);
        }
        //选择一键部署
        if (actionEvent.getSource() == mBTDeployStart) {
            //删除是否删除
            if (mRBUnDelete.isSelected()) {
            }
            if (mRBDelete.isSelected()) {
            }
            String warPath = mTFWarPath.getText().trim();
            warUnPath = mTFWarUnPath.getText();

            if (warPath.trim().equals("") || warUnPath.trim().equals("")) {
                showAlert("错误", "路径没有选择正确", "没有选择War路径和解压路径");
                return;
            }

            warUnPath = warUnPath + "\\ROOT";
            String dbAddressStr = mTFDBAddress.getText().trim();
            String dbNameStr = mTFDBName.getText().trim();
            String dbUserNameStr = mTFDBUserName.getText().trim();
            String dbPassWordStr = mTFDBPassword.getText().trim();

            if (dbAddressStr.equals("") && dbNameStr.equals("") && dbUserNameStr.equals("")) {
                showAlert("错误", "数据库配置不完整", "数据库地址、名字、用户名没有输入");
                return;
            }
            String resourcePathStr = mTFResourcePath.getText().trim();
            if (resourcePathStr.equals("")) {
                showAlert("错误", "资源路径为空", "资源路径没有输入");
                return;
            }
            //查找进程
            String port = mTFTomcatPort.getText();
            if (port.equals("")) {
                showAlert("错误", "tomcat为空", "tomcat为空");
                return;
            }
            String deployType = propertyUtils.getConfigurationPropertyStringByKey("deploy.type");
            DeployTypeEnum deployTYpe;
            if (deployType.isEmpty()) {
                deployTYpe = DEPLOY_UPLOAD2CM;
            } else {
                deployTYpe = DeployTypeEnum.valueOf(deployType);
            }
            if (!deployTYpe.equals(DEPLOY_2UPLOAD)) {
                File warFile = new File(warPath);
                if (!warFile.exists()) {
                    showAlert("错误", "ROOT.war不存在", "ROOT.war不存在，请检查所选路径是否正确！");
                    return;
                }
            }

            if (Config.isModeSelector) {
                Map<String, Boolean> map = new HashMap<>();
                Dialog<String> dialog = new Dialog<>();
                try {
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/deploy_mode_dialog.fxml"));
                    VBox vBox = fxmlLoader.load();
                    dialog.getDialogPane().setContent(vBox);

                    CheckBox cm_CheckBox = (CheckBox) vBox.lookup("#mCB_CM");
                    CheckBox zyfl_CheckBox = (CheckBox) vBox.lookup("#mCB_ZYFL");
                    CheckBox upload_CheckBox = (CheckBox) vBox.lookup("#mCB_UPLOAD");
                    CheckBox apache_config_CheckBox = (CheckBox) vBox.lookup("#mCB_APACHE_CONFIG");

                    dialog.setTitle("部署选择");
                    //    dialog.getDialogPane().setContent(vBox);
                    dialog.setWidth(300);
                    dialog.setHeight(200);

                    dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
                    dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

                    dialog.setResultConverter(param -> {

                        if (param == ButtonType.OK) {
                            map.put("cm", cm_CheckBox.isSelected());
                            map.put("zyfl", zyfl_CheckBox.isSelected());
                            map.put("upload", upload_CheckBox.isSelected());
                            map.put("apache_config", apache_config_CheckBox.isSelected());
                            dialog.close();
                            // TODO: 2019/9/5 添加部署代码
                            setConfig();
                            //执行部署
                            ApplicationContext.getDeployConfigModel().setDeployModeSelectorMap(map);
                            Command command = new Command();
                            command.setContent(ApplicationContext.getDeployConfigModel());
                            command.setCommandCode(CommandMethodEnum.DEPLOY_INIT.getCode());
                            deployProcess.sendMessage(command);
                        }
                        if (param == ButtonType.CANCEL) {
                            //不执行任何操作
                            dialog.close();
                        }
                        return "";
                    });
                    dialog.showAndWait();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
            }

            //执行部署
            //   ThreadPoolManager.getInstance().execute(deployProcessorRunnable);
        }


        if (actionEvent.getSource() == mBTStart) {

            DeployConfigModel deployConfigModel = ApplicationContext.getDeployConfigModel();
            if (mRBConsole.isSelected()) {
                deployConfigModel.setServerStartTypeEnum(ServerStartTypeEnum.CONSOLE);
            }
            if (mRBService.isSelected()) {
                deployConfigModel.setServerStartTypeEnum(ServerStartTypeEnum.SERVICE);
            }
            if (Config.isModeSelector) {

                Map<String, Boolean> map = new HashMap<>();
                Dialog<String> dialog = new Dialog<>();
                try {
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/deploy_mode_dialog.fxml"));
                    VBox vBox = fxmlLoader.load();

                    DialogController controller = fxmlLoader.getController();
                    controller.setType(1);
                    dialog.getDialogPane().setContent(vBox);

                    CheckBox cm_CheckBox = (CheckBox) vBox.lookup("#mCB_CM");
                    CheckBox upload_CheckBox = (CheckBox) vBox.lookup("#mCB_UPLOAD");
                    dialog.setTitle("部署选择");
                    //    dialog.getDialogPane().setContent(vBox);
                    dialog.setWidth(300);
                    dialog.setHeight(200);

                    dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
                    dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

                    dialog.setResultConverter(param -> {
                        if (param == ButtonType.OK) {
                            map.put("cm", cm_CheckBox.isSelected());
                            map.put("upload", upload_CheckBox.isSelected());
                            dialog.close();
                            // TODO: 2019/9/5 添加启动代码
                            ApplicationContext.getDeployConfigModel().setDeployModeSelectorMap(map);

                            // ThreadPoolManager.getInstance().execute(deployModeSelectorServerControlRunnable);
                        }
                        if (param == ButtonType.CANCEL) {
                            //不执行任何操作
                            dialog.close();
                        }
                        return "";
                    });
                    dialog.showAndWait();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                //  ThreadPoolManager.getInstance().execute(deployServerControlRunnable);
            }


        }

        //结束进程按钮点击
        if (actionEvent.getSource() == mBTKillPort) {
            String tomcatPort = mTFTomcatPort.getText();

        }

    }


    /**
     * 点击选择数据库 回调方法
     *
     * @param event
     */
    public void onDBSelectAction(ActionEvent event) {
        /*String dbAddress = mTFDBAddress.getText();
        String userName = mTFDBUserName.getText();
        String password = mTFDBPassword.getText();
        List<String> dbList = MySqlHelper.getDBList(dbAddress, userName, password);
        mDBChoiceBox.setItems(FXCollections.observableList(dbList));
        mDBChoiceBox.setValue(null);*/

        Object dbName = mDBChoiceBox.getSelectionModel().getSelectedItem();
        if (null != dbName) {
            mTFDBName.setText(dbName.toString());
            mDBChoiceBox.setValue(null);
            mDBChoiceBox.hide();
            // mDBChoiceBox.getItems().clear();
        }

    }


    /**
     * 更新部署时按钮的状态
     *
     * @param disable
     * @param txt
     */
    private void setBTDeployStartState(boolean disable, String txt) {
        Platform.runLater(() -> {
            mBTDeployStart.setDisable(disable);
            mBTDeployStart.setText(txt);
        });
    }


    private void setBTStartState(boolean disable, String txt) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                mBTStart.setDisable(disable);
                mBTStart.setText(txt);
            }
        });
    }

    public void setStage(Stage primaryStage) {
        this.stage = primaryStage;
    }


    private void appendText(String str) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss");
                String dateStr = format.format(new Date());
                mTAConsole.appendText(dateStr + " - " + str + "\n");
            }
        });
    }

    @FXML
    public void initialize() {
        propertyUtils = new PropertyUtils(System.getProperty("conf.path") + "/" + ApplicationConfig.DEPLOY_CONFIG_FILE_NAME);
        propertyUtils.getConfiguration2ReloadProperties();
        //  OutputStreamConsole console = new OutputStreamConsole(mTAConsole);
        // System.setOut(new PrintStream(console, true));

        ToggleGroup toggleGroup = new ToggleGroup();
        mRBUnDelete.setToggleGroup(toggleGroup);
        mRBDelete.setToggleGroup(toggleGroup);
        mRBDelete.setSelected(true);
        //控件鼠标悬浮提示
        Tooltip mRBDeleteTooltip = new Tooltip();
        mRBDeleteTooltip.setText("选择删除，则将Tomcat下ROOT文件夹全部删除");
        Image image = new Image(MainController.class.getClass().getResourceAsStream("/image/icons.png"));
        mRBDeleteTooltip.setGraphic(new ImageView(image));
        mRBDelete.setTooltip(mRBDeleteTooltip);
        mRBDelete.setDisable(true);

        Tooltip mRBUnDeleteTooltip = new Tooltip();
        mRBUnDeleteTooltip.setText("选择不删除，则不会将Tomcat下ROOT文件夹删除，则会出现部署异常！");
        Image UnDeleteImage = new Image(getClass().getResourceAsStream("/image/icons.png"));
        mRBDeleteTooltip.setGraphic(new ImageView(UnDeleteImage));
        mRBUnDelete.setTooltip(mRBUnDeleteTooltip);
        mRBUnDelete.setDisable(true);


        // mProgressBar.setProgress(0.5F);
        mCBPublishConfig.setSelected(true);
        mCBPublishConfig.setDisable(true);
        mCBStreamConfig.setSelected(true);
        mCBStreamConfig.setDisable(true);


        mTAConsole.setWrapText(true);
        mTAConsole.setEditable(false);
        mTAConsole.setMaxHeight(Double.MAX_VALUE);
        mTAConsole.setMaxWidth(Double.MAX_VALUE);


        ToggleGroup startTypeGroup = new ToggleGroup();
        mRBService.setToggleGroup(startTypeGroup);
        mRBConsole.setToggleGroup(startTypeGroup);
        // mRBConsole.setSelected(true);


        ToggleGroup selectROOTGroup = new ToggleGroup();
        mRBSelect.setToggleGroup(selectROOTGroup);
        mRBSelectDownload.setToggleGroup(selectROOTGroup);
        //    mRBSelectDownload.setSelected(true);
        Tooltip selectTooltip = new Tooltip();
        selectTooltip.setText("选择‘自选’则需要自己手动选择ROOT.war位置，选择‘下载’则需要用工具下载ROOT.war自动下载");
        mRBSelectDownload.setTooltip(selectTooltip);
        mRBSelect.setTooltip(selectTooltip);
        mRBSelect.setSelected(true);

        selectROOTGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            @Override
            public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
                log.info("oldValue:" + oldValue + " | newValue:" + newValue);

                if (mRBSelectDownload.isSelected()) {
                    mTFWarPath.setDisable(true);
                    mTFWarPath.setText(Config.downloadFilePath);
                }

                if (mRBSelect.isSelected()) {
                    mTFWarPath.setDisable(false);
                    if (null == currentWarPath) {
                        mTFWarPath.setText(propertyUtils.getConfigurationPropertyStringByKey("cm.tomcat.war.path"));
                    } else {
                        mTFWarPath.setText(currentWarPath);
                    }
                }
            }
        });


        //设置默认输入内容
        mTFDBAddress.setText(dbAddressStr);
        mTFDBName.setText(dbNameStr);
        mTFDBUserName.setText(dbUserNameStr);
        mTFDBPassword.setText(dbPassWordStr);
        mTFResourcePath.setText(resourcePath);
        // TODO: 2018/5/9  启动停止功能暂停
        mBTStartTomcat.setDisable(true);
        mBTStart.setVisible(true);


        // mTFWarPath.setText("F:\\QQdownload\\ROOT.war");
        //  mTFWarUnPath.setText("E:\\ThinkWin\\ThinkWinCRV3.5.0\\tomcat\\webapps");

        if (Config.isHome) {
            defaultWarPath = "D:\\Downloads";
        } else {
            defaultWarPath = propertyUtils.getConfigurationPropertyStringByKey("cm.tomcat.war.path");
            defaultUnZIPWarPath = propertyUtils.getConfigurationPropertyStringByKey("cm.tomcat.exportWar.path");

            defaultStaticWarPath = propertyUtils.getConfigurationPropertyStringByKey("cm.zyflWar.path");
            defaultStaticUnZIPWarPath = propertyUtils.getConfigurationPropertyStringByKey("cm.zyfl.exportWar.path");
        }

        //初始化读取配置信息
        mTFWarPath.setText(propertyUtils.getConfigurationPropertyStringByKey("cm.tomcat.war.path"));
        mTFWarUnPath.setText(propertyUtils.getConfigurationPropertyStringByKey("cm.tomcat.exportWar.path"));
        mTFDBAddress.setText(propertyUtils.getConfigurationPropertyStringByKey("cm.config.db.address"));
        mTFDBName.setText(propertyUtils.getConfigurationPropertyStringByKey("cm.config.db.name"));
        mTFDBUserName.setText(propertyUtils.getConfigurationPropertyStringByKey("cm.config.db.userName"));
        mTFDBPassword.setText(propertyUtils.getConfigurationPropertyStringByKey("cm.config.db.userPass"));
        mTFResourcePath.setText(propertyUtils.getConfigurationPropertyStringByKey("cm.config.resourcesPath"));
        mTFTomcatPort.setText(propertyUtils.getConfigurationPropertyStringByKey("cm.tomcat.port"));

        mTFStaticWarPath.setText(propertyUtils.getConfigurationPropertyStringByKey("cm.zyflWar.path"));
        mTFStaticWarUnPath.setText(propertyUtils.getConfigurationPropertyStringByKey("cm.zyfl.exportWar.path"));

        String tomcatStartType = propertyUtils.getConfigurationPropertyStringByKey("cm.tomcat.start.type");

        if (tomcatStartType.equals("console")) {
            mRBConsole.setSelected(true);
            mRBService.setSelected(false);
        } else if (tomcatStartType.equals("service")) {
            mRBService.setSelected(true);
            mRBConsole.setSelected(false);
        }
        if (!Config.isDebug) {
            // TODO: 2018/11/20 暂将结束进程和启动进程按钮隐藏
            mBTKillPort.setVisible(false);
            mBTStartTomcat.setVisible(false);
            // TODO: 2018/11/20 暂将Jenkins打包和下载按钮隐藏
            mBTJenkinsBuild.setVisible(false);
            mBTDownload.setVisible(false);

            // TODO: 2018/12/18 不是debug模式下，将 debug按钮隐藏
            mBTDebug.setVisible(false);
            mBTTest.setVisible(false);


            mBTCmCfg.setVisible(false);
            mBTCmCfg.setManaged(false);
            mBTIntegrationCfg.setVisible(false);
            mBTIntegrationCfg.setManaged(false);
            mBTSpring.setVisible(false);
            mBTSpring.setManaged(false);
            mBTPublishCfg.setVisible(false);
            mBTPublishCfg.setManaged(false);
        }
        mFlowPaneCfgRoot.setVgap(3.0);
        /*-------初始化配置文件列表----------*/
        Properties properties = new PropertyUtils(Config.CRConfigListFileName).getOrderedProperties();

        Set<String> propertyNames = properties.stringPropertyNames();
        for (String name : propertyNames) {
            //将文件后缀名截取掉
            //    String substring = name.substring(0, name.lastIndexOf("."));
            String filePath = properties.getProperty(name);
            File file = new File(filePath);
            //截取后缀名
            String substring = file.getName().substring(0, file.getName().lastIndexOf("."));
            Button configButton = new Button(substring);
            configButton.setId(name);

            mFlowPaneCfgRoot.getChildren().add(configButton);

            configButton.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    Button source = (Button) event.getSource();
                    String path = properties.getProperty(source.getId());
                    File file = new File(path);
                    //打开编辑窗口
                    showEditWindowV2(file.getAbsolutePath(), file.getName());
                }
            });
        }

        // readServerState();

        /**
         * 更新数据库信息
         */
        observableList = FXCollections.observableArrayList();
        List<String> defaultItem = new ArrayList<>();
        defaultItem.add("正在连接数据库");
        observableList.setAll();
        mDBPopupChoiceBox = new ChoiceBox<>();
        mDBPopupChoiceBox.setItems(observableList);
        // mDBPopupChoiceBox.setMaxWidth();
        //  initPopupChoiceBoxListener();

        mDBChoiceBox.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                System.out.println("MouseEvent:" + event.getX() + "," + event.getY());
                System.out.println("MouseEventgetSceneX:" + event.getSceneX() + "," + event.getSceneY());
                System.out.println("MouseEventgetScreenX:" + event.getScreenX() + "," + event.getScreenY());

                if (null == mDBPopup) {
                    //数据库选择器
                    mDBPopup = createPopup(mDBPopupChoiceBox);
                    //进度条
                    mDBProgressPopup = createProgressPopup(stage);
                }
                // setProgressPopupShow(true);
                mDBProgressPopup.show();
                centerScreenForApplication(mDBProgressPopup);
                //    JFXSnackbarUtils.show("正在连接数据库", 2000L, anchorPaneRoot);
                if (Config.isDebug) {
                    Button test = new Button("test");
                    //  test.visibleProperty().setValue(true);
                    anchorPaneRoot.getChildren().addAll(test);
                    test.setVisible(true);
                    test.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event) {

                            JFXSnackbarUtils.show(stage.getY() + "", 5000, anchorPaneRoot);
                        }
                    });
                }
                String dbAddress = mTFDBAddress.getText();
                String userName = mTFDBUserName.getText();
                String password = mTFDBPassword.getText();
                ThreadPoolManager.getInstance().execute(() -> {
                    //连接数据库 获取数据库列表
                    long start = System.currentTimeMillis();
                    dbList = MySqlHelper.getDBList(dbAddress, userName, password);
                    long end = System.currentTimeMillis();
                    log.info("数据库获取耗时：" + (end - start) + "ms");

                    Platform.runLater(() -> {
                       /*         if (dbList.size() == 1&&dbList.get(0).contains("数据库连接失败")) {
                                    mDBProgressPopup.hide();
                                    //显示popup窗体，
                                    mDBPopup.show(stage,event.getScreenX()-event.getX(),event.getScreenY()-event.getY());
                                    mDBPopupChoiceBox.show();
                                    // JFXSnackbarUtils.show("数据库连接失败，请检查是否输入错误", 2000L, anchorPaneRoot);
                                    return;
                                    //判断如果 之前的数据与最新数据不相同的话，再重新设置新的集合
                                } else*/

                        if (null != dbList && !observableList.equals(dbList)) {
                            observableList = null;
                            mDBPopupChoiceBox = null;

                            mDBPopup.getContent().clear();
                            mDBPopupChoiceBox = new ChoiceBox<>();
                            mDBPopup.getContent().add(mDBPopupChoiceBox);
                            //将浮层的选择器大小设置和主框体一直，覆盖掉主窗体
                            mDBPopupChoiceBox.setMaxWidth(mDBChoiceBox.getWidth());
                            // observableList.clear();
                            // observableList.addAll(dbList);
                            //added by 2018-11-19 13:16  修复在连续addAll 或者setItem的时候响应过慢导致主线程堵塞
                            observableList = FXCollections.observableArrayList(dbList);
                            mDBPopupChoiceBox.setItems(observableList);
                            initPopupChoiceBoxListener();
                        }
                        //  setProgressPopupShow(false);
                        mDBProgressPopup.hide();
                        //显示popup窗体，
                        mDBPopup.show(stage, event.getScreenX() - event.getX(), event.getScreenY() - event.getY());
                        mDBPopupChoiceBox.show();
                    });
                });

                //  JFXSnackbarUtils.show("连接成功", 2000L, anchorPaneRoot);
                //显示popup窗体，
                //  mDBPopup.show(stage,event.getScreenX()-event.getX(),event.getScreenY()-event.getY());
                // mDBPopupChoiceBox.show();
            }
        });
        setConfig();

        if (Config.isConfigSync) {
            PushConfigProcess pushConfigProcess = new PushConfigProcess();
            ThreadPoolManager.getInstance().execute(() -> {
                log.info("服务启动7777");
                try {
                    SocketManager.getSocketServer(7777).startServer();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }
        /**
         * 初始化处理器
         */
        FileUploadProcess fileUploadProcess = new FileUploadProcess();
        syncConfigProcess = new SyncConfigProcess();
        deployProcess = new DeployProcess();

        deployProcess.setOnDeployProcessorListener(new DeployListener());
    }

    /**
     * 初始化PopupChoiceBox监听器 ,选择器显示和隐藏时调用此方法
     */
    private void initPopupChoiceBoxListener() {
        mDBPopupChoiceBox.showingProperty().addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable observable) {
                if (!mDBPopupChoiceBox.isShowing()) {
                    mDBPopup.hide();
                    //隐藏的时候，获取当前选择器选择的值
                    Object dbName = mDBPopupChoiceBox.getSelectionModel().getSelectedItem();
                    if (null != dbName) {
                        if (((String) dbName).contains("数据库连接失败")) {
                            return;
                        }
                        mTFDBName.setText(dbName.toString());
                        mDBPopupChoiceBox.setValue(null);
                    }

                }
            }
        });

    }

    /**
     * 创建Popup 窗体
     *
     * @param choiceBox 窗体内容
     * @return 创建的窗体
     */
    private Popup createPopup(ChoiceBox<String> choiceBox) {
        Popup popup = new Popup();
        popup.getContent().add(choiceBox);
        return popup;
    }

    /**
     * loading 圈
     *
     * @param stage
     * @return
     */
    public Stage createProgressPopup(Stage stage) {
        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.initOwner(stage);
        dialogStage.initStyle(StageStyle.TRANSPARENT);
        BorderPane borderPane = new BorderPane();
        ProgressIndicator progressIndicator = new ProgressIndicator();
        progressIndicator.setProgress(-1F);
        progressIndicator.setStyle("-fx-progress-color: #1791ff;" +
                "-fx-background-color: rgba(0,0,0,0);");
        progressIndicator.setMaxWidth(stage.getWidth() / 4);
        progressIndicator.setMaxHeight(stage.getHeight() / 4);
        // Scene dialogScene = new Scene(choiceBox1, 200, 200);
        borderPane.setCenter(progressIndicator);
        Label label = new Label("正在获取数据...");
        label.setStyle("-fx-font-style: italic;-fx-font-size: 20;-fx-font-family: Microsoft YaHei; -fx-text-fill: blue");
        BorderPane borderPaneBottomLabel = new BorderPane();
        borderPaneBottomLabel.setCenter(label);
        borderPane.setBottom(borderPaneBottomLabel);
        // borderPane.setStyle("-fx-background-color: rgba(0,0,0,0);");
        // borderPane.setStyle("-fx-background-color: rgba(0,0,0,0);");
        borderPane.setStyle("-fx-background-color: rgba(255, 255, 255, 0.5);-fx-background-insets: 20");
        Scene dialogScene = new Scene(borderPane, stage.getWidth() / 2, stage.getHeight() / 2);
        dialogScene.setFill(Color.TRANSPARENT);

        //Scene dialogScene = new Scene(dialogVbox, 200, 200);
        dialogStage.setScene(dialogScene);

        // dialogStage.show();
        return dialogStage;
    }

    public void setProgressPopupShow(boolean isShow) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                if (isShow) {
                    mDBProgressPopup.show();
                } else {
                    mDBProgressPopup.hide();
                }
            }
        });
    }


    public void onMenuAction(ActionEvent actionEvent) {
        //Cr默认配置
        if (actionEvent.getSource() == mMenuItemCRProperties) {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/CRSetting.fxml"));
                Parent crSettingRoot = fxmlLoader.load();
                Scene crSettingScene = new Scene(crSettingRoot, 600.0, 410.0);
                Stage crSettingStage = new Stage();
                crSettingStage.setTitle("配置");
                crSettingStage.setScene(crSettingScene);
                crSettingStage.initStyle(StageStyle.UTILITY);
                // crSettingStage.centerOnScreen();

                //设置弹窗时，堵塞父窗口
                crSettingStage.initModality(Modality.WINDOW_MODAL);
                crSettingStage.initOwner(stage);
                crSettingStage.show();
                CRSettingController crSettingController = fxmlLoader.getController();
                crSettingController.setStage(crSettingStage);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        //Jenkins打包配置
        if (actionEvent.getSource() == mMenuItemJenkinsProperties) {

            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/JenkinsSetting.fxml"));
                Parent jenkinsSettingRoot = fxmlLoader.load();
                Scene jenkinsSettingScene = new Scene(jenkinsSettingRoot, 600.0, 410.0);
                Stage jenkinsSettingStage = new Stage();
                jenkinsSettingStage.setTitle("Jenkins打包配置");
                jenkinsSettingStage.setScene(jenkinsSettingScene);
                jenkinsSettingStage.initStyle(StageStyle.UTILITY);
                // crSettingStage.centerOnScreen();

                //设置弹窗时，堵塞父窗口
                jenkinsSettingStage.initModality(Modality.WINDOW_MODAL);
                jenkinsSettingStage.initOwner(stage);
                jenkinsSettingStage.show();
                JenkinsSettingController jenkinsSettingController = fxmlLoader.getController();
                jenkinsSettingController.setStage(jenkinsSettingStage);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //添加配置文件
        if (actionEvent.getSource() == mMenuItemConfigList) {

            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/windows_config_add.fxml"));
                Parent configAddRoot = fxmlLoader.load();
                Scene configAddScene = new Scene(configAddRoot, 630.0, 600.0);
                Stage configAddStage = new Stage();


                configAddStage.setFullScreen(false);
                configAddStage.setResizable(false);

                configAddStage.setTitle("添加配置文件");
                configAddStage.setScene(configAddScene);
                configAddStage.initStyle(StageStyle.UTILITY);
                // crSettingStage.centerOnScreen();

                //设置弹窗时，堵塞父窗口
                configAddStage.initModality(Modality.WINDOW_MODAL);
                configAddStage.initOwner(stage);
                configAddStage.show();
                JFXConfigAddController jfxConfigAddController = fxmlLoader.getController();
                jfxConfigAddController.setStage(configAddStage);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //Apache资源分离配置
        if (actionEvent.getSource() == mMenuItemZYFLConfig) {

            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/ZYFLSettings.fxml"));
                Parent ZYFLRootParent = fxmlLoader.load();
                Scene zyflScene = new Scene(ZYFLRootParent, 1050.0, 700.0);
                Stage zyflStage = new Stage();

                zyflStage.setFullScreen(false);
                zyflStage.setResizable(false);
                zyflStage.setWidth(1050.0);
                zyflStage.setHeight(700.0);
                zyflStage.setTitle("资源分离配置");
                zyflStage.setScene(zyflScene);
                zyflStage.initStyle(StageStyle.UTILITY);
                // crSettingStage.centerOnScreen();

                ZYFLSettingController zyflSettingController = fxmlLoader.getController();
                zyflSettingController.setStage(zyflStage);
                zyflSettingController.setType(0);

                //设置弹窗时，堵塞父窗口
                zyflStage.initModality(Modality.WINDOW_MODAL);
                zyflStage.initOwner(stage);
                zyflStage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //Apache workers.properties 配置
        if (actionEvent.getSource() == mMenuItemWorkersProperties) {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/ZYFLSettings.fxml"));
                Parent ZYFLRootParent = fxmlLoader.load();
                Scene zyflScene = new Scene(ZYFLRootParent, 1050.0, 700.0);
                Stage zyflStage = new Stage();

                zyflStage.setFullScreen(false);
                zyflStage.setResizable(false);
                zyflStage.setWidth(1050.0);
                zyflStage.setHeight(700.0);
                zyflStage.setTitle("Apache workers.properties配置");
                zyflStage.setScene(zyflScene);
                zyflStage.initStyle(StageStyle.UTILITY);
                // crSettingStage.centerOnScreen();

                ZYFLSettingController zyflSettingController = fxmlLoader.getController();
                zyflSettingController.setStage(zyflStage);
                zyflSettingController.setType(1);
                //设置弹窗时，堵塞父窗口
                zyflStage.initModality(Modality.WINDOW_MODAL);
                zyflStage.initOwner(stage);
                zyflStage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //从服务获取配置
        if (actionEvent.getSource() == mMenuItemConfigPull) {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/window_config_pull.fxml"));
                Parent parent = fxmlLoader.load();
                Scene scene = new Scene(parent);
                Stage stage = new Stage();
                stage.setTitle("获取服务器配置");
                stage.setScene(scene);
                //  stage.initStyle(StageStyle.UTILITY);
                //anotherStage.centerOnScreen();
                //设置弹窗时，堵塞父窗口
                stage.initModality(Modality.WINDOW_MODAL);
                stage.initOwner(Main.mainStage);
                stage.show();
                //  AboutController aboutController = fxmlLoader.getController();
                //   aboutController.setStage(stage);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //关于
        if (actionEvent.getSource() == mMenuItemAbout) {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/about.fxml"));
                Parent anotherRoot = fxmlLoader.load();
                Scene anotherScene = new Scene(anotherRoot);
                Stage anotherStage = new Stage();
                anotherStage.setTitle("关于");
                anotherStage.setScene(anotherScene);
                anotherStage.initStyle(StageStyle.UTILITY);
                //anotherStage.centerOnScreen();
                anotherStage.show();
                AboutController aboutController = fxmlLoader.getController();
                aboutController.setStage(anotherStage);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //切换到windows菜单
        if (actionEvent.getSource() == mMenuItemWindows) {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/deploy_platform_dialog.fxml"));
                VBox vBox = fxmlLoader.load();

                Stage stage = new Stage();
                Scene scene = new Scene(vBox,300,200);
                stage.setScene(scene);
                stage.initOwner(this.stage);

                stage.initModality(Modality.APPLICATION_MODAL);
                stage.setTitle("切换到Windows");
                //   dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
                Button mBTPullConfig = (Button) vBox.lookup("#mBTPullConfig");
                TextField mTFAgentAddress = (TextField) vBox.lookup("#mTFAgentAddress");
                Button mBTSwitch = (Button) vBox.lookup("#mBTSwitch");
                mBTSwitch.setOnAction(event -> {
                    String addressText = mTFAgentAddress.getText();
                    SocketManager.getSocketClient().setHost(addressText)
                            .setPort(6767)
                            .connectServer();
                });

                mBTPullConfig.setOnAction(event -> {
                    Command command = new Command();
                    command.setCommandCode(CommandMethodEnum.SYNC_CR_CONFIG.getCode());
                    deployProcess.sendMessage(command);
                });
                SocketManager.getSocketClient().setOnConnectedListener(new SocketClient.OnConnectedListener() {
                    @Override
                    public void onSuccess(Channel channel) {
                        System.out.println(Thread.currentThread().getName());
                        mBTPullConfig.setDisable(false);
                        JFXSnackbarUtils.show("连接成功" + channel.remoteAddress(), 2000L, vBox);
                    }

                    @Override
                    public void onFail(Exception e) {
                        JFXSnackbarUtils.show("连接失败", 2000L, vBox);

                    }
                });

                syncConfigProcess.setOnSyncConfigListener(new SyncConfigProcess.OnSyncConfigListener() {
                    @Override
                    public void onSyncComplete() {
                        JFXSnackbarUtils.show("同步成功", 2000L, vBox);
                        AlertUtils.showCallBackAlert("重启应用","更新配置完成,重启后生效,是否重启应用?","更新配置完成,重启后生效,是否重启应用?", param -> {
                            if (param == ButtonType.OK) {
                                RestartUtils.restart();
                            }
                            return param;
                        });

                    }

                    @Override
                    public void onSyncFail() {

                    }
                });

                stage.show();
                //  popup.show(stage);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }


        //切换到linux模式
        if (actionEvent.getSource() == mMenuItemLinux) {
            JFXSnackbarUtils.show("切换到linux", 2000L, anchorPaneRoot);

        }
    }

    public void onTestAction(ActionEvent actionEvent) {

        //  ("提示", "部署完成正在启动Tomcat，是否删除ROOT.war？", "部署完成正在启动Tomcat，是否删除ROOT.war？");
        //showEditWindow();
        double height = anchorPaneRoot.getHeight();
        double flowPaneCfgRootHeight = mFlowPaneCfgRoot.getHeight();
        JFXSnackbarUtils.show(flowPaneCfgRootHeight + "", 2000L, anchorPaneRoot);

    }

    public void onJenkinsAction(ActionEvent actionEvent) {
        if (actionEvent.getSource() == mBTJenkinsBuild) {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/JenkinsBuildWindow.fxml"));
            Parent jenkinsParent = null;
            try {
                jenkinsParent = fxmlLoader.load();
                Scene jenkinsWindowScene = new Scene(jenkinsParent);
                Stage jenkinsWindowStage = new Stage();
                jenkinsWindowStage.setTitle("Jenkins构建");
                jenkinsWindowStage.setScene(jenkinsWindowScene);
                jenkinsWindowStage.initStyle(StageStyle.UTILITY);
                jenkinsWindowStage.initModality(Modality.WINDOW_MODAL);

                //anotherStage.centerOnScreen();
                jenkinsWindowStage.initOwner(stage);
                JenkinsBuildController controller = fxmlLoader.getController();
                controller.setStage(jenkinsWindowStage);
                jenkinsWindowStage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 配置文件编辑 页面跳转
     *
     * @param event
     */
    public void onEditAction(ActionEvent event) {
        if (event.getSource() == mBTCmCfg) {
            //  showEditWindow(mTFWarUnPath.getText() + "\\ROOT\\WEB-INF\\classes\\config\\cm.cfg.xml", "cm.cfg.xml");
            // showEditWindow("F:\\JavaWeb\\ThinkWin-Code\\thinkwin-cr\\target\\classes\\config\\cm.cfg.xml");
            showEditWindowV2(mTFWarUnPath.getText() + "\\ROOT\\WEB-INF\\classes\\config\\cm.cfg.xml", "cm.cfg.xml");
        }
        if (event.getSource() == mBTIntegrationCfg) {
            //  showEditWindow(mTFWarUnPath.getText() + "\\ROOT\\WEB-INF\\classes\\config\\integration.cfg.xml", "integration.cfg.xml");
            //  showEditWindow("F:\\JavaWeb\\ThinkWin-Code\\thinkwin-cr\\target\\classes\\config\\integration.cfg.xml");
            showEditWindowV2(mTFWarUnPath.getText() + "\\ROOT\\WEB-INF\\classes\\config\\integration.cfg.xml", "integration.cfg.xml");
        }
        if (event.getSource() == mBTSpring) {
            // showEditWindow(mTFWarUnPath.getText() + "\\ROOT\\WEB-INF\\classes\\config\\spring\\spring.properties", "spring.properties");
            //  showEditWindow("F:\\JavaWeb\\ThinkWin-Code\\thinkwin-cr\\target\\classes\\config\\integration.cfg.xml");
            showEditWindowV2(mTFWarUnPath.getText() + "\\ROOT\\WEB-INF\\classes\\config\\spring\\spring.properties", "spring.properties");
        }
        if (event.getSource() == mBTPublishCfg) {
            //  showEditWindow(mTFWarUnPath.getText() + "\\ROOT\\WEB-INF\\classes\\config\\publish.cfg.xml", "publish.cfg.xml");
            //  showEditWindow("F:\\JavaWeb\\ThinkWin-Code\\thinkwin-cr\\target\\classes\\config\\integration.cfg.xml");
            showEditWindowV2(mTFWarUnPath.getText() + "\\ROOT\\WEB-INF\\classes\\config\\publish.cfg.xml", "publish.cfg.xml");
        }

    }

    /**
     * 显示编辑窗口, 代码高亮 和 搜索采用codemirror  基于 javafx webView实现。
     *
     * @param path  文件路径
     * @param title 标题
     */
    private void showEditWindowV2(String path, String title) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainController.class.getResource("/fxml/WebViewEditor.fxml"));

            Parent root = fxmlLoader.load();
            VBox vBox = (VBox) root;
            WebView mWebViewEditor = (WebView) root.lookup("#mWebView");
            TextField mTFFind = (TextField) root.lookup("#mTFFind");
            BorderPane borderPane = (BorderPane) root.lookup("#borderPane");
            VBox mVBoxInput = (VBox) root.lookup("#mVBoxInput");
            GridPane mGridPane = (GridPane) root.lookup("#mGridPane");

            double height = Utils.getScreenHeight();
            double width = Utils.getScreenWidth();

            Scene scene = new Scene(root, 1270, height * 0.9);
            Stage htmlEditorStage = new Stage();
            htmlEditorStage.setScene(scene);
            htmlEditorStage.setTitle(title);
            htmlEditorStage.setWidth(1270);
            htmlEditorStage.setHeight(height * 0.9);
            htmlEditorStage.setFullScreen(false);
            scene.getStylesheets().addAll(MainController.class.getResource("/css/tools-css.css").toExternalForm());
            //定义一个模式窗口，阻止事件传递到其整个所有者窗口层次结构。
            htmlEditorStage.initModality(Modality.WINDOW_MODAL);
            htmlEditorStage.initStyle(StageStyle.UTILITY);
            htmlEditorStage.initOwner(stage);

            WebXmlEditorController controller = fxmlLoader.getController();
            controller.setStage(htmlEditorStage);
            controller.setFilePath(path);

            //创建的时候重新设置高度， 解决有时候 webview高度获取fxml的高度问题
            vBox.setPrefHeight(height * 0.9);
            borderPane.setPrefHeight(height * 0.9);
            mWebViewEditor.setPrefHeight(height * 0.9);
            htmlEditorStage.show();

            //创建窗口时将预置的侧边隐藏
            double mVBoxInputWidth = mVBoxInput.getWidth();
            //重新给窗口设置宽度
            // htmlEditorStage.setWidth(htmlEditorStage.getWidth() - mVBoxInputWidth);
            htmlEditorStage.setWidth(htmlEditorStage.getWidth() - (htmlEditorStage.getWidth() - mGridPane.getColumnConstraints().get(0).getPrefWidth()));
            //将GridPane的第二个Column宽度设置为0 隐藏掉
            mGridPane.getColumnConstraints().get(1).setMaxWidth(0);
            //将侧边布局隐藏掉
            mVBoxInput.setManaged(false);
            mVBoxInput.setVisible(false);


            //将窗体显示在主应用中间
            centerScreenForApplication(htmlEditorStage);

            //  mWebViewEditor.setPrefHeight(vBox.getHeight());
            /*将子布局的首选宽高和父布局一样大*/
            borderPane.prefHeightProperty().bind(vBox.heightProperty());
            borderPane.prefWidthProperty().bind(vBox.widthProperty());
            /*将底部 其他布局显示出来 显示出来 */
            mWebViewEditor.prefHeightProperty().bind(vBox.heightProperty().subtract(mTFFind.heightProperty()));
            //将WebView的宽度设置为何父布局一样大
            mWebViewEditor.prefWidthProperty().bind(vBox.widthProperty());


            double x = stage.getX();
            double y = stage.getY();

            System.out.println("getX():" + x + ",getY():" + y);
            //窗口显示位置x
            double screen_x = stage.getX() + stage.getWidth();
            double screen_y = stage.getY() + stage.getHeight();
            System.out.println("screen_x:" + screen_x + ",screen_y:" + screen_y);

            int taskbarheight = Toolkit.getDefaultToolkit().getScreenSize().height
                    - GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().height;

            System.out.println(taskbarheight);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 将窗体显示在主程序窗体内
     *
     * @param centerStage 窗体stage
     */
    private void centerScreenForApplication(Stage centerStage) {
        //获取任务栏高度
        int taskbarheight = Toolkit.getDefaultToolkit().getScreenSize().height
                - GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().height;
        //窗口显示位置x
        double screen_x = stage.getX() + stage.getWidth();
        double screen_y = stage.getY() + stage.getHeight();
        //屏幕高度和宽度
        double height = Utils.getScreenHeight();
        double width = Utils.getScreenWidth();
        System.out.println("height:" + height + ",width:" + width);
        //获取窗口中心距离 屏幕顶部 Y轴位置
        double half_Y = (stage.getY() + stage.getHeight() / 2);
        double half_X = (stage.getX() + stage.getWidth() / 2);
        //获取屏幕Y轴中心位置
        double half_ScreenY = height / 2;
        //获取编辑框1/2高度
        double edit_Y = height * 0.9 / 2;
        //获取窗口距离底部Y轴距离
        double half_bottomY = height - half_Y;

        System.out.println("half_Y:" + half_Y + ",half_ScreenY:" + half_ScreenY + ",edit_Y:" + edit_Y + ",half_bottomY:" + half_bottomY);

        //获取弹出窗口的y 和x 位置
        double centerStageY = centerStage.getY();
        double centerStageX = centerStage.getX();
        //获取弹出窗口的宽 和高
        double centerStageWidth = centerStage.getWidth();
        double centerStageHeight = centerStage.getHeight();


        log.info("centerStageWidth:" + centerStageWidth);
        log.info("centerStageHeight:" + centerStageHeight);
        //判断当 弹出的窗体宽度的1/2大于父窗体中心距离屏幕
        /**
         * ( width - half_X) >= centerStageWidth/2 判断屏幕宽度减去父窗体中心距离屏幕左边的宽度 大于 弹出窗体 1/2 宽度
         * (height - half_Y - taskbarheight )  判断屏幕高度减去 父窗体中心距离屏幕顶部的高度 大于 弹出窗体 1/2 高度
         *
         * half_X>= centerStageWidth/2   判断父窗体中心点宽度 大于 弹出窗体 1/2 宽度
         *
         * half_Y>=centerStageHeight/2  判断父窗体中心点高度 大于 弹出窗体 1/2 高度
         */
        if ((width - half_X) >= centerStageWidth / 2 && (height - half_Y - taskbarheight) >= centerStageHeight / 2 && half_X >= centerStageWidth / 2 && half_Y >= centerStageHeight / 2) {
            //将弹出窗体 在父窗体上面居中显示
            centerStage.setX(half_X - centerStage.getWidth() / 2);
            centerStage.setY(half_Y - centerStage.getHeight() / 2);

        } else {
            //将 弹出窗体不做处理。 默认由屏幕中间弹出
            centerStage.setX(width / 2 - centerStage.getWidth() / 2);
            centerStage.setY(height / 2 - centerStage.getHeight() / 2);
        }


    /*    //当窗口的中心距离底部或者顶部的高度不足 编辑窗口的 一半高度时，将编辑窗口居中显示
        if (screen_x >= width || half_Y <= edit_Y || (half_bottomY - taskbarheight) <= edit_Y) {

        } else {
            centerStage.setX(stage.getWidth() / 2 + stage.getX() - centerStage.getWidth() / 2);
            centerStage.setY(stage.getHeight() / 2 + stage.getY() - centerStage.getHeight() / 2);
        }*/

    }

    public void onDownloadAction(ActionEvent event) {

        if (event.getSource() == mBTDownload) {
            PropertyUtils propertyUtils = new PropertyUtils(Config.JenkinsPropertiesFileName);
            propertyUtils.getConfiguration2Properties();
            String user = propertyUtils.getConfigurationPropertyStringByKey("jenkins.user");
            String pwd = propertyUtils.getConfigurationPropertyStringByKey("jenkins.pwd");
            if (!user.isEmpty() && !pwd.isEmpty()) {
                Stage progress = ProgressUtils.createProgress(stage);
                progress.show();
                ThreadPoolManager.getInstance().execute(() -> {
                    boolean isLogin = ProjectBuild.login(user.trim(), pwd.trim());
                    Platform.runLater(() -> {
                        if (!isLogin) {
                            log.warn("失败");
                            progress.close();
                            createJenkinsLoginWindow();
                            JenkinsLoginController.setOnSuccessListener(new JenkinsLoginController.OnSuccessListener() {
                                @Override
                                public void onSuccess() {
                                    createDownloadWindow();
                                }

                                @Override
                                public void onFail() {

                                }
                            });
                        } else {
                            progress.close();
                            createDownloadWindow();
                        }
                    });
                });
            }
        }

    }

    /**
     * 创建下载窗体
     */
    private void createDownloadWindow() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/DownloadInfoWindow.fxml"));
        Parent downloadParent = null;
        try {
            downloadParent = fxmlLoader.load();
            Scene downloadScene = new Scene(downloadParent);
            Stage downloadStage = new Stage();
            downloadStage.setTitle("Jenkins构建");
            downloadStage.setScene(downloadScene);
            downloadStage.initStyle(StageStyle.UTILITY);
            downloadStage.initModality(Modality.WINDOW_MODAL);

            //anotherStage.centerOnScreen();
            downloadStage.initOwner(stage);
            DownloadInfoController controller = fxmlLoader.getController();
            controller.setStage(downloadStage);
            downloadStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void createJenkinsLoginWindow() {

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/jenkins_login_window.fxml"));
        try {
            BorderPane borderPane = fxmlLoader.load();
            Stage loginStage = new Stage();
            loginStage.setTitle("登录");
            Scene loginScene = new Scene(borderPane, 300, 200);
            loginStage.setScene(loginScene);
            loginStage.initModality(Modality.WINDOW_MODAL);
            loginStage.initOwner(stage);
            jenkinsLoginController = fxmlLoader.getController();
            jenkinsLoginController.setStage(loginStage);
            loginStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void onDebugAction(ActionEvent event) {
        try {
            String dbAddress = mTFDBAddress.getText();
            String userName = mTFDBUserName.getText();
            String password = mTFDBPassword.getText();
            List<String> dbList = MySqlHelper.getDBList(dbAddress, userName, password);

            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/debug.fxml"));
            Parent debugParent = fxmlLoader.load();
            Scene debugScene = new Scene(debugParent);
            Stage debugStage = new Stage();
            debugStage.setScene(debugScene);
            debugStage.setTitle("debug");
            debugStage.getIcons().addAll(new Image(this.getClass().getResource("/image/icons8_logo.png").toString()));

            debugStage.initStyle(StageStyle.DECORATED);
            debugStage.initModality(Modality.NONE);
            // debugStage.initOwner(stage);

            DebugController debugController = fxmlLoader.getController();
            debugController.setStage(debugStage);

            debugController.setDBList(dbList);
            debugStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public void onDeployTypeAction(ActionEvent actionEvent) {

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/dialog_deploy.fxml"));
            Parent parent = fxmlLoader.load();
            Scene scene = new Scene(parent, 200, 200.0);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("部署类型选择");
            stage.setResizable(false);
            DialogDeployTypeController controller = fxmlLoader.getController();
            controller.setMainController(this);
            controller.setStage(stage);
            //设置图标
            stage.getIcons().addAll(new Image(this.getClass().getResource("/image/icons8_logo.png").toString()));
            stage.initStyle(StageStyle.DECORATED);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(this.stage);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public void setDeployType(String[] text) {
        this.type = text[1];
        mBTDeployType.setText(text[0]);
    }

    public void onUploadAction(ActionEvent actionEvent) {

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/window_upload_setting.fxml"));
            Parent parent = fxmlLoader.load();
            Scene scene = new Scene(parent);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("上传组件配置");
            stage.setResizable(false);
            UploadSettingController controller = fxmlLoader.getController();
            controller.setStage(stage);
            //设置图标
            stage.getIcons().addAll(new Image(this.getClass().getResource("/image/icons8_logo.png").toString()));
            stage.initStyle(StageStyle.DECORATED);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(this.stage);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private void requestFocus() {
        Platform.runLater(() -> {
            //重新获取焦点
            if (!stage.isFocused()) {
                stage.requestFocus();
            }
        });
    }

    private void setConfig() {
        String appConfigPath = System.getProperty("conf.path");
        DeployConfigModel deployConfigModel = ApplicationContext.getDeployConfigModel();
        deployConfigModel.setHttpdOldChangedPath(appConfigPath + "/httpd_replace.txt");
        deployConfigModel.setHttpdZYFLChangedPath(appConfigPath + "/httpd_zyfl_replace.txt");
        deployConfigModel.setHttpdUpload1TomcatChangedPath(appConfigPath + "/httpd_upload_1tomcat_replace.txt");

        deployConfigModel.setHttpdUploadChangedPath(appConfigPath + "/httpd_upload_replace.txt");
        deployConfigModel.setHttpdIPMChangedPath(appConfigPath + "/httpd_ipm_replace.txt");

        deployConfigModel.setWorkersOldChangedPath(appConfigPath + "/workers_replace.txt");
        deployConfigModel.setWordkersUploadChangedPath(appConfigPath + "/workers_upload_replace.txt");

        String deployType = propertyUtils.getConfigurationPropertyStringByKey("deploy.type");
        if (!deployType.isEmpty()) {
            deployConfigModel.setDeployTypeEnum(DeployTypeEnum.valueOf(deployType));
        }
        //  String cmInstallPath = propertyUtils.getConfigurationPropertyStringByKey("cm.install.path");

        Map<String, String> cmDeployConfigMap = new HashMap<>();
        String warUnPath = mTFWarUnPath.getText();
        File parentFile = new File(warUnPath).getParentFile();
        if (null != parentFile) {
            String tomcatPath = parentFile.getAbsolutePath();
            cmDeployConfigMap.put("cmTomcatCachePath", tomcatPath + "\\work");
            cmDeployConfigMap.put("cmTomcatRootPath", warUnPath + "\\ROOT");
            cmDeployConfigMap.put("cmTomcatStartUpPath", tomcatPath + "\\bin\\startup.bat");
        }
        String cmTomcatServiceName = propertyUtils.getConfigurationPropertyStringByKey("cm.tomcat.service.name");
        cmDeployConfigMap.put("cmTomcatServiceName", cmTomcatServiceName);
        cmDeployConfigMap.put("cmTomcatPort", mTFTomcatPort.getText());
        cmDeployConfigMap.put("cmWarPath", mTFWarPath.getText());
        cmDeployConfigMap.put("cmWarFlag", Utils.getUUID32());
        cmDeployConfigMap.put("cmTomcatExportPath", warUnPath + "\\ROOT");

        cmDeployConfigMap.put("localIp", propertyUtils.getConfigurationPropertyStringByKey("local.ip"));

        cmDeployConfigMap.put("cmDBAddress", mTFDBAddress.getText());
        cmDeployConfigMap.put("cmDBName", mTFDBName.getText());
        cmDeployConfigMap.put("cmDBUserName", mTFDBUserName.getText());
        cmDeployConfigMap.put("cmDBUserPass", mTFDBPassword.getText());

        cmDeployConfigMap.put("cmResourcesPath", mTFResourcePath.getText());
        cmDeployConfigMap.put("cmServerIp", propertyUtils.getConfigurationPropertyStringByKey("cm.server.ip"));
        deployConfigModel.setCmDeployConfigMap(cmDeployConfigMap);

        Map<String, String> zyflDeployConfigMap = new HashMap<>();
        String apacheServiceName = propertyUtils.getConfigurationPropertyStringByKey("apache.service.name");
        zyflDeployConfigMap.put("apacheServiceName", apacheServiceName);
        File parentFile1 = new File(warUnPath).getParentFile();
     /*   String cmInstallPath = new File(warUnPath).getParentFile()

                .getParentFile().getAbsolutePath();*/

        if (null != parentFile1) {
            String cmInstallPath = parentFile1.getParentFile().getAbsolutePath();
            zyflDeployConfigMap.put("apacheHttpdPath", cmInstallPath + "\\apache\\conf\\httpd.conf");
            zyflDeployConfigMap.put("apacheWorkersPath", cmInstallPath + "\\apache\\conf\\workers.properties");
            zyflDeployConfigMap.put("apacheHtdocsPath", cmInstallPath + "\\apache\\htdocs");
        }
        zyflDeployConfigMap.put("zyflWarPath", mTFStaticWarPath.getText());
        zyflDeployConfigMap.put("zyflWarFlag", Utils.getUUID32());

        zyflDeployConfigMap.put("apacheHtdocsFilter", "res");
        deployConfigModel.setZyflDeployConfigMap(zyflDeployConfigMap);

        Map<String, String> uploadDeployConfigMap = new HashMap<>();
        String uploadProjectName = propertyUtils.getConfigurationPropertyStringByKey("upload.tomcat.project.name");
        String uploadExportPath = propertyUtils.getConfigurationPropertyStringByKey("upload.exportWar.path");
        File parentFile2 = new File(uploadExportPath).getParentFile();

        //   String absolutePath = new File(uploadExportPath).getParentFile().getAbsolutePath();
        if (null != parentFile2) {
            String absolutePath = parentFile2.getAbsolutePath();
            uploadDeployConfigMap.put("uploadTomcatStartUpPath", absolutePath + "\\bin\\startup.bat");
            uploadDeployConfigMap.put("uploadTomcatPort", propertyUtils.getConfigurationPropertyStringByKey("upload.tomcat.port"));
            uploadDeployConfigMap.put("uploadWarPath", propertyUtils.getConfigurationPropertyStringByKey("upload.war.path"));
            uploadDeployConfigMap.put("uploadWarFlag", Utils.getUUID32());
            uploadDeployConfigMap.put("uploadTomcatExportPath", uploadExportPath + "\\"+uploadProjectName);
            uploadDeployConfigMap.put("uploadTomcatServiceName", propertyUtils.getConfigurationPropertyStringByKey("upload.tomcat.serviceName"));
            uploadDeployConfigMap.put("uploadTomcatCachePath",  absolutePath + "\\work");
            uploadDeployConfigMap.put("uploadTomcatProjectName",  uploadProjectName);

        }
        uploadDeployConfigMap.put("apacheServerIp", propertyUtils.getConfigurationPropertyStringByKey("apache.server.ip"));

        deployConfigModel.setUploadDeployConfigMap(uploadDeployConfigMap);
    }

    class DeployListener implements ProcessBase.OnDeployProcessorListener {


        @Override
        public void onDeployInit() {
            //重新赋予新的配置
            setConfig();
        }

        @Override
        public void onDeployProcessorStart() {
            log.info("onDeployProcessorStart");
            //修改按钮状态
            setBTDeployStartState(true, "正在部署...");
            setBTStartState(true, "启动");
        }

        @Override
        public void onDeployProcessorEnd() {
            log.info("onDeployProcessorEnd");
            //修改按钮状态
            setBTDeployStartState(false, "一键部署");
            setBTStartState(false, "启动");

            //部署完毕 保存配置
            MainController.this.propertyUtils.setConfigurationProperty("cm.tomcat.port", mTFTomcatPort.getText());
            MainController.this.propertyUtils.setConfigurationProperty("cm.tomcat.war.path", mTFWarPath.getText());
            MainController.this.propertyUtils.setConfigurationProperty("cm.tomcat.exportWar.path", mTFWarUnPath.getText());
            MainController.this.propertyUtils.setConfigurationProperty("cm.config.db.address", mTFDBAddress.getText());
            MainController.this.propertyUtils.setConfigurationProperty("cm.config.db.name", mTFDBName.getText());
            MainController.this.propertyUtils.setConfigurationProperty("cm.config.db.userName", mTFDBUserName.getText());
            MainController.this.propertyUtils.setConfigurationProperty("cm.config.db.userPass", mTFDBPassword.getText());

            MainController.this.propertyUtils.setConfigurationProperty("cm.zyflWar.path", mTFStaticWarPath.getText());
            MainController.this.propertyUtils.setConfigurationProperty("cm.zyfl.exportWar.path", mTFStaticWarUnPath.getText());
            MainController.this.propertyUtils.setConfigurationProperty("cm.config.resourcesPath", mTFResourcePath.getText());


            Platform.runLater(() -> {


                Dialog<String> dialog = new Dialog<>();
                try {
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/deploy_mode_dialog.fxml"));
                    VBox vBox = fxmlLoader.load();

                    DialogController controller = fxmlLoader.getController();
                    controller.setType(2);
                    dialog.getDialogPane().setContent(vBox);

                    CheckBox cm_CheckBox = (CheckBox) vBox.lookup("#mCB_CM");
                    CheckBox upload_CheckBox = (CheckBox) vBox.lookup("#mCB_ZYFL");
                    CheckBox zyfl_CheckBox = (CheckBox) vBox.lookup("#mCB_UPLOAD");

                    dialog.setTitle("删除选择");
                    //    dialog.getDialogPane().setContent(vBox);
                    dialog.setWidth(300);
                    dialog.setHeight(200);

                    dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
                    dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

                    dialog.setResultConverter(param -> {
                        if (param == ButtonType.OK) {
                            controller.deleteWar();
                            dialog.close();
                        }
                        if (param == ButtonType.CANCEL) {
                            dialog.close();
                        }
                        return "";
                    });
                    dialog.showAndWait();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            requestFocus();

        }

        @Override
        public void onDeployProcessSuccess(DeployState deployState) {
            appendText(deployState.getInfo());
            log.info("onDeployProcessSuccess ," + deployState.getInfo());
        }

        @Override
        public void onDeployProcessFail(DeployState deployState) {
            requestFocus();
            log.info("onDeployProcessFail");
            setBTDeployStartState(false, "一键部署");
            setBTStartState(false, "启动");
            TaskEnum taskEnum = deployState.getTaskEnum();
            //AlertUtils.showAlert("错误",taskEnum.toString()+" 部署失败!",deployState.getE());
            AlertUtils.showAlert("错误", " 部署失败!", deployState.getE());
        }

    }

    class ServerControlListener implements ProcessBase.OnServerControlListener {

        @Override
        public void onServerStart() {
            setBTStartState(true, "正在启动");
            appendText("服务正在启动...");
            setConfig();
        }

        @Override
        public void onServerStarted(DeployState deployState) {
            setBTStartState(false, "停止");
            log.info(deployState.getInfo());
            appendText(deployState.getTaskEnum().toString() + deployState.getInfo());
        }

        @Override
        public void onServerStoping() {
            setBTStartState(true, "正在停止");
            appendText("服务正在停止...");
        }

        @Override
        public void onServerStoped(DeployState deployState) {
            setBTStartState(false, "启动");
            appendText(deployState.getTaskEnum().toString() + deployState.getInfo());
        }

        @Override
        public void onServerFail(DeployState deployState) {
            log.info(deployState.getInfo());
            log.info(deployState.getE());
            setBTStartState(false, "启动");
            AlertUtils.showAlert("错误", deployState.getTaskEnum().toString() + " 错误", deployState.getE());
        }
    }
}
