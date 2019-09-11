package com.tools.gui.controller;

import com.tools.commons.utils.PropertyUtils;
import com.tools.commons.utils.Utils;
import com.tools.gui.config.Config;
import com.tools.service.service.command.factory.CommandFactory;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

import static com.tools.gui.utils.view.RestartUtils.restart;


public class CRSettingController {
    //war地址
    public TextField mTFWarPath;
    //war解压地址
    public TextField mTFWarUnPath;
    //数据库地址
    public TextField mTFDBAddress;
    //数据库名字
    public TextField mTFDBName;
    //数据库用户名
    public TextField mTFDBUserName;
    //数据库密码
    public TextField mTFDBPassword;
    //数据库资源文件路径
    public TextField mTFResourcePath;
    //tomcat端口
    public TextField mTFTomcatPort;
    //选择war地址
    public Button mBTSelectWar;
    //选择war解压地址
    public Button mBTSelectUnPath;
    //选择资源路径地址
    public Button mBTSelectResource;

    public Button mBTSave;
    public Button mBTCancel;
    public Button mBTReset;
    public RadioButton mRBService;
    public RadioButton mRBConsole;
    public TextField mTFServerName;
    public Label mLBServerName;
    //Redis配置
    public TextField mTFRedisAddress;
    public TextField mTFRedisPass;
    //开机启动
    public CheckBox mCBBootStart;
    //解压类型选择
    public RadioButton mRBZIPJava;
    public RadioButton mRBZIPHaoZIP;
    //文件删除选择
    public RadioButton mRBDelJava;
    public RadioButton mRBDelCommand;
    //本机ip配置
    public RadioButton mRBAutoIp;
    public RadioButton mRBInput;
    public TextField mTFInputIP;
    //apache 服务名
    public TextField mTFApacheServerName;

    private Stage mStage;

    private static final Logger log = LoggerFactory.getLogger(CRSettingController.class);
    private PropertyUtils propertyUtils;

