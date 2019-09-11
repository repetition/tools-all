package com.tools.gui.utils.view;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.util.Callback;

public class AlertUtils {

    public static void showAlert(String title, String headerText, String contentText) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Alert alert = new Alert(Alert.AlertType.INFORMATION, contentText, ButtonType.CLOSE);
                alert.setHeaderText(headerText);
                alert.setTitle(title);
                Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
                stage.getIcons().addAll(new Image(this.getClass().getResource("/image/icons.png").toString()));
                //  alert.initOwner();
                alert.show();
            }
        });
    }


    public static void showDeleteAlert(String title, String headerText, String contentText, Callback<ButtonType, ButtonType> value) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                //  Alert alert = new Alert(AlertType.CONFIRMATION, contentText,ButtonType.OK, ButtonType.CANCEL);
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle(title);
                alert.setHeaderText(headerText);
               // alert.setContentText(contentText);
                alert.getButtonTypes().setAll(ButtonType.OK, ButtonType.CANCEL);
                alert.show();
                alert.setResultConverter(value);
            }
        });
    }


    public static void showCallBackAlert(String title, String headerText, String contentText, Callback<ButtonType, ButtonType> value) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                //  Alert alert = new Alert(AlertType.CONFIRMATION, contentText,ButtonType.OK, ButtonType.CANCEL);
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle(title);
                alert.setHeaderText(headerText);
                // alert.setContentText(contentText);
                alert.getButtonTypes().setAll(ButtonType.OK, ButtonType.CANCEL);
                alert.show();
                alert.setResultConverter(value);
            }
        });
    }
}
