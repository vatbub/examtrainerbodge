package com.github.vatbub.examtrainer.bodge.logic;

import com.github.vatbub.common.core.Common;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class QuestionFile {
    private static final String fileExtension = "etq";
    private File originalFile;
    private List<Question> questions = new ArrayList<>();

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
        Files.createDirectory(new File(getTemporaryUnzipLocation()).toPath());
    }

    private void readFile() throws ZipException, IOException {
        // Unzip
        getOriginalZipFile().extractAll(getTemporaryUnzipLocation());

        // read questions
        for(File file : Objects.requireNonNull(new File(getTemporaryUnzipLocation()).listFiles())){
            getQuestions().add(Question.fromFile(file));
        }
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
        return Common.getInstance().getAndCreateAppDataPathAsFile().toPath().resolve(getOriginalFile().getName().replace("." + fileExtension, "")).toAbsolutePath().toString();
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    public void save() throws IOException, ZipException {
        for(Question question:getQuestions()){
            question.save(new File(getTemporaryUnzipLocation()).toPath().resolve(question.getTargetFileName()).toFile());
        }

        Files.delete(getOriginalFile().toPath());

        getOriginalZipFile().createZipFileFromFolder(getTemporaryUnzipLocation(), new ZipParameters(), false, 0);
    }
}
