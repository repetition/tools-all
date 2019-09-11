package com.tools.gui.controller;
import com.tools.commons.utils.LanguageFormatUtils;
import com.tools.gui.jenkins.JenkinsAutoBuild;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.function.Predicate;

public class ModuleListController {
    private static final Logger log = LoggerFactory.getLogger(ModuleListController.class);
    public Button mBTSelect;
    public Button mBTCancel;
    public ListView mModuleListView;
    public TextField mTFSearch;
    private String currentSelectorProject;

    private JenkinsBuildController jenkinsBuildController;
    private Stage mStage;

    public void onAction(ActionEvent event) {
        if (event.getSource() == mBTSelect) {

            if (null == currentSelectorProject) {
                return;
            }

            if (currentSelectorProject.contains("thinkwin-cr")) {
                currentSelectorProject = currentSelectorProject.substring(0, currentSelectorProject.indexOf("-thinkwin-cr"));
            }

            if (currentSelectorProject.contains("thinkwin-cm")){
                currentSelectorProject = currentSelectorProject.substring(0, currentSelectorProject.indexOf("-thinkwin-cm"));
            }
            jenkinsBuildController.setSelect(currentSelectorProject);
            mStage.close();
        }

        if (event.getSource() == mBTCancel) {
            mStage.close();
        }
    }

    @FXML
    private void initialize() {
        List<String> strings = JenkinsAutoBuild.searchProject("build");
        ObservableList<String> stringObservableList = FXCollections.observableArrayList(strings);
        mModuleListView.setItems(stringObservableList);
        //listView 点击事件
        mModuleListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                log.info("selector：" + newValue);
                currentSelectorProject = (String) newValue;
            }
        });

        FilteredList<String> filteredList = new FilteredList<>(stringObservableList);

        mTFSearch.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                //   searchListForJava(newValue, strings);

                //searchListForJavaFX  javaFx原生api实现
                filteredList.setPredicate(new Predicate<String>() {
                    @Override
                    public boolean test(String s) {
                        if (newValue == null || newValue.isEmpty()) {
                            return true;
                        }
 /*                       char[] toCharArray = LanguageFormatUtils.toHanyuPinyin(newValue).toCharArray();
                        boolean[] isContains = new boolean[toCharArray.length];
                        boolean[] matchs = new boolean[toCharArray.length];
                        Arrays.fill(matchs,true);
                        for (int i = 0; i < toCharArray.length; i++) {
                            if (LanguageFormatUtils.toHanyuPinyin(s).contains(String.valueOf(toCharArray[i]))) {
                                isContains[i] = true;
                                toCharArray[i] = 0;
                            } else {
                                isContains[i] = false;
                            }
                        }
                        if (Arrays.toString(isContains).equals(Arrays.toString(matchs))) {
                            return true; // Filter matches
                        }*/
                        if (LanguageFormatUtils.toHanyuPinyin(s).contains(LanguageFormatUtils.toHanyuPinyin(newValue))) {
                            return true; // Filter matches
                        }
                        return false; // Does not match
                    }
                });
                mModuleListView.setItems(filteredList);
            }

        });


    }

    public void setStage(Stage stage) {
        mStage = stage;
    }

    public void setController(JenkinsBuildController jenkinsBuildController) {
        this.jenkinsBuildController = jenkinsBuildController;
    }
}
