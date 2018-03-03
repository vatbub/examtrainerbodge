package com.github.vatbub.examtrainer.bodge.javafx.exam;

import com.github.vatbub.examtrainer.bodge.logic.Question;
import com.github.vatbub.examtrainer.bodge.logic.TextQuestion;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleButton;
import javafx.stage.Stage;
import net.lingala.zip4j.exception.ZipException;

import java.io.IOException;

public class TextExamDialog {
    private Question question;
    private ExamManager examManager;
    private Stage stage;
    private ExamManager.AfterQuestionAskedRunnable onOkClicked;
    private ExamManager.AfterQuestionAskedRunnable onCancelClicked;

    @FXML
    private Label questionTextLabel;

    @FXML
    private TextArea answerTextArea;

    @FXML
    private ToggleButton turboModeToggleButton;
    private boolean initialized = false;

    public static TextExamDialog show(Question question, ExamManager examManager, ExamManager.AfterQuestionAskedRunnable onOkClicked, ExamManager.AfterQuestionAskedRunnable onCancelClicked) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(TextExamDialog.class.getResource("RFExamDialog.fxml"));
        Parent root = fxmlLoader.load();
        TextExamDialog controller = fxmlLoader.getController();

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

    private TextQuestion.TextAnswer getAnswer(){
        TextQuestion.TextAnswer answer = new TextQuestion.TextAnswer();
        answer.setValue(answerTextArea.getText());
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
