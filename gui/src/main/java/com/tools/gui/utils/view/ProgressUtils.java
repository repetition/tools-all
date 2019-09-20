package com.tools.gui.utils.view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

import java.io.IOException;

public class ProgressUtils {


    public static Stage createProgress(Window stage){

        FXMLLoader fxmlLoader = new FXMLLoader(ProgressUtils.class.getResource("/fxml/progress.fxml"));
        try {
            BorderPane borderPane = fxmlLoader.load();
            Stage progressStage = new Stage();
            Scene progressScene = new Scene(borderPane);
            progressScene.setFill(Color.TRANSPARENT);
            progressStage.centerOnScreen();
            progressStage.setScene(progressScene);
            progressStage.initModality(Modality.WINDOW_MODAL);
            progressStage.initStyle(StageStyle.TRANSPARENT);
            progressStage.initOwner(stage);
            return progressStage;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
