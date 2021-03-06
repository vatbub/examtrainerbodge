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
import com.github.vatbub.examtrainer.bodge.logic.TextQuestion;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class TextEditor {
    private Runnable afterEditCallback;
    private Question question;
    private Stage stage;

    @FXML
    private TextArea questionTextTextArea;

    @FXML
    private TextArea textIfWrongTextArea;

    @FXML
    private TextArea answerTextArea;

    @FXML
    private TextField numOfPointsTextField;

    public static TextEditor show(Question question, Runnable afterEditCallback) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(QuestionEditor.class.getResource("TextEditor.fxml"));
        Parent root = fxmlLoader.load();
        TextEditor controller = fxmlLoader.getController();

        controller.setQuestion(question);
        controller.setAfterEditCallback(afterEditCallback);

        controller.stage = new Stage();
        Scene scene = new Scene(root);
        controller.stage.setScene(scene);
        controller.stage.show();

        return controller;
    }

    @FXML
    void okButtonOnAction(ActionEvent event) {
        getQuestion().setQuestionText(questionTextTextArea.getText());
        getQuestion().setTextIfWrongAnswer(textIfWrongTextArea.getText());
        getQuestion().setNumberOfPoints(Integer.parseInt(numOfPointsTextField.getText()));

        TextQuestion.TextAnswer res = new TextQuestion.TextAnswer();
        res.setValue(answerTextArea.getText());
        getQuestion().setCorrectAnswer(res);
        hide();
    }

    private void hide() {
        stage.hide();
        if (getAfterEditCallback() != null)
            getAfterEditCallback().run();
    }

    @FXML
    void cancelButtonOnAction(ActionEvent event) {
        hide();
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
        if (question.getQuestionText() != null) {
            questionTextTextArea.setText(question.getQuestionText());
        }

        if (question.getTextIfWrongAnswer() != null) {
            textIfWrongTextArea.setText(question.getTextIfWrongAnswer());
        }

        numOfPointsTextField.setText(Integer.toString(question.getNumberOfPoints()));

        if (question.getCorrectAnswer() != null)
            answerTextArea.setText(((TextQuestion.TextAnswer) question.getCorrectAnswer()).getValue());
    }

    public Runnable getAfterEditCallback() {
        return afterEditCallback;
    }

    public void setAfterEditCallback(Runnable afterEditCallback) {
        this.afterEditCallback = afterEditCallback;
    }
}
