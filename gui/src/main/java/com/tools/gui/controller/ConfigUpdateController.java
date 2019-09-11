package com.tools.gui.controller;

import com.tools.commons.utils.StringUtils;
import com.tools.commons.utils.Utils;
import com.tools.gui.utils.view.AlertUtils;
import com.tools.gui.utils.view.JFXSnackbarUtils;
import com.tools.gui.utils.view.RestartUtils;
import com.tools.socket.client.ClientInstructionProcess;
import com.tools.socket.client.SocketClient;
import com.tools.socket.manager.SocketManager;
import io.netty.channel.Channel;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.ResourceBundle;

public class ConfigUpdateController implements Initializable {
    private static final Logger log = LoggerFactory.getLogger(ConfigUpdateController.class);
    public TextField mTFAddress;
    public Hyperlink mHLUpdate;
    public int port = 7777;
    private SocketClient socketClient;
    @Override
    public void initialize(URL location, ResourceBundle resources) {


        socketClient = SocketManager.getSocketClient();
        socketClient.setPort(port);
        socketClient.setOnConnectedListener(new SocketClient.OnConnectedListener() {
            @Override
            public void onSuccess(Channel channel) {
                updateView(true,"正在更新...");
                //下载更新
             //   new ClientInstructionProcess(channel).setController(ConfigUpdateController.this).updateConfig();
            }

            @Override
            public void onFail(Exception e) {

                updateView(false,"下载更新");

                JFXSnackbarUtils.show("下载更新错误！",2000L, (Pane) mTFAddress.getParent().getParent());
            }
        });

    }



    public void onAction(ActionEvent actionEvent) {
        updateView(true,"正在更新...");

        if (!socketClient.getConnect()) {
            String addressText = mTFAddress.getText();
            if (!StringUtils.isEmpty(addressText)) {
                socketClient.setHost(addressText);
                socketClient.connectServer();
            }
        }else {
            //下载更新
           // new ClientInstructionProcess(socketClient.getChannel()).setController(this).updateConfig();
        }

    }


    public void updateView(boolean disable,String text){
        Platform.runLater(() -> {
            log.info(disable+","+text);
            mHLUpdate.setDisable(disable);
            mHLUpdate.setText(text);
        });
    }

    public void complete() {

        updateView(false,"下载更新");
        JFXSnackbarUtils.show("更新完成！",2000L, (Pane) mTFAddress.getParent().getParent());


        AlertUtils.showCallBackAlert("重启应用","更新配置完成,重启后生效,是否重启应用?","更新配置完成,重启后生效,是否重启应用?", param -> {
            if (param == ButtonType.OK) {
                RestartUtils.restart();
            }
            return param;
        });

    }
}