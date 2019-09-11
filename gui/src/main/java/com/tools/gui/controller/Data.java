package com.tools.gui.controller;

import com.tools.gui.utils.view.NotificationsBuild;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

import java.io.IOException;

public class Data {

    @FXML
    private HBox hBox;
    @FXML
    private Label label1;
    @FXML
    private Label label2;

    @FXML
    private Button button;

    public Data() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/listCellItem.fxml"));
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void onAction(ActionEvent actionEvent) {
        NotificationsBuild.showCenterNotification("test","test",5.0);
        button.setText("已经点击");
        button.setDisable(true);
        button.setVisible(false);
    }

    public void setInfo(String string) {
        label1.setText(string);
        label2.setText(string);
    }

    public HBox getBox() {
        return hBox;
    }

    @FXML
    public void initialize() {

    }


}
