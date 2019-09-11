package com.tools.gui.controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DebugController {
    private static final Logger log = LoggerFactory.getLogger(DebugController.class);
    private Stage mStage;
    public ComboBox combobox;
    public ChoiceBox choicebox;
    public SplitMenuButton splitmenubutton;

    public List<String> dbList ;

    @FXML
    public void initialize() {
        choicebox.setValue("2222");
/*        combobox.editorProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                System.out.println(newValue);
                System.out.println(combobox.editorProperty().toString());
            }
        });*/
        List<String> list  = new ArrayList<>();
        list.add("1");
        list.add("2");
        list.add("3");
        list.add("4");
        list.add("5");
        choicebox.hide();
       // combobox.setItems(FXCollections.observableList(list));
        combobox.setValue("cr_4_2_7");

/*        choicebox.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                choicebox.setValue(null);
            }
        });*/
        choicebox.onActionProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                System.out.println(22222);
            }
        });
        choicebox.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                System.out.println(333);
            }
        });
        combobox.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String string = combobox.getSelectionModel().getSelectedItem().toString();
                log.info(string);
            }
        });
    }

    public void setStage(Stage debugStage) {
        mStage= debugStage;
    }

    public void setDBList(List<String> dbList) {
        this.dbList = dbList;
        combobox.setItems(FXCollections.observableList(dbList));
        choicebox.setItems(FXCollections.observableList(dbList));

    }

    public void onAction(ActionEvent actionEvent) {
        System.out.println(1111);
    }

    public void onAlertAction(ActionEvent actionEvent) {

        Map<String, Boolean> map = new HashMap<>();
        Dialog<String> dialog = new Dialog<>();
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/deploy_mode_dialog.fxml"));
            VBox vBox = fxmlLoader.load();
            dialog.getDialogPane().setContent(vBox);

            CheckBox cm_CheckBox = (CheckBox) vBox.lookup("#mCB_CM");
            CheckBox zyfl_CheckBox = (CheckBox) vBox.lookup("#mCB_ZYFL");
            CheckBox upload_CheckBox = (CheckBox) vBox.lookup("#mCB_UPLOAD");
            CheckBox apache_config_CheckBox = (CheckBox) vBox.lookup("#mCB_APACHE_CONFIG");

            dialog.setTitle("部署选择");
            //    dialog.getDialogPane().setContent(vBox);
            dialog.setWidth(300);
            dialog.setHeight(200);

            dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
            dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

            dialog.setResultConverter(param -> {

                if (param == ButtonType.OK) {
                    map.put("cm",cm_CheckBox.isSelected());
                    map.put("zyfl",zyfl_CheckBox.isSelected());
                    map.put("upload",upload_CheckBox.isSelected());
                    map.put("apache_config",apache_config_CheckBox.isSelected());
                    dialog.close();
                }
                return "";
            });
            dialog.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
