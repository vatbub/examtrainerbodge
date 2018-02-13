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


import com.github.vatbub.examtrainer.bodge.logic.QuestionFile;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.stage.FileChooser;
import net.lingala.zip4j.exception.ZipException;

import java.io.File;
import java.io.IOException;

public class MainWindow {
    private QuestionFile currentFile;

    @FXML
    private MenuItem saveFileMenuItem;

    @FXML
    private Button launchExamButton;

    @FXML
    private MenuItem saveAsFileMenuItem;

    @FXML
    private Button editQuestionsButton;

    @FXML
    void newFileMenuItemOnAction(ActionEvent event) throws ZipException, IOException {
        File fileToSaveIn = showSaveAsDialog();
        if (fileToSaveIn == null) return;
        setCurrentFile(new QuestionFile(fileToSaveIn));
    }

    private File showSaveAsDialog() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save questions file");

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Question Files", "*." + QuestionFile.fileExtension)
        );

        return fileChooser.showSaveDialog(EntryClass.getCurrentEntryClassInstance().getStage());
    }

    @FXML
    void openFileMenuItemOnAction(ActionEvent event) throws ZipException, IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open questions file");

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Question Files", "*." + QuestionFile.fileExtension)
        );

        File openedFile = fileChooser.showOpenDialog(EntryClass.getCurrentEntryClassInstance().getStage());
        if (openedFile != null)
            setCurrentFile(new QuestionFile(openedFile));
    }

    @FXML
    void saveFileMenuItemOnAction(ActionEvent event) throws IOException, ZipException {
        getCurrentFile().save();
    }

    @FXML
    void saveAsFileMenuItemOnAction(ActionEvent event) {

    }

    @FXML
    void closeMenuItemOnAction(ActionEvent event) {

    }

    @FXML
    void launchExamButtonOnAction(ActionEvent event) {

    }

    @FXML
    void viewSingleResultButtonOnAction(ActionEvent event) {

    }

    @FXML
    void viewOverallPerformanceButtonOnAction(ActionEvent event) {

    }

    @FXML
    void editQuestionsButtonOnAction(ActionEvent event) throws IOException {
        QuestionEditor.show(getCurrentFile());
    }

    public QuestionFile getCurrentFile() {
        return currentFile;
    }

    public void setCurrentFile(QuestionFile currentFile) throws IOException {
        if (getCurrentFile() != null)
            getCurrentFile().close();

        this.currentFile = currentFile;
        updateButtonEnabledStatus(currentFile == null);
    }

    private void updateButtonEnabledStatus(boolean isDisabled) {
        launchExamButton.setDisable(isDisabled);
        editQuestionsButton.setDisable(isDisabled);
        saveFileMenuItem.setDisable(isDisabled);
        saveAsFileMenuItem.setDisable(isDisabled);
    }
}
