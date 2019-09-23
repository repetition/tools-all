package com.tools.gui.controller;

import com.tools.commons.utils.PropertyUtils;
import com.tools.gui.config.Config;
import com.tools.gui.main.Main;
import com.tools.service.constant.DeployTypeEnum;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class DialogDeployTypeController extends BaseController implements Initializable {
    /**
     * 单机-旧版项目
     */
    public RadioButton mRBOld;
    /**
     * 单机-资源分离
     */
    public RadioButton mRBZYFL;
    /**
     * 单机-上传组件+CM
     */
    public RadioButton mRBUpload2CM;
    /**
     * 双机-上传组件(只部CM)
     */
    public RadioButton mRB2UploadCM;
    /**
     * 双机-上传组件(只部上传组件)
     */
    public RadioButton mRB2Upload;

    public RadioButton mRBUpload2IPM;

    public Button mBTSave;
    public Button mBTCancel;
    private Stage stage;
    private MainController mainController;
    private PropertyUtils propertyUtils;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        ToggleGroup toggleGroup = new ToggleGroup();
        mRBOld.setToggleGroup(toggleGroup);
        mRBZYFL.setToggleGroup(toggleGroup);
        mRBUpload2CM.setToggleGroup(toggleGroup);
        mRBUpload2IPM.setToggleGroup(toggleGroup);
        mRB2UploadCM.setToggleGroup(toggleGroup);
        mRB2Upload.setToggleGroup(toggleGroup);

      /*  if (!Config.isDebug){
            mRB2UploadCM.setDisable(true);
            mRB2Upload.setDisable(true);
        }
*/
        propertyUtils = new PropertyUtils(Config.CRConfigFileName);
        propertyUtils.getConfiguration2Properties();


        String deployType = propertyUtils.getConfigurationPropertyStringByKey("deploy.type");
        if (deployType.equals("")) {
            deployType = DeployTypeEnum.DEPLOY_OLD.name();
        }
        switch (DeployTypeEnum.valueOf(deployType)) {

            case DEPLOY_OLD:
                mRBOld.setSelected(true);
                break;

            case DEPLOY_ZYFL:
                mRBZYFL.setSelected(true);
                break;

            case DEPLOY_UPLOAD2CM:
                mRBUpload2CM.setSelected(true);
                break;

            case DEPLOY_UPLOAD2IPM:
                mRBUpload2IPM.setSelected(true);
                break;

            case DEPLOY_2UPLOAD_CM:
                mRB2UploadCM.setSelected(true);
                break;

            case DEPLOY_2UPLOAD:
                mRB2Upload.setSelected(true);
                break;
        }

    }

    public void onAction(ActionEvent actionEvent) {

        if (actionEvent.getSource() == mBTSave) {

            if (mRBOld.isSelected()) {
                mainController.setDeployType(new String[]{mRBOld.getText(), DeployTypeEnum.DEPLOY_OLD.name()});
                propertyUtils.setConfigurationProperty("deploy.type", DeployTypeEnum.DEPLOY_OLD.name());
            }
            if (mRBZYFL.isSelected()) {
                mainController.setDeployType(new String[]{mRBZYFL.getText(), DeployTypeEnum.DEPLOY_ZYFL.name()});
                propertyUtils.setConfigurationProperty("deploy.type", DeployTypeEnum.DEPLOY_ZYFL.name());
            }
            if (mRBUpload2CM.isSelected()) {
                mainController.setDeployType(new String[]{mRBUpload2CM.getText(), DeployTypeEnum.DEPLOY_UPLOAD2CM.name()});
                propertyUtils.setConfigurationProperty("deploy.type", DeployTypeEnum.DEPLOY_UPLOAD2CM.name());
            }

            if (mRBUpload2IPM.isSelected()) {
                mainController.setDeployType(new String[]{mRBUpload2IPM.getText(), DeployTypeEnum.DEPLOY_UPLOAD2IPM.name()});
                propertyUtils.setConfigurationProperty("deploy.type", DeployTypeEnum.DEPLOY_UPLOAD2IPM.name());
            }

            if (mRB2UploadCM.isSelected()) {
                mainController.setDeployType(new String[]{mRB2UploadCM.getText(), DeployTypeEnum.DEPLOY_2UPLOAD_CM.name()});
                ToastController.showToast(ToastController.TOAST_SUCCESS, Main.mainStage, "此模式只部署CM");
                propertyUtils.setConfigurationProperty("deploy.type", DeployTypeEnum.DEPLOY_2UPLOAD_CM.name());
            }
            if (mRB2Upload.isSelected()) {
                mainController.setDeployType(new String[]{mRB2Upload.getText(), DeployTypeEnum.DEPLOY_2UPLOAD.name()});
                ToastController.showToast(ToastController.TOAST_SUCCESS, Main.mainStage, "此模式只部署上传组件");
                propertyUtils.setConfigurationProperty("deploy.type", DeployTypeEnum.DEPLOY_2UPLOAD.name());
            }
            stage.close();
        }

        if (actionEvent.getSource() == mBTCancel) {
            stage.close();
        }
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }
}
