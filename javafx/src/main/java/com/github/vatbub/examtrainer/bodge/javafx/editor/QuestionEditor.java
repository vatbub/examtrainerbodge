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
import com.github.vatbub.examtrainer.bodge.logic.QuestionFile;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import net.lingala.zip4j.exception.ZipException;

import java.io.IOException;

public class QuestionEditor {
    private QuestionFile questionFile;
    @FXML
    private Button editQuestionButton;

    @FXML
    private Button deleteQuestionButton;

    @FXML
    private TableView<Question> questionTable;

    public static QuestionEditor show(QuestionFile questionFile) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(QuestionEditor.class.getResource("QuestionEditor.fxml"));
        Parent root = fxmlLoader.load();
        QuestionEditor controller = fxmlLoader.getController();

        controller.setQuestionFile(questionFile);

        Stage primaryStage = new Stage();
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);

        primaryStage.show();

        return controller;
    }

    @FXML
    void initialize() {
        assert editQuestionButton != null : "fx:id=\"editQuestionButton\" was not injected: check your FXML file 'QuestionEditor.fxml'.";
        assert deleteQuestionButton != null : "fx:id=\"deleteQuestionButton\" was not injected: check your FXML file 'QuestionEditor.fxml'.";
        assert questionTable != null : "fx:id=\"questionTable\" was not injected: check your FXML file 'QuestionEditor.fxml'.";
    }

    @SuppressWarnings("unchecked")
    public void reloadQuestions(){
        javafx.scene.control.TableColumn<Question, Integer> idCol = (javafx.scene.control.TableColumn<Question, Integer>) questionTable.getColumns().get(0);
        javafx.scene.control.TableColumn<Question, String> questionCol = (javafx.scene.control.TableColumn<Question, String>) questionTable.getColumns().get(1);

        idCol.setCellValueFactory(new PropertyValueFactory<Question, Integer>("id"));
        questionCol.setCellValueFactory(new PropertyValueFactory<Question, String>("questionText"));

        questionTable.setItems(FXCollections.observableArrayList(getQuestionFile().getQuestions()));
    }

    @FXML
    void saveButtonOnAction(ActionEvent event) throws IOException, ZipException {
        getQuestionFile().save();
    }

    @FXML
    void newQuestionButtonOnAction(ActionEvent event) throws IOException {
        QuestionTypeSelector.show(new QuestionTypeSelector.TypeSelectorCallback() {
            @Override
            public void onOkSelected(Question.Types selectedType) throws IOException {
                editQuestion(getQuestionFile().createNewQuestion(selectedType));
                reloadQuestions();
            }

            @Override
            public void onCancelled() {
                // do nothing
            }
        }, Question.Types.RF);
    }

    private void editQuestion(Question question) throws IOException {
        switch (question.getType()) {
            case RF:
                RFEditor.show(question, this::reloadQuestions);
                break;
            case TEXT:
                TextEditor.show(question, this::reloadQuestions);
                break;
        }
    }

    @FXML
    void editQuestionButtonOnAction(ActionEvent event) throws IOException {
        editQuestion(questionTable.getSelectionModel().getSelectedItem());
    }

    @FXML
    void deleteQuestionButtonOnAction(ActionEvent event) {
        getQuestionFile().getQuestions().remove(questionTable.getSelectionModel().getSelectedItem());
        reloadQuestions();
    }

    public QuestionFile getQuestionFile() {
        return questionFile;
    }

    public void setQuestionFile(QuestionFile questionFile) {
        this.questionFile = questionFile;
        reloadQuestions();
    }
}
