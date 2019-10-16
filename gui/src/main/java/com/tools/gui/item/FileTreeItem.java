package com.tools.gui.item;

import com.tools.constant.CommandMethodEnum;
import com.tools.gui.process.FileBrowserProcess;
import com.tools.gui.utils.view.JFXSnackbarUtils;
import com.tools.gui.utils.view.ProgressUtils;
import com.tools.socket.bean.Command;
import com.tools.socket.bean.FileItemInfo;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class FileTreeItem extends TreeItem<Object> {

    private static final Logger log = LoggerFactory.getLogger(FileTreeItem.class);
    private Stage stage;

    private FileItemInfo fileItemInfo;

    private FileBrowserProcess fileBrowserProcess;
    //当前的子节点是否第一次加载
    private boolean isFirstTimeChildren = true;
    Image completeImage = new Image(getClass().getResourceAsStream("/image/complete.png"), 18, 18, true, true);

    Image image = new Image(getClass().getResourceAsStream("/image/complete.png"), 18, 18, true, true);
    //文件夹默认图标
    Image directoryImage = new Image(getClass().getResourceAsStream("/image/folder.png"), 18, 18, true, true);
    //文件夹打开状态图标
    Image directoryOpenImage = new Image(getClass().getResourceAsStream("/image/folder-open.png"), 18, 18, true, true);
    //文件图标
    Image fileImage = new Image(getClass().getResourceAsStream("/image/white.png"), 18, 18, true, true);

    private String filter;
    private List<String> suffixFilterList;

    public FileTreeItem(FileItemInfo fileItemInfo, Stage stage) {
        this.fileItemInfo = fileItemInfo;
        this.stage = stage;

        if (null != fileItemInfo) {
            setValue(fileItemInfo.getFileName());
            //如果是文件夹,添加 展开和关闭监听事件
            if (fileItemInfo.getIsDirectory()) {
                setGraphic(new ImageView(directoryImage));
                //展开事件
                this.addEventHandler(TreeItem.branchExpandedEvent(), event -> {

                    FileTreeItem source = (FileTreeItem) event.getSource();
                    //节点展开时 设置展开状态的图标
                    if (source.isExpanded()) {
                        ImageView imageView = (ImageView) source.getGraphic();
                        imageView.setImage(directoryOpenImage);
                    }
                });
                //折叠事件
                this.addEventHandler(TreeItem.branchCollapsedEvent(), event -> {

                    FileTreeItem source = (FileTreeItem) event.getSource();
                    //节点关闭时 设置关闭状态的图标
                    log.info("branchCollapsedEvent :" + source.isExpanded());
                    if (!source.isExpanded()) {
                        ImageView imageView = (ImageView) source.getGraphic();
                        imageView.setImage(directoryImage);
                    }

                });
            }
            //如果是文件,设置文件图标
            if (fileItemInfo.getIsFile()) {
                setGraphic(new ImageView(fileImage));
            }
        }

    }

    /**
     * 设置节点
     * @param fileItemInfo 节点信息
     * @return
     */
    public FileTreeItem setFileItemInfo(FileItemInfo fileItemInfo) {
        this.fileItemInfo = fileItemInfo;
        //判断是否是root节点
        if (fileItemInfo.getNodeType().equals(FileItemInfo.ROOT)) {
            this.setValue(fileItemInfo.getFileName());
            this.setGraphic(new ImageView(completeImage));

            for (FileItemInfo fileChild : fileItemInfo.getFileChilds()) {
                log.info(fileChild.getAbsolutePath() + " isDir :" + fileChild.getIsDirectory());
                //设置过滤器类型
                fileChild.setFilter(fileItemInfo.getFilter());
                FileTreeItem childItemNode = new FileTreeItem(fileChild, stage);
                childItemNode.setFileBrowserProcess(fileBrowserProcess);
                childItemNode.setFileFilter(filter, suffixFilterList);
                this.getChildren().add(childItemNode);
            }
            //添加root节点时,默认将节点展开
            setExpanded(true);
        }
        //判断子节点
        if (fileItemInfo.getNodeType().equals(FileItemInfo.CHILD)) {
            log.info(fileItemInfo.getAbsolutePath() + " isDir :" + fileItemInfo.getIsDirectory());
            FileTreeItem childItemNode = new FileTreeItem(fileItemInfo,stage);
            childItemNode.setFileFilter(filter, suffixFilterList);
            childItemNode.setFileBrowserProcess(fileBrowserProcess);
        }

        return this;
    }

    /**
     * 设置文件浏览处理器
     * @param fileBrowserProcess  处理器
     */
    public void setFileBrowserProcess(FileBrowserProcess fileBrowserProcess) {
        this.fileBrowserProcess = fileBrowserProcess;
    }

    /**
     * 设置文件浏览处理器
     * @param filter  过滤器
     * @param suffixFilterList
     */
    public void setFileFilter(String filter, List<String> suffixFilterList) {
        this.filter = filter;
        this.suffixFilterList = suffixFilterList;
    }

    @Override
    public boolean isLeaf() {
        //用来控制是否可以折叠
        if (fileItemInfo != null) {

            return fileItemInfo.getIsFile();
        }
        return false;

    }

    public FileItemInfo getFileItemInfo() {
        return fileItemInfo;
    }

    @Override
    public ObservableList<TreeItem<Object>> getChildren() {
        /**
         *只有第一次且为子节点时,才调用buildChildren方法
         */
        if (isFirstTimeChildren&&fileItemInfo.getNodeType().equals(FileItemInfo.CHILD)) {
            log.info("调用了getChildren : " + getFileItemInfo().getFileName());
            isFirstTimeChildren = false;
            super.getChildren().setAll(buildChildren(this));
        }
        return super.getChildren();
    }

    /**
     * 生成子节点,展开子节点都会调用此方法,只会调用一次
     * @param fileTreeItem 当前节点的条目
     * @return 默认返回空集合,异步由fileBrowserProcess.setOnFileBrowserSyncListener回调返回
     */
    private ObservableList<FileTreeItem> buildChildren(FileTreeItem fileTreeItem) {
        //生成进度条
        Stage progress = ProgressUtils.createProgress(stage);
        progress.show();
        FileItemInfo fileItemInfo = fileTreeItem.getFileItemInfo();
        //设置过滤器
        fileItemInfo.setFilter(this.filter);
        fileItemInfo.setSuffixFilterList(this.suffixFilterList);
        //添加子节点回调监听
        fileBrowserProcess.setOnFileBrowserSyncListener(new FileBrowserProcess.OnFileBrowserSyncListener() {
            @Override
            public void onDirectoryUpdate(List<FileItemInfo> fileItemInfoList) {
                ObservableList<FileTreeItem> observableList = FXCollections.observableArrayList();
                //遍历创建添加子节点
                for (FileItemInfo itemInfo : fileItemInfoList) {
                    FileTreeItem childItem = new FileTreeItem(itemInfo, stage);
                    childItem.setFileFilter(filter, suffixFilterList);
                    childItem.setFileBrowserProcess(fileBrowserProcess);
                    observableList.add(childItem);
                }
                log.info(getFileItemInfo().getAbsolutePath());
                //将子节点添加到当前节点中
                fileTreeItem.getChildren().setAll(observableList);
                //加载完成,关闭进度条
                Platform.runLater(() -> {
                    progress.close();
                });
            }
            @Override
            public void onError() {
                Platform.runLater(() -> {
                    if (progress.isShowing()) {
                        progress.close();
                    }
                });
                JFXSnackbarUtils.show("没有连接agent服务,请重新连接agent服务",2000,(Pane) stage.getScene().getRoot());
            }
        });

        //发送获取子节点的指令
        Command command = new Command();
        command.setCommandCode(CommandMethodEnum.GET_FILE_DIRECTORY.getCode());
        command.setCommandMethod(CommandMethodEnum.GET_FILE_DIRECTORY.toString());
        command.setContent(fileItemInfo);
        //发动指令到agent
        fileBrowserProcess.sendMessage(command);

        return FXCollections.emptyObservableList();
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

}
