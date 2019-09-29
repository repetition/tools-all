package com.tools.gui.controller;

import com.tools.commons.utils.FileUtils;
import com.tools.gui.config.Config;
import com.tools.gui.utils.view.JFXSnackbarUtils;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.*;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebEvent;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Duration;
import netscape.javascript.JSObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.ResourceBundle;

public class WebXmlEditorController extends BaseController  implements Initializable  {
    private static final Logger log = LoggerFactory.getLogger(WebXmlEditorController.class);
    /**
     * CodeEditor 用来加载codemirror 进行xml格式化
     */
    public WebView mWebView;
    public TextField mTFFind;

    public RadioButton mRBPrevious;
    public RadioButton mRBNext;

    public Button mBTSave;
    public Button mBTClose;
    public Button mBTFind;

    public Button mBTest;
    private WebEngine webEngine;

    public BorderPane borderPane;
    private Stage mHtmlEditorStage;
    private String filePath;

    public VBox mVBoxInput;
    public GridPane mGridPane;
    public ColumnConstraints column2;

    private Window window;
    private ObservableList<ColumnConstraints> columnConstraints;
    private double vBoxInput_open_width =0;
    private double windowWidth;

    public HBox mHBoxInputChildren;
    /**
     * item控制器。 必须为fx:id + MainController 才可以注入
     */
    @FXML
    private ConfigItemController mHBoxInputChildrenController;

    /**
     * 搜索事件
     * @param actionEvent
     */
    public void onFindAction(ActionEvent actionEvent) {

        String findText = mTFFind.getText().toLowerCase().trim();

        if (null ==findText || findText.isEmpty()) {
            JFXSnackbarUtils.show("请输入搜索的内容",2000L,borderPane);
            return;
        }
        /*向下搜索*/
        if (mRBNext.isSelected()) {
            Object executeScript =  webEngine.executeScript("findNext('"+findText+"');");
            if (!(Boolean)executeScript){
                JFXSnackbarUtils.show("没有搜索到内容",2000L,borderPane);
            }

        }
        /*向上搜索*/
        if (mRBPrevious.isSelected()) {
            Object executeScript = webEngine.executeScript("findPrevious('" + findText + "');");
            if (!(Boolean)executeScript) {
                JFXSnackbarUtils.show("没有搜索到内容", 2000L, borderPane);
            }
        }
    }

    public void onSaveAction(ActionEvent actionEvent) {
        String sourceStr = (String)webEngine.executeScript("getValue();");
       // log.info(sourceStr);
         FileUtils.saveFile(sourceStr,filePath);
         //保存配置文件时，触发保存运行时修改的配置文件方法
        boolean checked = mHBoxInputChildrenController.saveLastItemPropertyChanged();
        if (!checked) {
            return;
        }
        mHtmlEditorStage.close();
    }

