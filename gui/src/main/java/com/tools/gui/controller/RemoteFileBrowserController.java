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
import javafx.scene.control.Label;
import javafx.scene.control.TreeView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.*;

/**
 * 远程文件浏览器
 */
public class RemoteFileBrowserController extends BaseController implements Initializable {
    private static final Logger log = LoggerFactory.getLogger(RemoteFileBrowserController.class);
    public TreeView mTreeView;
    public Button mBTSelector;
    public Button mBTCancel;
    public Label mLabelFilter;
    public ColumnConstraints column1;
    public GridPane gridPane;
    public ColumnConstraints column2;
    public ColumnConstraints column3;
    private Stage currentStage;

    private BaseController baseController;
    private FileFilter fileFilter;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        log.info("initialize");
        //让三个列自适应  将三个列的宽度  = gridPane宽度的三分之一
        column1.percentWidthProperty().bind(gridPane.widthProperty().divide(gridPane.getColumnConstraints().size()));
        column2.percentWidthProperty().bind(gridPane.widthProperty().divide(gridPane.getColumnConstraints().size()));
        column3.percentWidthProperty().bind(gridPane.widthProperty().divide(gridPane.getColumnConstraints().size()));

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
                    rootNode.setFileFilter(fileFilter.getFilter(), fileFilter.getSuffixFilterList());
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

            if (fileFilter.getSuffixFilterList().size()>=1) {
                mLabelFilter.setText("filter : ("+String.join(",",fileFilter.getSuffixFilterList())+")");
            }
        });

    }

    public void setStage(Stage stage) {
        this.currentStage = stage;
        log.info("setStage");
    }

    /**
     * 设置文件过滤器
     *
     * @param fileFilter 过滤器
     */
    public void setFileFilter(FileFilter fileFilter) {
        log.info("setFileFilter");
        this.fileFilter = fileFilter;
    }

    public void setBaseController(BaseController baseController) {
        this.baseController = baseController;
    }

    public void onCancelAction(ActionEvent actionEvent) {
        currentStage.close();
    }

    public void onSelectorAction(ActionEvent actionEvent) {
        FileTreeItem selectedItem = (FileTreeItem) mTreeView.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            onFileSelectorCallBack.onFileSelector(selectedItem.getFileItemInfo());
            log.info(selectedItem + "");
        }
        currentStage.close();
    }

    private OnFileSelectorCallBack onFileSelectorCallBack;

    public void setOnFileSelectorCallBack(OnFileSelectorCallBack onFileSelectorCallBack) {
        this.onFileSelectorCallBack = onFileSelectorCallBack;
    }

    public interface OnFileSelectorCallBack {
        void onFileSelector(FileItemInfo fileItemInfo);
    }


    public static class FileFilter {

        private List<String> suffixFilterList;
        private String filter;

        public FileFilter(String filter, String... suffixFilters) {
            this.filter = filter;
            if (suffixFilters == null) {
                this.suffixFilterList = Collections.unmodifiableList(new ArrayList<>());
            } else {
                this.suffixFilterList = Collections.unmodifiableList(Arrays.asList(suffixFilters.clone()));
            }
        }

        public FileFilter(String filter) {
            this.filter = filter;
            this.suffixFilterList = Collections.unmodifiableList(new ArrayList<>());
        }

        public List<String> getSuffixFilterList() {
            return suffixFilterList;
        }

        public String getFilter() {
            return filter;
        }
    }
}
