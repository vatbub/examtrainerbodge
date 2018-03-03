package com.github.vatbub.examtrainer.bodge.javafx;

import com.github.vatbub.examtrainer.bodge.javafx.editor.QuestionEditor;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class CreateResultListFileDialog {
    private Runnable onNewFileClicked;
    private Runnable onOpenFileClicked;
    private Stage stage;

    public static CreateResultListFileDialog show(Runnable onNewFileClicked, Runnable onOpenFileClicked) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(CreateResultListFileDialog.class.getResource("CreateResultListFileDialog.fxml"));
        Parent root = fxmlLoader.load();
        CreateResultListFileDialog controller = fxmlLoader.getController();

        controller.setOnNewFileClicked(onNewFileClicked);
        controller.setOnOpenFileClicked(onOpenFileClicked);

        controller.stage = new Stage();
        Scene scene = new Scene(root);
        controller.stage.setScene(scene);
        controller.stage.show();

        return controller;
    }

    @FXML
    void createNewFileButtonOnAction(ActionEvent event) {
        stage.hide();
        getOnNewFileClicked().run();
    }

    @FXML
    void openFileButtonOnAction(ActionEvent event) {
        stage.hide();
        getOnOpenFileClicked().run();
    }

    public Runnable getOnNewFileClicked() {
        return onNewFileClicked;
    }

    public void setOnNewFileClicked(Runnable onNewFileClicked) {
        this.onNewFileClicked = onNewFileClicked;
    }

    public Runnable getOnOpenFileClicked() {
        return onOpenFileClicked;
    }

    public void setOnOpenFileClicked(Runnable onOpenFileClicked) {
        this.onOpenFileClicked = onOpenFileClicked;
    }
}
