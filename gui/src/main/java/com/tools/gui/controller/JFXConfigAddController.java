package com.tools.gui.controller;

import com.jfoenix.controls.*;
import com.jfoenix.controls.cells.editors.TextFieldEditorBuilder;
import com.jfoenix.controls.cells.editors.base.GenericEditableTreeTableCell;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import com.tools.commons.utils.FileUtils;
import com.tools.commons.utils.PropertyUtils;
import com.tools.commons.utils.Utils;
import com.tools.gui.config.Config;
import com.tools.gui.utils.view.FileChooserUtils;
import com.tools.gui.utils.view.JFXSnackbarUtils;
import com.tools.gui.utils.view.RestartUtils;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URL;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;

/**
 * 添加配置文件 MainController
 */
public class JFXConfigAddController extends BaseController implements Initializable {
    private static final Logger log = LoggerFactory.getLogger(JFXConfigAddController.class);

    public VBox mVBoxItemRoot;
    public JFXTreeTableView<ConfigFileInfo> treeTableView;
    public JFXTreeTableColumn<ConfigFileInfo,String> fileNameColumn;
    public JFXTreeTableColumn<ConfigFileInfo,String> filePathColumn;

    public JFXButton mBTConfigAdd;
    public JFXButton mBTConfigRemove;
    public BorderPane borderPaneRoot;
    private ObservableList<ConfigFileInfo> observableList;
    private Stage stage;
    private Properties properties;
    private Properties orderedProperties;

    public  boolean[] configListFileIsChanged = {false};
    private PropertyUtils propertyUtils;

    public void onConfigAddAction(ActionEvent actionEvent) {
        //文件选择器
        String filePath = FileChooserUtils.showSelectFileChooser(new FileChooser.ExtensionFilter("config", "*.xml", "*.properties"),"D://",stage);
        File file = new File(filePath);
        if (!file.getName().equals("")&&!file.isDirectory()) {
            //保存一个没有后缀名的
            String substring = file.getName().substring(0, file.getName().lastIndexOf("."));
            //key改成文件路径的md5值,防止存在重复的文件名
            propertyUtils.setOrderedProperty(Utils.getMD5(filePath),filePath);
            //将文件添加到TreeTableView上
            observableList.add(new ConfigFileInfo(file.getName(),file.getAbsolutePath()));
        }
    }

