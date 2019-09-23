package com.tools.gui.controller;

import com.tools.gui.item.FileTreeItem;
import com.tools.gui.process.CommandMethodEnum;
import com.tools.gui.process.FileBrowserProcess;
import com.tools.gui.process.ProcessManager;
import com.tools.socket.bean.Command;
import com.tools.socket.bean.FileItemInfo;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TreeView;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * 远程文件浏览器
 */
public class RemoteFileBrowserController extends BaseController implements Initializable {
    private static final Logger log = LoggerFactory.getLogger(RemoteFileBrowserController.class);
    public TreeView mTreeView;
    public Button mBTSelector;
    public Button mBTCancel;
    private Stage currentStage;

    private BaseController baseController;
    private String filter;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        log.info("initialize");

        FileBrowserProcess fileBrowserProcess = ProcessManager.getFileBrowserProcess();

        //设置root节点,第一次将FileItemInfo 传null,等到监听回调再设置FileItemInfo
        FileTreeItem rootNode = new FileTreeItem(null, currentStage);

        mTreeView.setRoot(rootNode);

        //设置处理器,用来发送和监听指令
        rootNode.setFileBrowserProcess(fileBrowserProcess);
        fileBrowserProcess.setOnFileBrowserSyncListener(fileItemInfoList -> {
            for (FileItemInfo fileItemInfo : fileItemInfoList) {
                Platform.runLater(() -> {
                    //设置过滤器
                    rootNode.setFileFilter(filter);
                    //收到数据需要设置fileItemInfo,ROOT节点只会收到一个节点
                    rootNode.setFileItemInfo(fileItemInfo);
                });
            }
        });

        Command command = new Command();
        command.setCommandCode(CommandMethodEnum.GET_FILE_DIRECTORY.getCode());
        command.setCommandMethod(CommandMethodEnum.GET_FILE_DIRECTORY.toString());
        command.setContent(new FileItemInfo().setNodeType(FileItemInfo.ROOT));
        fileBrowserProcess.sendMessage(command);
        //TreeView的条目选择事件
        mTreeView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {

            FileTreeItem fileTreeItem = (FileTreeItem) newValue;
            FileItemInfo fileItemInfo = fileTreeItem.getFileItemInfo();

            if (fileItemInfo.getNodeType().equals(FileItemInfo.ROOT)) {
                return;
            }
            currentStage.setTitle("远程路径选择 - " + fileItemInfo.getAbsolutePath());
        });

    }

    public void setStage(Stage stage) {
        this.currentStage = stage;
        log.info("setStage");
    }

    public void setFileFilter(String filter) {
        log.info("setFileFilter");
        this.filter =filter;

    }

    public void setBaseController(BaseController baseController) {
        this.baseController = baseController;
    }

    public void onCancelAction(ActionEvent actionEvent) {
        currentStage.close();
    }

    public void onSelectorAction(ActionEvent actionEvent) {
        FileTreeItem selectedItem = (FileTreeItem) mTreeView.getSelectionModel().getSelectedItem();

        baseController.onFileSelector(selectedItem.getFileItemInfo().getAbsolutePath());
        log.info(selectedItem+"");
        currentStage.close();
    }

}
