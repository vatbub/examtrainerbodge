package com.github.vatbub.examtrainer.bodge.logic;

import com.github.vatbub.common.core.Common;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import org.apache.commons.io.FileUtils;
import oshi.util.FileUtil;
import sun.net.www.protocol.file.FileURLConnection;

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
        return Common.getInstance().getAndCreateAppDataPathAsFile().toPath().resolve(getOriginalFile().getName().replace("." + fileExtension, "")).toAbsolutePath().toString();
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

    public void save() throws IOException, ZipException {
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

    public boolean containsId(int id){
        for(Question question:getQuestions()){
            if (question.getId()==id)
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
}