    public void onCancelAction(ActionEvent actionEvent) {
        mHtmlEditorStage.close();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        log.info("WebXmlEditorController initialize");

        if (!Config.isDebug) {
            mBTest.setVisible(false);
            mBTest.setManaged(false);
        }

        ToggleGroup toggleGroup = new ToggleGroup();
        mRBPrevious.setToggleGroup(toggleGroup);
        mRBNext.setToggleGroup(toggleGroup);
        mRBNext.setSelected(true);
        /**
         * input输入框 的 问题输入事件，用来标记所有符合条件的文字
         */
        mTFFind.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                //调用js，标记所有相同的搜索

                webEngine.executeScript("selectMultiple('"+newValue+"');");

            }
        });

        webEngine = mWebView.getEngine();
        webEngine.setJavaScriptEnabled(true);

        webEngine.getLoadWorker().stateProperty().addListener(new ChangeListener<Worker.State>() {

            private JavaScriptLog javaScriptLog;

            @Override
            public void changed(ObservableValue<? extends Worker.State> observable, Worker.State oldValue, Worker.State newValue) {
                if (newValue== Worker.State.SUCCEEDED) {
                    JSObject window = (JSObject) webEngine.executeScript("window");
                    javaScriptLog = new JavaScriptLog();
                    window.setMember("java",javaScriptLog);

                    //    log.info("执行js");
                    /*读取文件*/
                    String str = FileUtils.readFile(filePath);
                    //    log.info(str);
                    //  webEngine.executeScript("setValue("+str+")");
                    //codemirror \ 会被转义，所以赋值之前 直接给转义掉 将\ 替换成 \\
                    String replace_Str = str.replace("\\", "\\\\");

                    /*html不支持\r\n 需要 转义 \\r\\n */
                    String replace = replace_Str.replace("\n", "\\n");

                    //xml中如果存在 ' 号,在进行 executeScript时会报异常, setValue之前将所有'号替换成其他符号
                    replace = replace.replace("'","&#x27;");


                    webEngine.executeScript("setValue('"+replace+"');");

                    // webEngine.executeScript("setValue();");
                    // webEngine.executeScript("setValue(\""+str+"\");");
                    //    webEngine.executeScript("demo('test');");
                    //   webEngine.executeScript("setValue('"+str+"');");
                    // webEngine.executeScript("setValue('"+replace+"');");
                    /*隐藏web自带滚动条*/
                    webEngine.executeScript("document.body.style.overflow = 'hidden';");
                    /*获取底部拖动栏的高度*/
                    Object scrollBar_horizontal_height = webEngine.executeScript("document.getElementsByClassName('CodeMirror-simplescroll-horizontal')[0].offsetHeight");
                    log.info("scrollBar_horizontal_height:"+scrollBar_horizontal_height);
                    // VBox parent = (VBox) mWebView.getParent().getParent();
                    BorderPane borderpane = (BorderPane) mWebView.getParent();

                    // log.info("VBox.getHeight() : "+parent.getHeight());
                    log.info("borderpane.getHeight() : "+borderpane.getHeight());

                    double height = mWebView.getHeight();
                    log.info("webView_Height:"+height+"");
                    /*将剪辑器高度减去拖动栏的高度，使拖动栏显示出来*/
                    webEngine.executeScript("reSize('"+(height-(int) scrollBar_horizontal_height)+"px"+"');");

                }
            }
        });
        /**
         * js alert回调事件
         */
        webEngine.setOnAlert(new EventHandler<WebEvent<String>>() {
            @Override
            public void handle(WebEvent<String> event) {
                String data = event.getData();
                //  log.info(data);
            }
        });

        String url = WebXmlEditorController.class.getResource("/codemirror/codeEditor.html").toExternalForm();
        log.info(url);
        webEngine.load(url);

    }

    public void setStage(Stage htmlEditorStage) {
        mHtmlEditorStage = htmlEditorStage;

    }

    /**
     * 设置文件路径
     * @param path 文件路径
     */
    public void setFilePath(String path) {
        this.filePath = path;
        mHBoxInputChildrenController.setFilePath(filePath);

    }

    public void onTestAction(ActionEvent actionEvent) {

        log.info(mGridPane.getWidth()+"");

    }

    public void onOpenInputAction(ActionEvent actionEvent) {
        if (mVBoxInput.isManaged()) {
            initCloseTimeLine().play();
        }else {
            initOpenTimeLine().play();
        }
    }
    /**
     * 侧边窗口关闭动画
     * @return 返回TimeLine对象
     */
    private Timeline initCloseTimeLine() {
        columnConstraints = mGridPane.getColumnConstraints();
        //获取要展开的宽度
       // vBoxInput_open_width = mVBoxInput.getWidth();
        //获取窗口的宽度
        windowWidth = window.getWidth();
        //创建一个宽度属性 用来绑定到timelines中，以此来观察值得变化
        ReadOnlyDoubleWrapper wrapper = new ReadOnlyDoubleWrapper(windowWidth);
        //创建时间轴动画
        Timeline timeline = new Timeline();
        timeline.setCycleCount(1);
        timeline.setAutoReverse(true);
        //创建一个value值，从一个值在一定时间内变成另一个值
        KeyValue keyValue = new KeyValue(wrapper, windowWidth - vBoxInput_open_width);
        KeyFrame keyFrame = new KeyFrame(Duration.millis(800),keyValue);
        timeline.getKeyFrames().addAll(keyFrame);
        wrapper.addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {

                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        column2.setPrefWidth(vBoxInput_open_width -(windowWidth - newValue.doubleValue()));
                        column2.setMaxWidth(vBoxInput_open_width -(windowWidth - newValue.doubleValue()));
                        window.setWidth(newValue.doubleValue());
                    }
                });
            }
        });

        timeline.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                mVBoxInput.setManaged(false);
                mVBoxInput.setVisible(false);
               // columnConstraints.remove(column2);
            }
        });

        return timeline;
    }

    /**
     * 侧边窗口打开动画
     * @return 返回TimeLine对象
     */
    private Timeline initOpenTimeLine() {

        if (vBoxInput_open_width==0) {
            vBoxInput_open_width = mVBoxInput.getWidth();
        }
        columnConstraints = mGridPane.getColumnConstraints();
        //获取窗体宽度
        window = mHtmlEditorStage.getScene().getWindow();
        double open_windowWidth = window.getWidth();
        //创建一个宽度属性 用来绑定到timelines中，以此来观察值得变化
        ReadOnlyDoubleWrapper open_Wrapper = new ReadOnlyDoubleWrapper(open_windowWidth);
        //创建时间轴动画
        Timeline open_Timeline = new Timeline();
        open_Timeline.setCycleCount(1);
        open_Timeline.setAutoReverse(true);
        //在一定时间内 由指定值变成指定值
        KeyValue keyValue = new KeyValue(open_Wrapper, (open_windowWidth+vBoxInput_open_width));
        KeyFrame keyFrame = new KeyFrame(Duration.millis(800), keyValue);
        open_Timeline.getKeyFrames().addAll(keyFrame);
        //TimeLine执行完毕
        open_Timeline.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
            }
        });
        //数值的变化监听
        open_Wrapper.addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        //动态设置column2的值
                        column2.setPrefWidth(newValue.doubleValue()-open_windowWidth);
                        column2.setMaxWidth(newValue.doubleValue()-open_windowWidth);
                        //动态设置window窗口的值
                        window.setWidth(newValue.doubleValue());
                    }
                });

            }
        });
        //
       // columnConstraints.add(column2);
        mVBoxInput.setManaged(true);
        mVBoxInput.setVisible(true);
        return open_Timeline;
    }

    /**
     * js回调方法
     */
    public class JavaScriptLog{
        public void log (Object message){
            log.info("JavaScript: "+message);
        }
    }



}
