package com.tools.gui.utils.view;

import com.jfoenix.controls.JFXSnackbar;
import javafx.application.Platform;
import javafx.scene.layout.Pane;

public class JFXSnackbarUtils {

    public static void show(String toastMessage, long timeout , Pane paneRoot){

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                JFXSnackbar jfxSnackbar = new JFXSnackbar(paneRoot);
                jfxSnackbar.getStylesheets().addAll(JFXSnackbarUtils.class.getResource("/css/tools-css.css").toExternalForm());
                jfxSnackbar.show(toastMessage,timeout);
            }
        });
    }

}
