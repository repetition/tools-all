package com.tools.gui.item;

import com.tools.gui.process.CommandMethodEnum;
import com.tools.gui.process.FileBrowserProcess;
import com.tools.gui.utils.view.ProgressUtils;
import com.tools.socket.bean.Command;
import com.tools.socket.bean.FileItemInfo;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileTreeItem extends TreeItem<Object> {

    private static final Logger log = LoggerFactory.getLogger(FileTreeItem.class);
    private  Stage stage;

    private FileItemInfo fileItemInfo;

    private FileBrowserProcess fileBrowserProcess;
    //当前的子节点是否第一次加载
    private boolean isFirstTimeChildren =true;
    Image image = new Image(getClass().getResourceAsStream("/image/complete.png"), 18, 18, true, true);
   //文件夹默认图标
    Image directoryImage = new Image(getClass().getResourceAsStream("/image/folder.png"), 18, 18, true, true);
    //文件夹打开状态图标
    Image directoryOpenImage = new Image(getClass().getResourceAsStream("/image/folder-open.png"), 18, 18, true, true);
    //文件图标
    Image fileImage = new Image(getClass().getResourceAsStream("/image/white.png"), 18, 18, true, true);



    public FileTreeItem(FileItemInfo fileItemInfo, Stage stage) {
        this.fileItemInfo = fileItemInfo;
        this.stage=stage;
        setValue(fileItemInfo.getFileName());

        if (fileItemInfo.getIsDirectory()){
            setGraphic(new ImageView(directoryImage));
            //展开事件
            this.addEventHandler(TreeItem.branchExpandedEvent(),event -> {

                FileTreeItem source = (FileTreeItem) event.getSource();
                if (source.isExpanded()) {
                    ImageView imageView = (ImageView) source.getGraphic();
                    imageView.setImage(directoryOpenImage);
                }
            });
            //折叠事件
            this.addEventHandler(TreeItem.branchCollapsedEvent(),event -> {

                FileTreeItem source = (FileTreeItem) event.getSource();
                log.info("branchCollapsedEvent :"+source.isExpanded() );
                if (!source.isExpanded()) {
                    ImageView imageView = (ImageView) source.getGraphic();
                    imageView.setImage(directoryImage);
                }

            });
        }

        if (fileItemInfo.getIsFile()){
            setGraphic(new ImageView(fileImage));
        }

    }

    public void setFileBrowserProcess(FileBrowserProcess fileBrowserProcess) {
        this.fileBrowserProcess = fileBrowserProcess;
    }

    @Override
    public boolean isLeaf() {
        //用来控制是否可以折叠
        return fileItemInfo.getIsFile();
    }

    public FileItemInfo getFileItemInfo() {
        return fileItemInfo;
    }

    @Override
    public ObservableList<TreeItem<Object>> getChildren() {
        /**
         *
         */
        if(isFirstTimeChildren){
            log.info("调用了getChildren : "+ getFileItemInfo().getFileName());
            isFirstTimeChildren=false;
            super.getChildren().setAll(buildChildren(this));
        }
        return super.getChildren();
    }

    private ObservableList<FileTreeItem> buildChildren(FileTreeItem fileTreeItem) {
        Stage progress = ProgressUtils.createProgress(stage);
        progress.show();
        FileItemInfo fileItemInfo = fileTreeItem.getFileItemInfo();
        fileItemInfo.setFilter(FileItemInfo.FILTER_DIRECTORY_ONLY);
        Command command = new Command();
        command.setCommandCode(CommandMethodEnum.GET_FILE_DIRECTORY.getCode());
        command.setCommandMethod(CommandMethodEnum.GET_FILE_DIRECTORY.toString());
        command.setContent(fileItemInfo);
        fileBrowserProcess.sendMessage(command);

        fileBrowserProcess.setOnFileBrowserSyncListener(fileItemInfoList -> {
            ObservableList<FileTreeItem> observableList = FXCollections.observableArrayList();


            for (FileItemInfo itemInfo : fileItemInfoList) {
                FileTreeItem childItem = new FileTreeItem(itemInfo,stage);
                childItem.setFileBrowserProcess(fileBrowserProcess);
                observableList.add(childItem);
            }
            log.info(this.getFileItemInfo().getAbsolutePath());
            fileTreeItem.getChildren().setAll(observableList);

            Platform.runLater(() -> {
                progress.close();
            });
        });


        return FXCollections.emptyObservableList();
    }

}
