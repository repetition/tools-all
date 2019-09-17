package com.tools.gui.controller;

import com.tools.commons.utils.FileUtils;
import com.tools.commons.utils.StringUtils;
import com.tools.gui.utils.view.AlertUtils;
import com.tools.service.context.ApplicationContext;
import com.tools.service.model.DeployConfigModel;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class ZYFLSettingController implements Initializable {
    private final static Logger log = LoggerFactory.getLogger(ZYFLSettingController.class);
    public Button mBTSave;
    public Button mBTCancel;
    public TextArea mTAEdit;
    public TextArea mTAZYFLEdit;
    public TextArea mTAUploadEdit;

    public Label mLabel;
    public Label mLabelZYFL;
    public Label mLabelUpload;

    public Label mLabelUploadIPM;
    public TextArea mTAUploadIPMEdit;
    public GridPane mGridPaneRoot;

    public Label mLabelUpload1Tomcat;
    public TextArea mTAUpload1TomcatEdit;

    private Stage stage;

    private int type = 3;
    private DeployConfigModel deployConfigModel;

    public void onAction(ActionEvent actionEvent) {
        String confPath = System.getProperty("conf.path");
        if (actionEvent.getSource() == mBTSave) {
            //普通旧版Apache配置
            String taEditText = mTAEdit.getText();
            //资源分离apache配置
            String tazyflEditText = mTAZYFLEdit.getText();
            //普通上传组件Apache配置
            String uploadEditText = mTAUploadEdit.getText();

            String uploadIPMEditText = mTAUploadIPMEdit.getText();

            String upload1TomcatEditText = mTAUpload1TomcatEdit.getText();


            //httpd配置
            if (type == 0) {
                if (!StringUtils.isEmpty(taEditText) && !StringUtils.isEmpty(tazyflEditText)
                        && !StringUtils.isEmpty(uploadEditText) && !StringUtils.isEmpty(uploadIPMEditText) && !StringUtils.isEmpty(upload1TomcatEditText)) {
                    FileUtils.saveFile(taEditText, deployConfigModel.getHttpdOldChangedPath());
                    FileUtils.saveFile(tazyflEditText, deployConfigModel.getHttpdZYFLChangedPath());
                    FileUtils.saveFile(upload1TomcatEditText, deployConfigModel.getHttpdUpload1TomcatChangedPath());
                    FileUtils.saveFile(uploadEditText, deployConfigModel.getHttpdUploadChangedPath());
                    FileUtils.saveFile(uploadIPMEditText, deployConfigModel.getHttpdIPMChangedPath());
                    stage.close();
                }else {
                    AlertUtils.showAlert("错误", "将所有输入框输入", "");
                }
            }
            //workers配置
            if (type == 1) {

                if (!StringUtils.isEmpty(taEditText) && !StringUtils.isEmpty(tazyflEditText)
                        && !StringUtils.isEmpty(uploadEditText)) {
                    FileUtils.saveFile(taEditText, deployConfigModel.getWorkersOldChangedPath());
                    FileUtils.saveFile(tazyflEditText, deployConfigModel.getWorkersOldChangedPath());
                    FileUtils.saveFile(uploadEditText, deployConfigModel.getWordkersUploadChangedPath());
                    stage.close();
                }else {
                    AlertUtils.showAlert("错误", "将所有输入框输入", "");
                }
            }
        }
        if (actionEvent.getSource() == mBTCancel) {
            stage.close();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        deployConfigModel = ApplicationContext.getDeployConfigModel();
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }


    public void setType(int type) {
        this.type = type;
        String confPath = System.getProperty("conf.path");

        if (type == 0) {

            mLabel.setText("Apache Httpd.conf(普通项目)");
            mLabelZYFL.setText("Apache Httpd.conf(资源分离项目)");
            mLabelUpload.setText("Apache Httpd.conf(上传组件项目)");

            File httpd_replace_file = new File(deployConfigModel.getHttpdOldChangedPath());
            File httpd_zyfl_replace_file = new File(deployConfigModel.getHttpdZYFLChangedPath());
            File httpd_upload_1tomcat_replace_file = new File(deployConfigModel.getHttpdUpload1TomcatChangedPath());
            File httpd_upload_replace_file = new File(deployConfigModel.getHttpdUploadChangedPath());
            File httpd_upload_IPM_replace_file = new File(deployConfigModel.getHttpdIPMChangedPath());

            if (httpd_replace_file.exists()) {
                mTAEdit.setText(FileUtils.readFile(httpd_replace_file.getAbsolutePath()));
            }
            if (httpd_zyfl_replace_file.exists()) {
                mTAZYFLEdit.setText(FileUtils.readFile(httpd_zyfl_replace_file.getAbsolutePath()));
            }
            if (httpd_upload_replace_file.exists()) {
                mTAUploadEdit.setText(FileUtils.readFile(httpd_upload_replace_file.getAbsolutePath()));
            }
            if (httpd_upload_1tomcat_replace_file.exists()) {
                mTAUpload1TomcatEdit.setText(FileUtils.readFile(httpd_upload_1tomcat_replace_file.getAbsolutePath()));
            }
            if (httpd_upload_IPM_replace_file.exists()) {
                mTAUploadIPMEdit.setText(FileUtils.readFile(httpd_upload_IPM_replace_file.getAbsolutePath()));
            }
        }

        if (type == 1) {

            mLabel.setText("Apache workers.properties(普通项目)");
            mLabelZYFL.setText("Apache workers.properties(资源分离项目)");
            mLabelUpload.setText("Apache workers.properties(上传组件项目)");

            mTAUploadIPMEdit.getParent().setManaged(false);
            mTAUploadIPMEdit.getParent().setVisible(false);

            mLabelUpload1Tomcat.getParent().setVisible(false);
            mLabelUpload1Tomcat.getParent().setManaged(false);
            //将GridPane 其中的列宽度设置为0
            ObservableList<ColumnConstraints> columnConstraints = mGridPaneRoot.getColumnConstraints();
            columnConstraints.get(columnConstraints.size() - 1).setMaxWidth(0);
            columnConstraints.get(2).setMaxWidth(0);

            File httpd_replace_file = new File(deployConfigModel.getWorkersOldChangedPath());
            File httpd_zyfl_replace_file = new File(deployConfigModel.getWorkersOldChangedPath());
            File httpd_upload_replace_file = new File(deployConfigModel.getWordkersUploadChangedPath());

            if (httpd_replace_file.exists()) {
                mTAEdit.setText(FileUtils.readFile(httpd_replace_file.getAbsolutePath()));
            }
            if (httpd_zyfl_replace_file.exists()) {
                mTAZYFLEdit.setText(FileUtils.readFile(httpd_zyfl_replace_file.getAbsolutePath()));
            }
            if (httpd_upload_replace_file.exists()) {
                mTAUploadEdit.setText(FileUtils.readFile(httpd_upload_replace_file.getAbsolutePath()));
            }

        }
    }
}