    public void onConfigRemoveAction(ActionEvent actionEvent) {
        //获取当前选中的index
        int focusedIndex = treeTableView.getSelectionModel().getFocusedIndex();
        //将当前选择的删除掉
        ConfigFileInfo configFileInfo = observableList.get(focusedIndex);
        //删除配置文件
        String fileName = configFileInfo.fileName.get();

        String key = propertyUtils.getOrderedPropertyStringByKey(fileName.substring(0, fileName.lastIndexOf(".")));
        //新旧key规则兼容
        if (key.equals("")) {
            propertyUtils.removeOrderedPropertyByKey(Utils.getMD5(configFileInfo.filePath.get()));
        }else {
            propertyUtils.removeOrderedPropertyByKey(fileName.substring(0, fileName.lastIndexOf(".")));
        }
        observableList.remove(focusedIndex);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        propertyUtils = new PropertyUtils(Config.CRConfigListFileName);
        propertyUtils.getOrderedProperties();
        //初始化时，将文件是否变更置为false

        observableList = FXCollections.observableArrayList();
        //读取配置文件
        properties = propertyUtils.getOrderedProperties();

        FileUtils.initFileChangedMonitor(propertyUtils.getPropertiesFile().getAbsolutePath(),configListFileIsChanged);

        Set<String> propertyNames = properties.stringPropertyNames();
        for (String name : propertyNames) {
            String filePath = properties.getProperty(name);
            File file = new File(filePath);
            //添加到tableView
            observableList.add(new ConfigFileInfo(file.getName(),filePath));
        }
        /*填充每个item*/
        fileNameColumn.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<ConfigFileInfo, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<ConfigFileInfo, String> param) {
                return param.getValue().getValue().fileName;
            }
        });
        /*填充每个item*/
        filePathColumn.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<ConfigFileInfo, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<ConfigFileInfo, String> param) {
                return param.getValue().getValue().filePath;
            }
        });

        /*item可以编辑*/
        fileNameColumn.setCellFactory(new Callback<TreeTableColumn<ConfigFileInfo, String>, TreeTableCell<ConfigFileInfo, String>>() {
            @Override
            public TreeTableCell<ConfigFileInfo, String> call(TreeTableColumn<ConfigFileInfo, String> param) {
                return new GenericEditableTreeTableCell<>(new TextFieldEditorBuilder());
            }
        });
        //创建树
        TreeItem<ConfigFileInfo> treeItem = new RecursiveTreeItem<>(observableList, new Callback<RecursiveTreeObject<ConfigFileInfo>, ObservableList<ConfigFileInfo>>() {
            @Override
            public ObservableList<ConfigFileInfo> call(RecursiveTreeObject<ConfigFileInfo> param) {
                return param.getChildren();
            }
        });



        /*不显示tree树*/
        treeTableView.setShowRoot(false);
        treeTableView.setEditable(true);
        //设置数据
        treeTableView.setRoot(treeItem);
        /*添加样式*/
        treeTableView.getStylesheets().addAll(JFXConfigAddController.class.getResource("/css/jfx-treetable-style.css").toExternalForm());
        ContextMenu contextMenu = new ContextMenu();
        MenuItem menuItem_up= new MenuItem("向上");

        MenuItem menuItem_down= new MenuItem("向下");
        contextMenu.getItems().add(menuItem_up);
        contextMenu.getItems().add(menuItem_down);
        menuItem_up.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                 //   treeTableView.getFocusModel().focusPrevious();

                int index = treeTableView.getSelectionModel().getSelectedIndex();

                if (index==0){
                    JFXSnackbarUtils.show("已到最上方",2000,borderPaneRoot);
                    return;
                }

                log.info("SelectedIndex:"+index);
                //向上
                TreeItem<ConfigFileInfo> configFileInfoTreeItem = treeItem.getChildren().remove(index);

                treeItem.getChildren().add(index-1,configFileInfoTreeItem);

                treeTableView.getSelectionModel().select(index-1);
                treeTableView.getFocusModel().focus(index-1);
            }
        });

        menuItem_down.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                int index = treeTableView.getSelectionModel().getSelectedIndex();

                if (index==treeItem.getChildren().size()-1){
                    JFXSnackbarUtils.show("已到最下",2000,borderPaneRoot);
                    return;
                }

                log.info("SelectedIndex:"+index);
                //向上
                TreeItem<ConfigFileInfo> configFileInfoTreeItem = treeItem.getChildren().remove(index);

                treeItem.getChildren().add(index+1,configFileInfoTreeItem);

                treeTableView.getSelectionModel().select(index+1);
                treeTableView.getFocusModel().focus(index+1);
            }
        });

        treeTableView.setContextMenu(contextMenu);
    }

    public void setStage(Stage stage) {
        this.stage = stage;

        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                FileUtils.closeFileChangedMonitor();
/*
                Boolean isChange = PropertyUtils.getOrderedPropertyBooleanByKey("isChange");
*/
                if (configListFileIsChanged[0]) {
                    RestartUtils.restart();
                }
                stage.close();
                configListFileIsChanged[0] = false;
            }
        });
    }

    /**
     * 配置文件路径和名称
     */
    class ConfigFileInfo extends RecursiveTreeObject<ConfigFileInfo> {
        /**
         * 文件名称
         */
        private StringProperty fileName;
        /**
         * 文件路径
         */
        private  StringProperty filePath;

        public ConfigFileInfo(String fileName, String filePath) {
            this.fileName = new SimpleStringProperty(fileName);
            this.filePath = new SimpleStringProperty(filePath);
        }

    }
}
