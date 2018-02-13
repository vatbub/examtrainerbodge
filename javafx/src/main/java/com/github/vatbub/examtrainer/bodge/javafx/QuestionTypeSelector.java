package com.github.vatbub.examtrainer.bodge.javafx;

import com.github.vatbub.examtrainer.bodge.logic.Question;
import com.github.vatbub.examtrainer.bodge.logic.QuestionFile;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Observable;

public class QuestionTypeSelector {
    private TypeSelectorCallback callback;
    private Stage stage;
    @FXML
    private ChoiceBox<Question.Types> typeSelector;

    @FXML
    void okButtonOnAction(ActionEvent event) throws IOException {
        if (callback!=null)
            callback.onOkSelected(typeSelector.getSelectionModel().getSelectedItem());
        hide();
    }

    @FXML
    void cancelButtonOnAction(ActionEvent event) {
        if (callback != null)
            callback.onCancelled();
        hide();
    }

    public static QuestionTypeSelector show(TypeSelectorCallback callback, Question.Types preselectedType) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(QuestionEditor.class.getResource("QuestionTypeSelector.fxml"));
        Parent root = fxmlLoader.load();
        QuestionTypeSelector controller = fxmlLoader.getController();

        controller.setCallback(callback);

        controller.typeSelector.setItems(FXCollections.observableArrayList(Question.Types.values()));

        controller.typeSelector.getSelectionModel().select(preselectedType);

        controller.stage = new Stage();
        Scene scene = new Scene(root);
        controller.stage.setScene(scene);
        controller.stage.show();

        return controller;
    }

    public TypeSelectorCallback getCallback() {
        return callback;
    }

    public void hide() {
        stage.hide();
    }

    public void setCallback(TypeSelectorCallback callback) {
        this.callback = callback;
    }

    public interface TypeSelectorCallback {
        void onOkSelected(Question.Types selectedType) throws IOException;

        void onCancelled();
    }
}
