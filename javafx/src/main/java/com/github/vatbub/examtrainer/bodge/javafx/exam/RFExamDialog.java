package com.github.vatbub.examtrainer.bodge.javafx.exam;

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
import com.github.vatbub.examtrainer.bodge.logic.RFQuestion;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;
import net.lingala.zip4j.exception.ZipException;

import java.io.IOException;

public class RFExamDialog {
    private Question question;
    private Stage stage;
    private ExamManager examManager;
    private ExamManager.AfterQuestionAskedRunnable onOkClicked;
    private ExamManager.AfterQuestionAskedRunnable onCancelClicked;
    private boolean initialized = false;

    @FXML
    private Label questionTextLabel;

    @FXML
    private ToggleGroup answer;

    @FXML
    private RadioButton wrongRadioButton;

    @FXML
    private RadioButton rightRadioButton;

    @FXML
    private ToggleButton turboModeToggleButton;

    public static RFExamDialog show(Question question, ExamManager examManager, ExamManager.AfterQuestionAskedRunnable onOkClicked, ExamManager.AfterQuestionAskedRunnable onCancelClicked) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(RFExamDialog.class.getResource("RFExamDialog.fxml"));
        Parent root = fxmlLoader.load();
        RFExamDialog controller = fxmlLoader.getController();

        controller.setQuestion(question);
        controller.setExamManager(examManager);
        controller.setOnOkClicked(onOkClicked);
        controller.setOnCancelClicked(onCancelClicked);

        controller.stage = new Stage();
        Scene scene = new Scene(root);
        controller.stage.setScene(scene);
        controller.stage.show();

        return controller;
    }

    @FXML
    void okButtonOnAction(ActionEvent event) throws IOException, ZipException {
        stage.hide();
        getOnOkClicked().run(getQuestion(), getAnswer());
    }

    private RFQuestion.RFAnswer getAnswer(){
        RFQuestion.RFAnswer answer = new RFQuestion.RFAnswer();
        answer.setValue(rightRadioButton.isSelected());
        return answer;
    }

    @FXML
    void cancelButtonOnAction(ActionEvent event) throws IOException, ZipException {
        stage.hide();
        getOnCancelClicked().run(getQuestion(), getAnswer());
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
        if (initialized)
            loadQuestionToUI();
    }

    public ExamManager getExamManager() {
        return examManager;
    }

    public void setExamManager(ExamManager examManager) {
        this.examManager = examManager;
    }

    public ExamManager.AfterQuestionAskedRunnable getOnOkClicked() {
        return onOkClicked;
    }

    public void setOnOkClicked(ExamManager.AfterQuestionAskedRunnable onOkClicked) {
        this.onOkClicked = onOkClicked;
    }

    public ExamManager.AfterQuestionAskedRunnable getOnCancelClicked() {
        return onCancelClicked;
    }

    public void setOnCancelClicked(ExamManager.AfterQuestionAskedRunnable onCancelClicked) {
        this.onCancelClicked = onCancelClicked;
    }

    @FXML
    void initialize() {
        initialized = true;
        if (getQuestion()!=null)
            loadQuestionToUI();
    }

    private void loadQuestionToUI(){
        questionTextLabel.setText(getQuestion().getQuestionText());
    }
}
