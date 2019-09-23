package com.tools.gui.controller;

import com.tools.gui.utils.view.AlertUtils;
import com.tools.service.context.ApplicationContext;
import com.tools.service.model.DeployConfigModel;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class DialogController extends BaseController implements Initializable {
    public CheckBox mCB_CM;
    public CheckBox mCB_ZYFL;
    public CheckBox mCB_UPLOAD;
    public CheckBox mCB_APACHE_CONFIG;
    public Label mLAApacheConfig;
    public HBox mHbApache;
    public Label mLASelector;
    private DeployConfigModel deployConfigModel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        deployConfigModel = ApplicationContext.getDeployConfigModel();
    }


    public void setType(int type) {

        if (type == 1) {
            mLAApacheConfig.setManaged(false);
            mHbApache.setManaged(false);
            mCB_ZYFL.setManaged(false);
            mCB_ZYFL.setVisible(false);
            mCB_APACHE_CONFIG.setManaged(false);
            mCB_APACHE_CONFIG.setVisible(false);

            mLASelector.setText("服务选择");
        } else if (type == 2) {
            mLAApacheConfig.setManaged(false);
            mCB_APACHE_CONFIG.setManaged(false);
            mCB_APACHE_CONFIG.setVisible(false);

            mLASelector.setText("选择要删除的WAR包");
        }
    }

    public void deleteWar() {
        String cmWarPath = deployConfigModel.getCmDeployConfigMap().get("cmWarPath");
        String uploadWarPath = deployConfigModel.getUploadDeployConfigMap().get("uploadWarPath");
        String zyflWarPath = deployConfigModel.getZyflDeployConfigMap().get("zyflWarPath");

        File cmWarfile = new File(cmWarPath);
        File uploadWarfile = new File(uploadWarPath);
        File zyflWarfile = new File(zyflWarPath);


        if (mCB_UPLOAD.isSelected()) {

            if (uploadWarfile.exists()){
                boolean isDelete = uploadWarfile.delete();
                if (!isDelete) {
                    AlertUtils.showAlert("错误",uploadWarfile.getName()+"删除失败!",uploadWarfile.getName()+"删除失败!请检查文件是否被其他进程使用。");
                }
            }
        }
        if (mCB_CM.isSelected()) {
            if (cmWarfile.exists()){
                boolean isDelete = cmWarfile.delete();
                if (!isDelete) {
                    AlertUtils.showAlert("错误",cmWarfile.getName()+"删除失败!",cmWarfile.getName()+"删除失败!请检查文件是否被其他进程使用。");
                }
            }
        }
        if (mCB_ZYFL.isSelected()) {

            if (zyflWarfile.exists()){
                boolean isDelete = zyflWarfile.delete();
                if (!isDelete) {
                    AlertUtils.showAlert("错误",zyflWarfile.getName()+"删除失败!",zyflWarfile.getName()+"删除失败!请检查文件是否被其他进程使用。");
                }
            }
        }
    }
}
