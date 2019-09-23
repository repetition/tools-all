package com.tools.gui.controller;

import javafx.fxml.Initializable;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;

import java.net.URL;
import java.util.ResourceBundle;

public class DeployTypeController extends BaseController implements Initializable {
    public RadioButton mRBUpload;
    public RadioButton mRBZYFL;
    public RadioButton mRBOld;



    @Override
    public void initialize(URL location, ResourceBundle resources) {

        ToggleGroup toggleGroup = new ToggleGroup();

        mRBUpload.setToggleGroup(toggleGroup);
        mRBZYFL.setToggleGroup(toggleGroup);
        mRBOld.setToggleGroup(toggleGroup);

    }
}
