package com.tools.gui.utils.view;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NotificationsBuild {

    private static final Logger log = LoggerFactory.getLogger(NotificationsBuild.class);


    public static void showBottomRightNotification(String title, String text, double seconds) {
        log.info("showBottomRightNotification");

        try {
            Node graphic = null;
            Notifications notificationBuilder = Notifications.create()
                    .title(title)
                    .text(text)
                    .graphic(graphic)
                    .hideAfter(Duration.seconds(seconds))
                    .position(Pos.BOTTOM_RIGHT);
            notificationBuilder.onAction(e -> System.out.println("Notification clicked on!"));

            notificationBuilder.show();
        }catch (Exception e){
            e.printStackTrace();
            log.error("运行异常",e);
        }
    }

    public static void showCenterNotification(String title, String text, double seconds) {
        Node graphic = null;
        Notifications notificationBuilder = Notifications.create()
                .title(title)
                .text(text)
                .graphic(graphic)
                .hideAfter(Duration.seconds(seconds))
                .position(Pos.CENTER);
        notificationBuilder.onAction(e -> System.out.println("Notification clicked on!"));

        notificationBuilder.show();
    }
}
