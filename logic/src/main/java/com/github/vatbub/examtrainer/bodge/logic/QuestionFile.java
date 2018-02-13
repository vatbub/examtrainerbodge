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
    public static final String fileExtension = "etq";
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
        close();
        Files.createDirectory(new File(getTemporaryUnzipLocation()).toPath());
    }

    private void readFile() throws ZipException, IOException {
        close();

        // Unzip
        getOriginalZipFile().extractAll(getTemporaryUnzipLocation());

        // read questions
        for (File file : Objects.requireNonNull(Objects.requireNonNull(new File(getTemporaryUnzipLocation()).listFiles())[0].listFiles())) {
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

    public void close() throws IOException {
        Files.deleteIfExists(new File(getTemporaryUnzipLocation()).toPath());
    }

    public void save() throws IOException, ZipException {
        for (Question question : getQuestions()) {
            question.save(new File(getTemporaryUnzipLocation()).toPath().resolve(question.getTargetFileName()).toFile());
        }

        if (getOriginalFile().exists())
            Files.delete(getOriginalFile().toPath());

        ZipParameters zipParameters = new ZipParameters();
        getOriginalZipFile().createZipFileFromFolder(getTemporaryUnzipLocation(), zipParameters, false, 0);
    }

    public Question createNewQuestion(Question.Types type) {
        Question res = Question.newInstance(type);
        res.setId(getNextId());
        getQuestions().add(res);
        return res;
    }

    private int getNextId() {
        int currentMax = Integer.MIN_VALUE;
        for (Question question : getQuestions()) {
            if (currentMax < question.getId())
                currentMax = question.getId();
        }
        return currentMax + 1;
    }
}
