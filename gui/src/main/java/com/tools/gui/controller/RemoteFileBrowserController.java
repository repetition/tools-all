package com.tools.gui.controller;

import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TreeView;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.ResourceBundle;

public class RemoteFileBrowserController implements Initializable {
    private static final Logger log = LoggerFactory.getLogger(RemoteFileBrowserController.class);
    public TreeView mTreeView;
    public Button mBTSelector;
    public Button mBTCancel;
    private Stage currentStage;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        log.info(currentStage+"");
    }

    public void setStage(Stage stage) {
        this.currentStage = stage;
    }
}
