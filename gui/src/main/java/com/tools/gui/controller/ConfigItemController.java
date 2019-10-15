package com.tools.gui.controller;

import com.google.gson.Gson;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXSnackbar;
import com.jfoenix.controls.JFXTextField;
import com.tools.commons.utils.PropertyUtils;
import com.tools.gui.config.ApplicationConfig;
import com.tools.gui.utils.view.JFXSnackbarUtils;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.XPath;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.function.Consumer;

/**
 * 嵌套控制器，配置文件修改值列表控制器。
 */
public class ConfigItemController extends BaseController implements Initializable {
    private static final Logger log = LoggerFactory.getLogger(ConfigItemController.class);
    public JFXTextField mTFConfigName;
    public JFXTextField mTFConfigValue;
    //列表父布局
    public HBox mHBoxConfigAdd;

    private static String filePath;
    private static PropertyUtils propertyUtils;
    private static HashMap<String, String> configMaps;

    /**
     * 添加按钮事件
     *
     * @param actionEvent 事件
     */
    public void onAddAction(ActionEvent actionEvent) {
        String configNameText = mTFConfigName.getText();
        String configValueText = mTFConfigValue.getText();
        if (null != configNameText && null != configValueText && !configNameText.isEmpty() && !configValueText.isEmpty()) {

            if (!saveLastItemPropertyChanged()) {
                return;
            }
            //保存配置
            configMaps.put(mTFConfigName.getText(), mTFConfigValue.getText());
            savePropertyChanged();
        } else {
            JFXSnackbarUtils.show("请输入要修改的配置项！", 2000L, (VBox) mHBoxConfigAdd.getParent());
            return;
        }
        //加载子布局View
        HBox hBox = loadChildrenView();
        //获取父布局
        VBox vBox = (VBox) mHBoxConfigAdd.getParent();
        //将item添加到父布局中
        vBox.getChildren().add(hBox);
        ObservableList<Node> vBoxChildren = vBox.getChildren();
        //重置所有子View
        resetChildren(vBoxChildren);
    }

    /**
     * 删除按钮事件
     *
     * @param actionEvent 事件
     */
    public void onRemoveAction(ActionEvent actionEvent) {

        configMaps.remove(mTFConfigName.getText());

        VBox vBox = (VBox) mHBoxConfigAdd.getParent();
        //删除当前条目
        //  2019/1/25 不能将子View从父控件删除，否则#208行代码则获取不到parent布局容器  ,将子View隐藏掉
        // vBox.getChildren().remove(mHBoxConfigAdd);
        mHBoxConfigAdd.setManaged(false);
        mHBoxConfigAdd.setVisible(false);
        mTFConfigName.setText("");
        mTFConfigValue.setText("");


        ObservableList<Node> vBoxChildren = vBox.getChildren();
        //重置所有子View
        resetChildren(vBoxChildren);
        savePropertyChanged();
    }

    /**
     * 保存配置，将configMaps转化成json保存
     */
    private void savePropertyChanged() {
        Gson gson = new Gson();
        propertyUtils.setOrderedProperty("changed", gson.toJson(configMaps));
    }

