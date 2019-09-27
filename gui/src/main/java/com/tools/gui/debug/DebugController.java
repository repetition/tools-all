package com.tools.gui.debug;

import com.tools.gui.controller.BaseController;
import com.tools.gui.process.CommandMethodEnum;
import com.tools.gui.process.DeployProcess;
import com.tools.gui.process.FileBrowserProcess;
import com.tools.gui.process.ProcessManager;
import com.tools.socket.bean.Command;
import com.tools.socket.bean.FileItemInfo;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.event.ItemEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DebugController extends BaseController {
    private static final Logger log = LoggerFactory.getLogger(DebugController.class);
    public TreeView treeView;
    public ComboBox combobox;
    public ChoiceBox choicebox;
    public SplitMenuButton splitmenubutton;
    public List<String> dbList;
    private Stage mStage;
    private DeployProcess deployProcess;

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
        List<String> list = new ArrayList<>();
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

        Image image = new Image(getClass().getResourceAsStream("/image/complete.png"), 18, 18, true, true);
        Image directoryImage = new Image(getClass().getResourceAsStream("/image/directory.png"), 18, 18, true, true);
/*        HBox root = new HBox();
        root.setAlignment(Pos.CENTER_LEFT);
        root.getChildren().add(new ImageView(image));
        root.getChildren().add(new Label("complete"));*/


        //设置root节点,第一次将FileItemInfo 传null,等到监听回调再设置FileItemInfo
        FileTreeItem rootNode = new FileTreeItem(null,mStage);
        treeView.setRoot(rootNode);

        FileBrowserProcess fileBrowserProcess = ProcessManager.getFileBrowserProcess();
        //设置处理器,用来发送和监听指令
        rootNode.setFileBrowserProcess(fileBrowserProcess);
        fileBrowserProcess.setOnFileBrowserSyncListener(new FileBrowserProcess.OnFileBrowserSyncListener() {
            @Override
            public void onDirectoryUpdate(List<FileItemInfo> fileItemInfoList) {
                for (FileItemInfo fileItemInfo : fileItemInfoList) {
                    Platform.runLater(() -> {
                        //收到数据需要设置fileItemInfo,ROOT节点只会收到一个节点
                        rootNode.setFileItemInfo(fileItemInfo);
                    });
                }
            }

            @Override
            public void onError() {

            }
        });
    }

    public void setStage(Stage debugStage) {
        mStage = debugStage;
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
                    map.put("cm", cm_CheckBox.isSelected());
                    map.put("zyfl", zyfl_CheckBox.isSelected());
                    map.put("upload", upload_CheckBox.isSelected());
                    map.put("apache_config", apache_config_CheckBox.isSelected());
                    dialog.close();
                }
                return "";
            });
            dialog.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setProcess(DeployProcess deployProcess) {

        this.deployProcess = deployProcess;

        Command command = new Command();
        command.setCommandCode(CommandMethodEnum.GET_FILE_DIRECTORY.getCode());
        command.setCommandMethod(CommandMethodEnum.GET_FILE_DIRECTORY.toString());
        command.setContent(new FileItemInfo().setNodeType(FileItemInfo.ROOT));
        deployProcess.sendMessage(command);
    }


    class MyCell extends TreeCell<Pane> {

        private final ContextMenu contextMenu;

        public MyCell() {
            contextMenu = new ContextMenu();
            MenuItem menuItem = new MenuItem("111");
            contextMenu.getItems().add(menuItem);

        }

        @Override
        protected void updateItem(Pane item, boolean empty) {
            super.updateItem(item, empty);

            if (empty) {
                setGraphic(null);
            } else {

                HBox hBox = new HBox();

                Button button = new Button("treeview1");
                CheckBox checkBox = new CheckBox("treeview2");

                HBox.setMargin(checkBox, new Insets(5));
                hBox.getChildren().add(button);
                hBox.getChildren().add(checkBox);
                hBox.setDisable(true);
                setGraphic(hBox);
            }
        }
    }


}
