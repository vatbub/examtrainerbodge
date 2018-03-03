package com.github.vatbub.examtrainer.bodge.logic;

/*-
 * #%L
 * examtrainer.bodge.logic
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


import com.github.vatbub.common.core.Common;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class QuestionFile {
    public static final String fileExtension = "etq";
    private File originalFile;
    private List<Question> questions = new ArrayList<>();
    private int random = (int) (Math.random() * 100000);

    public QuestionFile(String fileToRead) throws ZipException, IOException {
        this(new File(fileToRead));
    }

    public QuestionFile(File fileToRead) throws ZipException, IOException {
        originalFile = fileToRead;

        if (getOriginalFile().exists())
            readFile();
        else
            createFile();
    }

    private void createFile() throws IOException {
        close();
        Files.createDirectory(new File(getTemporaryUnzipLocation()).toPath());
    }

    private void readFile() throws ZipException, IOException {
        close();

        // Unzip
        getOriginalZipFile().extractAll(getTemporaryUnzipLocation());

        // read questions
        for (File file : Objects.requireNonNull(new File(getTemporaryUnzipLocation()).listFiles())) {
            getQuestions().add(Question.fromFile(file));
        }

        close();
    }

    private ZipFile getOriginalZipFile() throws ZipException {
        ZipFile zipFile = new ZipFile(getOriginalFile());
        zipFile.setRunInThread(false);
        return zipFile;
    }

    public File getOriginalFile() {
        return originalFile;
    }

    private String getTemporaryUnzipLocation() {
        return Common.getInstance().getAndCreateAppDataPathAsFile().toPath().resolve(Integer.toHexString(random)).toAbsolutePath().toString();
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    public void close() throws IOException {
        File tempFolder = new File(getTemporaryUnzipLocation());
        if (tempFolder.exists())
            FileUtils.deleteDirectory(tempFolder);
    }

    public void save(File fileToSaveIn) throws IOException, ZipException {
        originalFile = fileToSaveIn;
        save();
    }

    public void save() throws IOException, ZipException {
        close();
        File tempFolder = new File(getTemporaryUnzipLocation());
        Files.createDirectory(tempFolder.toPath());
        for (Question question : getQuestions()) {
            question.save(tempFolder.toPath().resolve(question.getTargetFileName()).toFile());
        }

        if (getOriginalFile().exists())
            Files.delete(getOriginalFile().toPath());

        ZipParameters zipParameters = new ZipParameters();
        zipParameters.setIncludeRootFolder(false);
        getOriginalZipFile().createZipFileFromFolder(getTemporaryUnzipLocation(), zipParameters, false, 0);
        close();
    }

    public Question createNewQuestion(Question.Types type) {
        Question res = Question.newInstance(type);
        res.setId(getNextId());
        getQuestions().add(res);
        return res;
    }

    public boolean containsId(int id) {
        for (Question question : getQuestions()) {
            if (question.getId() == id)
                return true;
        }
        return false;
    }

    private int getNextId() {
        int currentMax = 0;
        for (Question question : getQuestions()) {
            if (currentMax < question.getId())
                currentMax = question.getId();
        }
        return currentMax + 1;
    }

    @Override
    public boolean equals(Object obj) {
        return obj != null && obj instanceof QuestionFile && ((QuestionFile) obj).originalFile.equals(this.originalFile);

    }
}
