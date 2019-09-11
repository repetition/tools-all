package com.tools.gui.utils.view;

import com.tools.gui.main.Main;
import javafx.application.Platform;
import javafx.stage.Stage;

public class RestartUtils {
    /**
     * 重启应用，读取配置
     */
    public static void restart() {
        //关闭窗口
        // TODO: 2018/5/9   程序启动时为设置关闭为隐士关闭，在保存时需要重新启动应用，则需要将setImplicitExit置为true
        Platform.setImplicitExit(true);
        Main.mainStage.close();
        Main main = new Main();
        try {
            main.start(new Stage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