    public void setStage(Stage crSettingStage) {
        this.mStage = crSettingStage;
        //监听窗口初始化动作。每次恢复默认设置时加载默认设置
        mStage.setOnShown(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                EventType<WindowEvent> eventType = event.getEventType();
                readCRConfig();
            }
        });

    }

    public void onButtonAction(ActionEvent actionEvent) {

        if (actionEvent.getSource() == mBTSelectWar) {
            String filePathStr = showSelectFileChooser(new FileChooser.ExtensionFilter("war(*.war)", "*.war"), "");
            if (filePathStr.trim().equals("")) {
                return;
            } //设置war路径
            mTFWarPath.setText(filePathStr);
            log.info("war:" + filePathStr);
        }

        if (actionEvent.getSource() == mBTSelectUnPath) {
            String dirPathStr = showSelectDirectoryChooser(null, "");
            if (dirPathStr.trim().equals("")) {
                return;
            }
            mTFWarUnPath.setText(dirPathStr);
            log.info("UnPath:" + dirPathStr);
        }
        //选择资源路径
        if (actionEvent.getSource() == mBTSelectResource) {
            String selectResourcePath = showSelectDirectoryChooser(null, "");
            if (selectResourcePath.trim().equals("")) {
                return;
            }
            String res = "\\apache\\htdocs\\res";
            log.info("资源文件路径转换前：" + selectResourcePath);
            //    selectResourcePath = selectResourcePath + "\\";
            boolean contains = selectResourcePath.contains("\\apache\\htdocs\\res");
            if (contains) {
                String[] split = selectResourcePath.split("\\\\apache\\\\htdocs\\\\res");
                selectResourcePath = split[0] + res.replace("\\", "/");
                mTFResourcePath.setText(selectResourcePath);
                log.info("资源文件路径转换后：" + selectResourcePath);
            }
        }
        //保存
        if (actionEvent.getSource() == mBTSave) {

            String warPath = mTFWarPath.getText();
            String warUnPath = mTFWarUnPath.getText();
            if (warPath.equals("") || warUnPath.equals("")) {
                showAlert("错误", "路径没有选择正确", "没有选择War路径和解压路径");
                return;
            }
            String dbAddressStr = mTFDBAddress.getText();
            String dbNameStr = mTFDBName.getText();
            String dbUserNameStr = mTFDBUserName.getText();
            String dbPassWordStr = mTFDBPassword.getText();
            //判断数据库配置输入
            if (dbAddressStr.equals("") && dbNameStr.equals("") && dbUserNameStr.equals("") && dbPassWordStr.equals("")) {
                showAlert("错误", "数据库配置不完整", "数据库地址、名字、用户名没有输入");
                return;
            }
            String resourcePathStr = mTFResourcePath.getText();


            if (resourcePathStr.trim().equals("")) {
                showAlert("错误", "资源路径为空", "资源路径没有输入");
                return;
            }
            //查找进程
            String tomcatPort = mTFTomcatPort.getText();
            if (tomcatPort.equals("")) {
                showAlert("错误", "tomcat端口为空", "没有输入tomcat端口！");
                return;
            }

            String serverNameStr = mTFServerName.getText();
            if (mRBService.isSelected()) {
                if (null == serverNameStr || serverNameStr.equals("")) {
                    showAlert("错误", "服务名不能为空", "服务名不能为空");
                    return;
                }
            }
            String redisAddressStr = mTFRedisAddress.getText();
            String redisPassStr = mTFRedisPass.getText();
            //如果Redis地址为空，则不存Redis配置
            if (null != redisAddressStr || !redisAddressStr.equals("")) {
                propertyUtils.setOrderedProperty("cm.config.redis.address", redisAddressStr);
                propertyUtils.setOrderedProperty("cm.config.redis.pass", redisPassStr);
            }
            //开机启动设置
            Boolean bootStart = propertyUtils.getOrderedPropertyBooleanByKey("tools.config.bootStart");
            if (mCBBootStart.isSelected() && !bootStart) {
                //设置开机启动
              //  CmdUtils.cmdSetBootStart();
                CommandFactory.getCommand().cmdSetBootStartBySchtasks();
                propertyUtils.setOrderedProperty("tools.config.bootStart", "true");
            } else if (!mCBBootStart.isSelected() && bootStart) {
                //取消开机启动
               // CmdUtils.cmdCancelBootStart();
                CommandFactory.getCommand().cmdCancelBootStartBySchtasks();
                propertyUtils.setOrderedProperty("tools.config.bootStart", "false");
            }

            //保存配置
            propertyUtils.setOrderedProperty("cm.tomcat.war.path", warPath);
            propertyUtils.setOrderedProperty("cm.tomcat.exportWar.path", warUnPath);
            propertyUtils.setOrderedProperty("cm.config.db.address", dbAddressStr);
            propertyUtils.setOrderedProperty("cm.config.db.name", dbNameStr);
            propertyUtils.setOrderedProperty("cm.config.db.userName", dbUserNameStr);
            propertyUtils.setOrderedProperty("cm.config.db.userPass", dbPassWordStr);
            propertyUtils.setOrderedProperty("cm.config.resourcesPath", resourcePathStr);
            propertyUtils.setOrderedProperty("cm.tomcat.port", tomcatPort);
            propertyUtils.setOrderedProperty("cm.tomcat.service.name", serverNameStr);

            //服务类型配置
            if (mRBConsole.isSelected()) {
                propertyUtils.setOrderedProperty("cm.tomcat.start.type", "console");
                //选择控制台时，将服务置为空
                //propertyUtils.setOrderedProperty("TomcatServiceName", "");
            }
            if (mRBService.isSelected()) {
                propertyUtils.setOrderedProperty("cm.tomcat.start.type", "service");
            }

            //解压方式配置

            if (mRBZIPJava.isSelected()) {
                propertyUtils.setOrderedProperty("deploy.exportWar.type", "");
            }
            if (mRBZIPHaoZIP.isSelected()) {
                propertyUtils.setOrderedProperty("deploy.exportWar.type", "HaoZip");
            }

            if (mRBDelJava.isSelected()) {
                propertyUtils.setOrderedProperty("deploy.deleteFile.type", "");
            }
            if (mRBDelCommand.isSelected()) {
                propertyUtils.setOrderedProperty("deploy.deleteFile.type", "Command");
            }

            //本机ip配置
            if (mRBAutoIp.isSelected()) {
                String localIP = Utils.getLocalIP();
                propertyUtils.setOrderedProperty("local.ip.type", "auto");
                propertyUtils.setOrderedProperty("local.ip", localIP);
            }
            if (mRBInput.isSelected()) {
                propertyUtils.setOrderedProperty("local.ip.type", "input");
                propertyUtils.setOrderedProperty("local.ip", mTFInputIP.getText());
            }

            //保存Apache服务名
            String apacheServerName = mTFApacheServerName.getText();
            propertyUtils.setOrderedProperty("apache.service.name", apacheServerName);


            log.info("配置保存成功!");
            mStage.close();
            //重启应用读取配置
            restart();

        }
        if (actionEvent.getSource() == mBTCancel) {
            mStage.close();
        }
        if (actionEvent.getSource() == mBTReset) {
            //恢复默认设置
           // propertyUtils.defaultConfig();
            //重新关闭窗口重新读取数据
            mStage.close();
            mStage.show();
        }
    }



    private void showAlert(String title, String headerText, String contentText) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Alert alert = new Alert(Alert.AlertType.INFORMATION, contentText, ButtonType.CLOSE);
                alert.setHeaderText(headerText);
                alert.setTitle(title);
                Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
                stage.getIcons().addAll(new Image(this.getClass().getResource("/image/icons.png").toString()));
                //  alert.initOwner();
                alert.show();
            }
        });
    }

    /**
     * 选择文件
     *
     * @param filter      过滤器
     * @param defaultPtah 默认展示的路径
     * @return 返回选择文件的路径
     */
    private String showSelectFileChooser(FileChooser.ExtensionFilter filter, String defaultPtah) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("选择文件");
        //设置选择的文件过滤器
        //fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("war(*.war)", "*.war"));
        fileChooser.getExtensionFilters().addAll(filter);
        //设置默认显示的文件目录
        // fileChooser.setInitialDirectory(new File("F:\\QQdownload"));
        File defaultFile = new File(defaultPtah);
        if (defaultFile.exists()) {
            fileChooser.setInitialDirectory(defaultFile);
        }
        //获取选择的文件
        File selectFile = fileChooser.showOpenDialog(mStage);

        return null == selectFile ? "" : selectFile.getAbsoluteFile().toString();
    }

    /**
     * 选择目录
     *
     * @param filter      过滤器
     * @param defaultPtah 默认展示的路径
     * @return 返回选择文件的路径
     */
    private String showSelectDirectoryChooser(FileChooser.ExtensionFilter filter, String defaultPtah) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("选择目录");
        //设置选择的文件过滤器
        // fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("war(*.war)", "*.war"));
        //设置默认显示的文件目录
        //  directoryChooser.setInitialDirectory(new File("F:\\"));
        if (!defaultPtah.trim().isEmpty()) {
            directoryChooser.setInitialDirectory(new File(defaultPtah));
        }
        //获取选择的文件
        File selectDirectory = directoryChooser.showDialog(mStage);
        return null == selectDirectory ? "" : selectDirectory.getAbsoluteFile().toString();
    }

    @FXML
    public void initialize() {
        log.info("弹窗初始化！");

        propertyUtils = new PropertyUtils(Config.CRConfigFileName);
        propertyUtils.getOrderedProperties();
        
        ToggleGroup toggleGroup = new ToggleGroup();
        mRBService.setToggleGroup(toggleGroup);
        mRBConsole.setToggleGroup(toggleGroup);
        mRBConsole.setSelected(true);

        ToggleGroup zipToggleGroup = new ToggleGroup();
        mRBZIPJava.setToggleGroup(zipToggleGroup);
        mRBZIPHaoZIP.setToggleGroup(zipToggleGroup);
        mRBZIPJava.setSelected(true);

        ToggleGroup delToggleGroup = new ToggleGroup();
        mRBDelJava.setToggleGroup(delToggleGroup);
        mRBDelCommand.setToggleGroup(delToggleGroup);
        mRBDelJava.setSelected(true);

        ToggleGroup ipToggleGroup = new ToggleGroup();
        mRBAutoIp.setToggleGroup(ipToggleGroup);
        mRBInput.setToggleGroup(ipToggleGroup);
        mRBInput.setSelected(true);

/*        mCBBootStart.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                log.info(newValue+"");
            }
        });*/

        toggleGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            @Override
            public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
/*
                if (newValue == mRBService) {
                    mTFServerName.setVisible(true);
                    mLBServerName.setVisible(true);
                } else {
                    mTFServerName.setVisible(false);
                    mLBServerName.setVisible(false);
                }*/

            }
        });

