package com.tools.gui.controller;

import com.tools.commons.thread.ThreadPoolManager;
import com.tools.commons.utils.PropertyUtils;
import com.tools.commons.utils.StringUtils;
import com.tools.gui.config.Config;
import com.tools.gui.jenkins.ProjectBuild;
import com.tools.gui.utils.view.JFXSnackbarUtils;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.ResourceBundle;

import static com.tools.gui.utils.view.ProgressUtils.createProgress;

public class JenkinsLoginController implements Initializable {

    private static final Logger log = LoggerFactory.getLogger(JenkinsLoginController.class);
    public TextField mTFUser;
    public TextField mTFPwd;
    public Button mBTLogin;
    public Button mBTCancel;
    Stage progress;
    private Stage mStage;
    private PropertyUtils propertyUtils;

    public void onLoginAction(ActionEvent actionEvent) {


        String user = mTFUser.getText();
        String pwd = mTFPwd.getText();
        if (StringUtils.isEmpty(user) &&StringUtils.isEmpty(pwd)){
            log.warn("用户名或密码不能为空");
            JFXSnackbarUtils.show("用户名或密码不能为空",2000L,(Pane) mTFUser.getParent().getParent());
            return;
        }
        //创建loading窗
        progress = createProgress(mStage);
        progress.show();
        //执行登录
        ThreadPoolManager.getInstance().execute(() -> {
            if (null!=user&&null!=pwd){
                boolean isLogin = ProjectBuild.login(user.trim(), pwd.trim());

                Platform.runLater(() -> {
                    if (isLogin) {
                        propertyUtils.setConfigurationProperty("jenkins.user",user);
                        propertyUtils.setConfigurationProperty("jenkins.pwd",pwd);
                        log.warn("登录成功");
                        progress.close();
                        mOnSuccessListener.onSuccess();
                        JFXSnackbarUtils.show("登录成功",2000L,(Pane) mTFUser.getParent().getParent());
                    }else {
                        log.warn("失败");
                        progress.close();
                        mOnSuccessListener.onFail();
                        JFXSnackbarUtils.show("登录失败，请检查用户名和密码是否正确",2000L,(Pane) mTFUser.getParent().getParent());
                    }
                });
            }
        });

    }

    public void onCancelAction(ActionEvent actionEvent) {
        mStage.close();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        log.info("initialize");

        propertyUtils = new PropertyUtils(Config.JenkinsPropertiesFileName);
        propertyUtils.getConfiguration2Properties();

        mTFUser.setText(propertyUtils.getConfigurationPropertyStringByKey("jenkins.user"));
        mTFPwd.setText(propertyUtils.getConfigurationPropertyStringByKey("jenkins.pwd"));
    }

    public void setStage(Stage stage) {
        mStage = stage;
    }



    private static OnSuccessListener mOnSuccessListener;
    public static void setOnSuccessListener(OnSuccessListener onSuccessListener){
        mOnSuccessListener = onSuccessListener;
    }

    public  interface OnSuccessListener{
        void onSuccess();
        void onFail();
    }
}
