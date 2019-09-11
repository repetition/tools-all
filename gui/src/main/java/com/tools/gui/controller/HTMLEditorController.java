package com.tools.gui.controller;

import com.tools.commons.utils.FileUtils;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.web.HTMLEditor;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;

public class HTMLEditorController {
    public HTMLEditor mHEditor;
    private static final Logger log = LoggerFactory.getLogger(HTMLEditorController.class);
    public TextArea mTAEditor;
    public Button mBTSave;
    public Button mBTCancel;
    public TextField mTFFindInput;
    public Button mBTFind;
    public RadioButton mRBUp;
    public RadioButton mRBDown;
    public TextArea mCAEditor;
    private Stage mHtmlEditorStage;
    private String path;

    private int currentPosition = -1;

    public void onAction(ActionEvent event) {

        if (event.getSource() == mBTSave) {
            saveFile();
            mHtmlEditorStage.close();
        }

        if (event.getSource() == mBTCancel) {
            mHtmlEditorStage.close();
        }


        if (event.getSource() == mBTFind) {
            // TODO: 2018/5/16 设置查找

            String findstr = mTFFindInput.getText().trim();
            if (findstr.equals("")) {
                showAlert("提示", "请输入查找内容！", "请输入查找的内容！");
                return;
            }
            String editorText = mCAEditor.getText();
            //获取当前光标位置
            int caretPosition = mCAEditor.getCaretPosition();
            log.info("caretPosition:" + caretPosition);
            if (mRBDown.isSelected()) {
                //根据当前光标位置向下搜索 获取位置
                int startIndex;
                if (mCAEditor.getSelectedText() == null || mCAEditor.getSelectedText().trim().equals("")) {
                    startIndex = editorText.indexOf(findstr, caretPosition + 1);
                } else {
                    startIndex = editorText.indexOf(findstr, caretPosition - findstr.length()+1);
                }
                log.info("caretPosition:" + caretPosition + "startIndex: " + startIndex + "currentPosition:" + currentPosition);
                if (startIndex != -1) {
                    //将光标移动到查找位置
                    mTAEditor.positionCaret(startIndex);
                    //将查找的字符串选中
                    currentPosition = mCAEditor.getCaretPosition();
                    mCAEditor.selectRange(startIndex, startIndex + findstr.length());
                } else {
                    showAlert("提示", "没有搜索到内容！", "没有搜索到内容！");
                }
            }
            //向上查询
            if (mRBUp.isSelected()) {
                int index;
                //当前没有选中 字符串
                if (mCAEditor.getSelectedText() == null || mCAEditor.getSelectedText().trim().equals("")) {
                    index = editorText.lastIndexOf(findstr, caretPosition - 1);
                } else {
                    //如果选中了字符串 就删除字符串长度，然后+1查找
                    index = editorText.lastIndexOf(findstr, caretPosition - findstr.length() - 1);
                }
                if (index != -1) {
                   mCAEditor.positionCaret(index);
                    mCAEditor.selectRange(index, index + findstr.length());

                } else {
                    showAlert("提示", "没有搜索到内容！", "没有搜索到内容！");
                }
            }

        }

    }

    private void saveFile() {
        String editorText = mCAEditor.getText();
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(path);
            fileOutputStream.write(editorText.getBytes(Charset.forName("utf-8")));
            fileOutputStream.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (null!=fileOutputStream) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void setFilePath(String path) {
        this.path = path;
        String fileInfo = FileUtils.readFile(path);
        mCAEditor.replaceText(0, 0, fileInfo);
       // mCAEditor.showParagraphAtTop(0);
    }

    private void showAlert(String title, String headerText, String contentText) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText(headerText);
                alert.setContentText(contentText);
                alert.setTitle(title);
                alert.getButtonTypes().setAll(ButtonType.CLOSE);
                Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
                stage.getIcons().addAll(new Image(this.getClass().getResource("/image/icons.png").toString()));
                //  alert.initOwner();
                alert.show();
            }
        });
    }

    @FXML
    private void initialize() {
        //设置TextField提示语
        mTFFindInput.setPromptText("输入查找内容");
       // mTFFindInput.setText("zwDbUser");

      //  mCAEditor.setStyle("");

        ToggleGroup toggleGroup = new ToggleGroup();
        mRBUp.setToggleGroup(toggleGroup);
        mRBDown.setToggleGroup(toggleGroup);
        mRBDown.setSelected(true);
    }

    public void setStage(Stage htmlEditorStage) {
        mHtmlEditorStage = htmlEditorStage;
    }
}
