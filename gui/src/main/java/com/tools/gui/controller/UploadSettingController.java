package com.tools.gui.controller;

import com.tools.commons.utils.PropertyUtils;
import com.tools.gui.config.ApplicationConfig;
import com.tools.gui.config.Config;
import com.tools.gui.utils.view.FileChooserUtils;
import com.tools.service.context.ApplicationContext;
import com.tools.socket.bean.FileItemInfo;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import static com.tools.gui.utils.view.FileChooserUtils.showSelectDirectoryChooser;
import static com.tools.gui.utils.view.FileChooserUtils.showSelectFileChooser;


public class UploadSettingController extends BaseController implements Initializable {
    public TextField mTFUploadWarPath;
    public Button mBTUploadWarPath;
    public TextField mTFUploadUnWarPath;
    public Button mBTUploadUnWarPath;
    public TextField mTFUploadTomcatPort;

    public TextField mTFCMServerIp;
    public TextField mTFApacheServerIp;


    public Button mBTSave;
    public Button mBTCancel;
    public TextField mTFUploadTomcatServiceName;
    public TextField mTFComProjectName;
    public GridPane gridPane;
    private Stage stage;
    private PropertyUtils propertyUtils;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        propertyUtils = new PropertyUtils(ApplicationContext.getApplicationConfPath()+"/"+ ApplicationConfig.DEPLOY_CONFIG_FILE_NAME);
        propertyUtils.getConfiguration2Properties();

        mTFUploadWarPath.setText(propertyUtils.getConfigurationPropertyStringByKey("upload.war.path"));
        mTFUploadUnWarPath.setText(propertyUtils.getConfigurationPropertyStringByKey("upload.exportWar.path"));
        mTFUploadTomcatPort.setText(propertyUtils.getConfigurationPropertyStringByKey("upload.tomcat.port"));
        mTFComProjectName.setText(propertyUtils.getConfigurationPropertyStringByKey("upload.tomcat.project.name"));

        mTFCMServerIp.setText(propertyUtils.getConfigurationPropertyStringByKey("cm.server.ip"));
        mTFApacheServerIp.setText(propertyUtils.getConfigurationPropertyStringByKey("apache.server.ip"));
        mTFUploadTomcatServiceName.setText(propertyUtils.getConfigurationPropertyStringByKey("upload.tomcat.serviceName"));
    }



    public void onSelectPathAction(ActionEvent actionEvent) {

        if (actionEvent.getSource()==mBTUploadWarPath) {
            String uploadWarPath = mTFUploadWarPath.getText();
            String filePathStr = "";
            if (null!=uploadWarPath&&!uploadWarPath.isEmpty()){
                File file = new File(uploadWarPath);
                if (file.exists()) {
                    if (file.isDirectory()) {
                        filePathStr = showSelectFileChooser(new FileChooser.ExtensionFilter("war(*.war)", "*.war"), file.getAbsolutePath(), stage);
                    }
                    if (file.isFile()){
                        filePathStr = showSelectFileChooser(new FileChooser.ExtensionFilter("war(*.war)", "*.war"), file.getParentFile().getAbsolutePath(), stage);
                    }
                }else {
                    filePathStr = showSelectFileChooser(new FileChooser.ExtensionFilter("war(*.war)", "*.war"), file.getParentFile().getAbsolutePath(), stage);
                }
            }else {
                filePathStr = showSelectFileChooser(new FileChooser.ExtensionFilter("war(*.war)", "*.war"), "C:\\Downloads", stage);
            }
            if (filePathStr.trim().equals("")) {
                return;
            }
            mTFUploadWarPath.setText(filePathStr);
        }



        if (actionEvent.getSource()==mBTUploadUnWarPath) {
            RemoteFileBrowserController.FileFilter fileFilter = new RemoteFileBrowserController.FileFilter(FileItemInfo.FILTER_DIRECTORY_ONLY);

            FileChooserUtils.showRemoteFileBrowserWindow(stage,fileFilter,fileItemInfo -> {
                String filePath = fileItemInfo.getAbsolutePath();
                mTFUploadUnWarPath.setText(filePath);
            });

        }
    }


    public void onAction(ActionEvent actionEvent) {
        if (actionEvent.getSource()==mBTSave) {
            propertyUtils.setConfigurationProperty("upload.war.path",mTFUploadWarPath.getText());
            propertyUtils.setConfigurationProperty("upload.exportWar.path",mTFUploadUnWarPath.getText());
            propertyUtils.setConfigurationProperty("upload.tomcat.port",mTFUploadTomcatPort.getText());
            propertyUtils.setConfigurationProperty("cm.server.ip",mTFCMServerIp.getText());
            propertyUtils.setConfigurationProperty("apache.server.ip",mTFApacheServerIp.getText());
            propertyUtils.setConfigurationProperty("upload.tomcat.serviceName",mTFUploadTomcatServiceName.getText());
            propertyUtils.setConfigurationProperty("upload.tomcat.project.name",mTFComProjectName.getText());
            stage.close();
        }
        if (actionEvent.getSource()==mBTCancel) {
            stage.close();
        }
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }
}