/*        if (mRBService.isSelected()) {
            mTFServerName.setVisible(true);
            mLBServerName.setVisible(true);
        } else {
            mTFServerName.setVisible(false);
            mLBServerName.setVisible(false);
        }*/


        ipToggleGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            @Override
            public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
                if (newValue == mRBAutoIp) {
                    mTFInputIP.setText(Utils.getLocalIP());
                }

                if (newValue==mRBInput){
                    mTFInputIP.setText(propertyUtils.getOrderedPropertyStringByKey("local.ip"));
                }
            }
        });

//读取默认配置
        readCRConfig();
    }

    private void readCRConfig() {
        //初始化读取配置信息
        mTFWarPath.setText(propertyUtils.getOrderedPropertyStringByKey("cm.tomcat.war.path"));
        mTFWarUnPath.setText(propertyUtils.getOrderedPropertyStringByKey("cm.tomcat.exportWar.path"));
        mTFDBAddress.setText(propertyUtils.getOrderedPropertyStringByKey("cm.config.db.address"));
        mTFDBName.setText(propertyUtils.getOrderedPropertyStringByKey("cm.config.db.name"));
        mTFDBUserName.setText(propertyUtils.getOrderedPropertyStringByKey("cm.config.db.userName"));
        mTFDBPassword.setText(propertyUtils.getOrderedPropertyStringByKey("cm.config.db.userPass"));
        mTFResourcePath.setText(propertyUtils.getOrderedPropertyStringByKey("cm.config.resourcesPath"));
        mTFTomcatPort.setText(propertyUtils.getOrderedPropertyStringByKey("cm.tomcat.port"));

        mTFServerName.setText(propertyUtils.getOrderedPropertyStringByKey("cm.tomcat.service.name"));

        String startType = propertyUtils.getOrderedPropertyStringByKey("cm.tomcat.start.type");

        if (startType.equals("console")) {
            mRBConsole.setSelected(true);
            mRBService.setSelected(false);
        } else if (startType.equals("service")) {
            mRBConsole.setSelected(false);
            mRBService.setSelected(true);
        }

        String unZipType = propertyUtils.getOrderedPropertyStringByKey("deploy.exportWar.type");
        if (unZipType.equals("")) {
            mRBZIPJava.setSelected(true);
            mRBZIPHaoZIP.setSelected(false);
        } else if (unZipType.equals("HaoZip")) {
            mRBZIPHaoZIP.setSelected(true);
            mRBZIPJava.setSelected(false);
        }
        String delFileType = propertyUtils.getOrderedPropertyStringByKey("deploy.deleteFile.type");
        if (delFileType.equals("")) {
            mRBDelJava.setSelected(true);
            mRBDelCommand.setSelected(false);
        } else if (delFileType.equals("Command")) {
            mRBDelCommand.setSelected(true);
            mRBDelJava.setSelected(false);
        }

        //本机ip设置
        String localIPType = propertyUtils.getOrderedPropertyStringByKey("local.ip.type");

        if (localIPType.equals("auto")) {
            mRBAutoIp.setSelected(true);
            mTFInputIP.setText(Utils.getLocalIP());
        }

        if (localIPType.equals("input")) {
            mRBInput.setSelected(true);
            mTFInputIP.setText(propertyUtils.getOrderedPropertyStringByKey("local.ip"));
        }
        //读取Apache服务名
        mTFApacheServerName.setText(propertyUtils.getOrderedPropertyStringByKey("apache.service.name"));

        //设置Redis信息
        boolean isEmpty = propertyUtils.getOrderedPropertyStringByKey("cm.config.redis.address").equals("");
        if (!isEmpty) {
            mTFRedisPass.setText(propertyUtils.getOrderedPropertyStringByKey("cm.config.redis.pass"));
            mTFRedisAddress.setText(propertyUtils.getOrderedPropertyStringByKey("cm.config.redis.address"));
        }

        if (propertyUtils.getOrderedPropertyBooleanByKey("tools.config.bootStart")) {
            mCBBootStart.setSelected(true);
        } else {
            mCBBootStart.setSelected(false);
        }
    }

}
