package com.tools.gui.controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.io.IOException;

public class ToastController {

    public static final int TOAST_SUCCESS = 11;
    public static final int TOAST_WARN = 12;
    public static final int TOAST_ERROR = 13;

    @FXML
    private HBox containerToast;

    @FXML
    private Label textToast;

    private void setToast(int toastType, String content){
        textToast.setText(content);
        switch (toastType){
            case TOAST_SUCCESS:
                containerToast.setStyle("-fx-background-color: #9FFF96");
                break;
            case TOAST_WARN:
                containerToast.setStyle("-fx-background-color: #FFCF82");
                break;
            case TOAST_ERROR:
                containerToast.setStyle("-fx-background-color: #FF777C");
                break;
        }
    }

    public static void showToast(int toastTyoe,Stage stage, String text){
        Stage dialog = new Stage();
        dialog.initOwner(stage);
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setResizable(false);
        dialog.initStyle(StageStyle.UNDECORATED);

        double dialogX = dialog.getOwner().getX();
        double dialogY = dialog.getOwner().getY();
        double dialogW = dialog.getOwner().getWidth();
        double dialogH = dialog.getOwner().getHeight();


        System.out.println(dialogX+";"+dialogY);
        System.out.println(dialogW+";"+dialogH);

        double w = stage.getWidth() / 2;
        double h = stage.getHeight() / 2;
        double x = dialog.getX();
        double y = dialog.getY();
        System.out.println(x+";"+y);
        System.out.println(stage.getX()+";"+stage.getY());
        double posX = dialogX + dialogW/2;
        double posY = dialogY + dialogH/6 *5;
        //dialog.setX(dialogX/2);
       // dialog.setY(dialogY/2);

        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(ToastController.class.getResource("/fxml/toastPopup.fxml"));
            loader.load();
            ToastController ce = loader.getController();
            ce.setToast(toastTyoe,text);
            dialog.setScene(new Scene(loader.getRoot()));

            dialog.show();
            new Timeline(new KeyFrame(
                    Duration.millis(1500),
                    ae -> {
                        dialog.close();
                    })).play();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}