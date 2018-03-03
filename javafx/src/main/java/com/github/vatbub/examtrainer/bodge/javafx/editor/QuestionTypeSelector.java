package com.github.vatbub.examtrainer.bodge.javafx.editor;

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


import com.github.vatbub.examtrainer.bodge.logic.Question;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.stage.Stage;

import java.io.IOException;

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
