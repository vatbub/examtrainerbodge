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
import com.github.vatbub.examtrainer.bodge.javafx.exam.ExamManager;
import com.github.vatbub.examtrainer.bodge.logic.QuestionFile;
import com.github.vatbub.examtrainer.bodge.logic.results.ResultFile;
import com.github.vatbub.examtrainer.bodge.logic.results.ResultListFile;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.stage.FileChooser;
import net.lingala.zip4j.exception.ZipException;

import java.io.File;
import java.io.IOException;

public class MainWindow {
    private QuestionFile currentQuestionFile;
    private ResultListFile currentResultListFile;

    @FXML
    private MenuItem saveFileMenuItem;

    @FXML
    private Button launchExamButton;

    @FXML
    private MenuItem saveAsFileMenuItem;

    @FXML
    private Button editQuestionsButton;

    @FXML
    void newQuestionFileMenuItemOnAction(ActionEvent event) throws ZipException, IOException {
        File fileToSaveIn = showSaveAsDialogForQuestionFile();
        if (fileToSaveIn == null) return;
        setCurrentQuestionFile(new QuestionFile(fileToSaveIn));
    }

    private File showSaveAsDialogForQuestionFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save questions file");

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Question Files", "*." + QuestionFile.fileExtension)
        );

        return fileChooser.showSaveDialog(EntryClass.getCurrentEntryClassInstance().getStage());
    }

    @FXML
    void openQuestionFileMenuItemOnAction(ActionEvent event) throws ZipException, IOException {
        openQuestionFile();
    }

    private void openQuestionFile() throws ZipException, IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open questions file");

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Question Files", "*." + QuestionFile.fileExtension)
        );

        File openedFile = fileChooser.showOpenDialog(EntryClass.getCurrentEntryClassInstance().getStage());
        if (openedFile != null)
            setCurrentQuestionFile(new QuestionFile(openedFile));
    }

    @FXML
    void saveQuestionFileMenuItemOnAction(ActionEvent event) throws IOException, ZipException {
        getCurrentQuestionFile().save();
    }

    @FXML
    void saveAsQuestionFileMenuItemOnAction(ActionEvent event) throws IOException, ZipException {
        File fileToSaveIn = showSaveAsDialogForQuestionFile();
        if (fileToSaveIn == null) return;
        getCurrentQuestionFile().save(fileToSaveIn);
    }

    @FXML
    void newResultFileMenuItemOnAction(ActionEvent event) throws ZipException, IOException {
        newResultListFile();
    }

    private void newResultListFile() throws IOException, ZipException {
        File fileToSaveIn = showSaveAsDialogForResultListFile();
        if (fileToSaveIn == null) return;
        setCurrentResultListFile(new ResultListFile(fileToSaveIn));
    }

    private File showSaveAsDialogForResultListFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save result file");

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Result list Files", "*." + ResultListFile.FILE_EXTENSION)
        );

        return fileChooser.showSaveDialog(EntryClass.getCurrentEntryClassInstance().getStage());
    }

    @FXML
    void openResultFileMenuItemOnAction(ActionEvent event) throws ZipException, IOException {
        openResultFile();
    }

    private void openResultFile() throws IOException, ZipException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open result file");

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Result list Files", "*." + ResultListFile.FILE_EXTENSION)
        );

        File openedFile = fileChooser.showOpenDialog(EntryClass.getCurrentEntryClassInstance().getStage());
        if (openedFile != null)
            setCurrentResultListFile(new ResultListFile(openedFile));
    }

    @FXML
    void saveResultFileMenuItemOnAction(ActionEvent event) throws IOException, ZipException {
        getCurrentResultListFile().save();
    }

    @FXML
    void saveAsResultFileMenuItemOnAction(ActionEvent event) throws IOException {
        File fileToSaveIn = showSaveAsDialogForResultListFile();
        if (fileToSaveIn == null) return;
        getCurrentResultListFile().save(fileToSaveIn);
    }

    @FXML
    void closeMenuItemOnAction(ActionEvent event) {
        Platform.exit();
    }

    @FXML
    void launchExamButtonOnAction(ActionEvent event) throws ZipException, IOException {
        if (getCurrentQuestionFile() == null)
            openQuestionFile();

        if (getCurrentResultListFile() == null || (getCurrentResultListFile().getMasterQuestionFile()!=null && !getCurrentQuestionFile().equals(getCurrentResultListFile().getMasterQuestionFile()))) {
            CreateResultListFileDialog.show(() -> {
                try {
                    newResultListFile();
                    launchExamButtonOnAction(event);
                } catch (IOException | ZipException e) {
                    e.printStackTrace();
                }
            }, () -> {
                try {
                    openResultFile();
                    launchExamButtonOnAction(event);
                } catch (IOException | ZipException e) {
                    e.printStackTrace();
                }
            });
            return;
        }

        ResultFile newResultFileForExam = newResultFile();
        if (newResultFileForExam == null) return;

        new ExamManager(getCurrentResultListFile(), newResultFileForExam).startExam();
    }

    private ResultFile newResultFile() throws IOException, ZipException {
        File fileToSaveIn = showSaveAsDialogForResultListFile();
        if (fileToSaveIn == null) return null;
        return new ResultFile(getCurrentQuestionFile(), fileToSaveIn);
    }

    private File showSaveAsDialogForResultFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save result file");

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Result list Files", "*." + ResultListFile.FILE_EXTENSION)
        );

        return fileChooser.showSaveDialog(EntryClass.getCurrentEntryClassInstance().getStage());
    }

    @FXML
    void viewSingleResultButtonOnAction(ActionEvent event) {

    }

    @FXML
    void viewOverallPerformanceButtonOnAction(ActionEvent event) {

    }

    @FXML
    void editQuestionsButtonOnAction(ActionEvent event) throws IOException, ZipException {
        if (getCurrentQuestionFile() == null)
            openQuestionFile();
        QuestionEditor.show(getCurrentQuestionFile());
    }

    public QuestionFile getCurrentQuestionFile() {
        return currentQuestionFile;
    }

    public void setCurrentQuestionFile(QuestionFile currentQuestionFile) throws IOException {
        if (getCurrentQuestionFile() != null)
            getCurrentQuestionFile().close();

        this.currentQuestionFile = currentQuestionFile;
    }

    public ResultListFile getCurrentResultListFile() {
        return currentResultListFile;
    }

    public void setCurrentResultListFile(ResultListFile currentResultListFile) {
        this.currentResultListFile = currentResultListFile;
    }
}
