package com.tools.gui.controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class ListViewController {
    private static final Logger log = LoggerFactory.getLogger(ListViewController.class);
    @FXML
    private ListView listView;
    private Set<String> stringSet;
    ObservableList observableList = FXCollections.observableArrayList();
    private final Parent parent;

    public ListViewController() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/customListview.fxml"));
        fxmlLoader.setController(this);
        try {
            parent = (Parent) fxmlLoader.load();
         //   Scene scene = new Scene(parent, 400.0, 500.0);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Parent getParent(){
        return parent;
    }

    public void setListView() {
        stringSet = new HashSet<>();
        stringSet.add("String 1");
        stringSet.add("String 2");
        stringSet.add("String 3");
        stringSet.add("String 4");
        observableList.setAll(stringSet);
        listView.setItems(observableList);
        listView.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
            @Override
            public ListCell<String> call(ListView<String> listView) {
                return new ListViewCell();
            }
        });
    }

    @FXML
    public void initialize() {
        setListView();

        listView.getSelectionModel().selectionModeProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                System.out.println("1111111111");
            }
        });
        listView.cellFactoryProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                System.out.println("1111111111");
            }
        });
        listView.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
               log.info("当前选择："+listView.getSelectionModel().getSelectedIndex());
                ObservableList selectedItems = listView.getSelectionModel().getSelectedItems();
            }
        });

    }
}