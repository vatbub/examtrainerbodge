package com.github.vatbub.examtrainer.bodge.javafx;

/*-
 * #%L
 * examtrainer.bodge.javafx
 * %%
 * Copyright (C) 2016 - 2018 Frederik Kammel
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */


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