    /**
     * 将子布局转化成控件View
     *
     * @return 返回转化成的View
     */
    private HBox loadChildrenView() {
        FXMLLoader fxmlLoader = new FXMLLoader();
        HBox hBox = null;
        try {
            //加载item布局 每次加载，此ConfigItemController 都会被实例化一次
            hBox = fxmlLoader.load(MainController.class.getResource("/fxml/config_item.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return hBox;
    }

    /**
     * 重置子View的编号，重置 添加按钮的显示和隐藏
     *
     * @param vBoxChildren 子View集合
     */
    private void resetChildren(ObservableList<Node> vBoxChildren) {
        //编号
        int num = 0;
        //动态设置控件数据
        for (Node vBoxChild : vBoxChildren) {
            //由于增加了 JFXSnackbar提示，他会将JFXSnackbar控件添加到父布局上，所以获取所有子控件时，将JFXSnackbar过滤掉
            if (vBoxChild instanceof JFXSnackbar) {
                continue;
            }
            if (!vBoxChild.isManaged()) {
                continue;
            }
            num++;
            HBox hBoxChild = (HBox) vBoxChild;
            Label mLaNum = (Label) hBoxChild.lookup("#mLaNum");
            //设置编号
            mLaNum.setText(String.valueOf(num));
            //如果为最后一个则不处理
            if (vBoxChild == vBoxChildren.get(vBoxChildren.size() - 1)) {
                continue;
            }

            JFXButton mBTAdd = (JFXButton) hBoxChild.lookup("#mBTAdd");
            JFXButton mBTRemove = (JFXButton) hBoxChild.lookup("#mBTRemove");

            //将添加按钮隐藏掉
            mBTAdd.setManaged(false);
            mBTAdd.setVisible(false);
            //将删除按钮显示
            mBTRemove.setManaged(true);
            mBTRemove.setVisible(true);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        log.info("ConfigItemController initialize");

    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
        //初始化配置文件
        //   if (propertyUtils == null) {
        File file = new File(filePath);
        propertyUtils = new PropertyUtils(ApplicationConfig.getApplicationConfPath() +file.getName() + ".Changed.properties");
        propertyUtils.getOrderedProperties();
        //   }
        //configMaps = new HashMap<>();
        String changed = propertyUtils.getOrderedPropertyStringByKey("changed");

        Gson gson = new Gson();
        //使用LinkedHashMap ，保证key的顺序
        configMaps = gson.fromJson(changed, LinkedHashMap.class);
        if (null == configMaps) {
            configMaps = new LinkedHashMap<>();
        }
        //获取父容器
        VBox vBox = (VBox) mHBoxConfigAdd.getParent();
        ObservableList<Node> vBoxChildren = vBox.getChildren();

        final int[] firstItem = {1};
        configMaps.entrySet().forEach(new Consumer<Map.Entry<String, String>>() {
            @Override
            public void accept(Map.Entry<String, String> stringStringEntry) {
                String key = stringStringEntry.getKey();
                String value = stringStringEntry.getValue();
                //标记第一个item。因为布局中存在一个item，打开窗口此控制器就会实例化，所以第一个item已经实例化直接设置 text值，后续的item需要重新加载布局
                if (firstItem[0] == 1) {
                    mTFConfigName.setText(key);
                    mTFConfigValue.setText(value);
                } else {
                    HBox hBox = loadChildrenView();
                    vBoxChildren.add(hBox);
                    JFXTextField tFConfigName = (JFXTextField) hBox.lookup("#mTFConfigName");
                    JFXTextField tFConfigValue = (JFXTextField) hBox.lookup("#mTFConfigValue");
                    tFConfigName.setText(key);
                    tFConfigValue.setText(value);
                }
                firstItem[0]++;
            }
        });
        //重置所有子View
        resetChildren(vBoxChildren);
    }

    /**
     * 由于保存事件由添加按钮触发，导致最后一个item 填写完毕后，数据无法保存，故保存配置文件时，使用确定按钮触发 保存
     */
    public boolean saveLastItemPropertyChanged() {
        //获取父布局
        VBox vBox = (VBox) mHBoxConfigAdd.getParent();
        ObservableList<Node> vBoxChildren = vBox.getChildren();
  /*      //倒序遍历
        for (int i = vBoxChildren.size()-1; i >= 0; i--) {
            Node node = vBoxChildren.get(i);
            //由于增加了 JFXSnackbar提示，他会将JFXSnackbar控件添加到父布局上，所以获取所有子控件时，将JFXSnackbar过滤掉
            if (node instanceof JFXSnackbar) {
                continue;
            }
            HBox hBox = (HBox) node;
            JFXTextField tFConfigName = (JFXTextField) hBox.lookup("#mTFConfigName");
            JFXTextField tFConfigValue = (JFXTextField) hBox.lookup("#mTFConfigValue");
            if (null != tFConfigName.getText() && null != tFConfigValue.getText() && !tFConfigName.getText().isEmpty() && !tFConfigValue.getText().isEmpty()) {
                configMaps.put(tFConfigName.getText(), tFConfigValue.getText());
                savePropertyChanged();
                break;
            }else {
                // TODO: 2019/1/25 此处存在bug，将最后一个item的值删除掉时，会将所有的值清空掉 
                configMaps.clear();
                savePropertyChanged();
            }
        }*/
        //配置保存之前，先清空，随后再一个一个添加。解决其中有item修改的时候没有保存到
        configMaps.clear();
        vBoxChildren.forEach(new Consumer<Node>() {
            @Override
            public void accept(Node node) {

                //由于增加了 JFXSnackbar提示，他会将JFXSnackbar控件添加到父布局上，所以获取所有子控件时，将JFXSnackbar过滤掉
                if (node instanceof JFXSnackbar) {
                    return;
                }
                HBox hBox = (HBox) node;
                JFXTextField tFConfigName = (JFXTextField) hBox.lookup("#mTFConfigName");
                JFXTextField tFConfigValue = (JFXTextField) hBox.lookup("#mTFConfigValue");
                //如果填入的配置项不为空的话 则保存配置
                if (null != tFConfigName.getText() && null != tFConfigValue.getText() && !tFConfigName.getText().isEmpty() && !tFConfigValue.getText().isEmpty()) {
                    //将配置项添加到map中
                    configMaps.put(tFConfigName.getText(), tFConfigValue.getText());
                }
            }
        });


        //获取后缀名
        String suffixName = filePath.substring(filePath.lastIndexOf("."), filePath.length()-1);

        boolean checkPropertyChanged = checkPropertyChanged(suffixName);
        if (!checkPropertyChanged) {
            return false;
        }
        //保存配置
        savePropertyChanged();
        return true;
    }

    private boolean checkPropertyChanged(String suffixName) {

        if (suffixName.equals(".xml")) {

            SAXReader saxReader = new SAXReader();
            Document document = null;
            org.dom4j.Element rootElement = null;
            String rootElementPath = null;
            try {
                document = saxReader.read(new File(filePath));
                //获取根节点
                rootElement = document.getRootElement();
                rootElementPath = rootElement.getPath();

                //检查xml是否存在命名空间
                String namespaceURI = rootElement.getNamespaceURI();
                HashMap<String, String> namespaceMap = null;
                if (!namespaceURI.isEmpty()) {
                    namespaceMap = new HashMap<>();
                    namespaceMap.put("nameSpace", namespaceURI);
                }

                for (Map.Entry<String, String> stringEntry : configMaps.entrySet()) {
                    String key = stringEntry.getKey();

                    if (null == namespaceMap) {
                        List<org.dom4j.Node> nodes = rootElement.selectNodes("/"+rootElementPath + "//" + key);
                        if (nodes.size() < 1) {

                            nodes = rootElement.selectNodes("/"+rootElementPath+"//*[@*='"+key+"']");
                            if (nodes.size()<1) {
                                JFXSnackbarUtils.show("[ " + key + " ] 没有找到！请检查输入是否错误", 2000L, (VBox) mHBoxConfigAdd.getParent());
                                log.warn(key + " not found！");
                                return false;
                            }
                        } else {
                            //处理带有命名空间的xml
                            //搜索属性值
                            XPath xPath = document.createXPath("/nameSpace:" + rootElementPath + "//nameSpace:*[@*='" + key + "']");
                            xPath.setNamespaceURIs(namespaceMap);
                            nodes = xPath.selectNodes(document);
                            //如果属性搜不到则搜索xml标签
                            if (nodes.size()<1) {
                                xPath = document.createXPath("/nameSpace:" + rootElementPath + "//nameSpace:" + key);
                                xPath.setNamespaceURIs(namespaceMap);
                                if (nodes.size()<1) {
                                    JFXSnackbarUtils.show("[ " + key + " ] 没有找到！请检查输入是否错误", 2000L, (VBox) mHBoxConfigAdd.getParent());
                                    log.warn(key + " not found！");
                                    return false;
                                }
                            }
                        }

                    }
                }
            } catch (DocumentException e) {
                e.printStackTrace();
            }
        }
        if (suffixName.equals(".properties")) {
            PropertyUtils propertyUtils = new PropertyUtils(new File(filePath));
            propertyUtils.getConfiguration2Properties();

            for (Map.Entry<String, String> stringEntry : configMaps.entrySet()) {
                String key = stringEntry.getKey();
                String stringByKey = propertyUtils.getConfigurationPropertyStringByKey(key);

                if ("".equals(stringByKey)) {
                    JFXSnackbarUtils.show("[ " + key + " ] 没有找到！请检查输入是否错误", 2000L, (VBox) mHBoxConfigAdd.getParent());
                    log.warn(key + " not found！");
                    return false;
                } else {
                }
            }
        }
        return true;
    }
}
