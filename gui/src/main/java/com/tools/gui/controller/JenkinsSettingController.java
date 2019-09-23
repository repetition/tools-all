package com.tools.gui.controller;

import com.tools.commons.utils.FileUtils;
import com.tools.commons.utils.IOUtils;
import com.tools.commons.utils.PropertyUtils;
import com.tools.gui.config.Config;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Properties;
import java.util.Set;


public class JenkinsSettingController extends BaseController{
    private static final Logger log = LoggerFactory.getLogger(JenkinsSettingController.class);

    public TextField mTFJenkinsAddress;
    public Button mBTSave;
    public Button mBTCancel;
    public Button mBTReset;
    public TextArea mTAModuleList;

    private Stage mStage;


    public void onButtonAction(ActionEvent actionEvent) {

        if (actionEvent.getSource() == mBTSave) {
            saveJenkinsProperties();
        }

        if (actionEvent.getSource() == mBTReset) {

        }
        if (actionEvent.getSource() == mBTCancel) {
            mStage.close();
        }
    }

    public void saveJenkinsProperties() {
        String jenkinsAddressText = mTFJenkinsAddress.getText();
        String moduleListText = mTAModuleList.getText();
        String[] moduleList = moduleListText.split("\n");

        FileOutputStream fos = null;
        try {
            fos = FileUtils.getFileOutputStream("JenkinsProperties.properties");

            jenkinsAddressText = "jenkins.address=" + jenkinsAddressText;
            fos.write(jenkinsAddressText.getBytes(Charset.forName("utf-8")));
            fos.write("\n".getBytes());
            fos.write("\n".getBytes());
            fos.flush();

            for (int i = 0; i < moduleList.length; i++) {
                String moduleStr = moduleList[i];
                moduleStr = "jenkins.module." + i + "=" + moduleStr + "\n";
                fos.write(moduleStr.getBytes(Charset.forName("UTF-8")));
                fos.flush();
            }
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            IOUtils.close(fos);
        }
        mStage.close();
    }

    @FXML
    private void initialize() {

        Properties properties = new PropertyUtils(Config.JenkinsPropertiesFileName).getOrderedProperties();
        Set<String> names = properties.stringPropertyNames();
        /*Enumeration<?> propertyNames = properties.propertyNames();

        while (propertyNames.hasMoreElements()) {
            log.info(propertyNames.nextElement() + "");
        }*/

        StringBuilder stringBuilder = new StringBuilder();
        for (String name : names) {
            String property = properties.getProperty(name);
            //读取Jenkins地址
            if (name.contains("jenkins.address")) {
                mTFJenkinsAddress.setText(property);
            }
            //读取模块列表
            if (name.contains("jenkins.module")) {
                stringBuilder.append(property + "\r\n");
            }
        }
        mTAModuleList.setText(stringBuilder.toString());
    }

    public void setStage(Stage jenkinsSettingStage) {
        mStage = jenkinsSettingStage;
    }
}
