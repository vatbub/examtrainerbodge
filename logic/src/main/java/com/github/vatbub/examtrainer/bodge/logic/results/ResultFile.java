package com.github.vatbub.examtrainer.bodge.logic.results;

import com.github.vatbub.common.core.Common;
import com.github.vatbub.examtrainer.bodge.logic.Question;
import com.github.vatbub.examtrainer.bodge.logic.QuestionFile;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;
import java.util.Properties;

public class ResultFile {
    public static final String fileExtension = "etr";
    public static final String questionsFileName = "questions." + QuestionFile.fileExtension;
    public static final String resultsFileName = "results.properties";
    private QuestionFile questionFile;
    private Map<Integer, Boolean> results;
    private File originalFile;

    public ResultFile(File file) throws IOException, ZipException {
        originalFile = file;
        readFile(file);
    }

    public ResultFile(QuestionFile questionFile, File fileToSaveIn) {
        originalFile = fileToSaveIn;
        setQuestionFile(questionFile);
    }

    private void readFile(File fileToRead) throws IOException, ZipException {
        close();

        // Unzip
        getOriginalZipFile().extractAll(getTemporaryUnzipLocation());

        // read questions
        setQuestionFile(createQuestionFile());

        // read results
        Properties properties = new Properties();
        FileReader fileReader = new FileReader(new File(getTemporaryUnzipLocation()).toPath().resolve(resultsFileName).toFile());
        properties.load(fileReader);
        fileReader.close();

        for (Map.Entry<Object, Object> property : properties.entrySet()) {
            getResults().put(Integer.parseInt((String) property.getKey()), Boolean.parseBoolean((String) property.getValue()));
        }

        close();
    }

    private QuestionFile createQuestionFile() throws ZipException, IOException {
        return new QuestionFile(new File(getTemporaryUnzipLocation()).toPath().resolve(questionsFileName).toString());
    }

    public void save() throws IOException, ZipException {
        File tempFolder = new File(getTemporaryUnzipLocation());
        Files.createDirectory(tempFolder.toPath());

        // save copy of questions
        QuestionFile copy = createQuestionFile();
        copy.setQuestions(getQuestionFile().getQuestions());
        copy.save();

        // save results
        Properties properties = new Properties();

        for (Map.Entry<Integer, Boolean> result : getResults().entrySet()) {
            properties.put(result.getKey(), result.getValue());
        }

        FileWriter fileWriter = new FileWriter(new File(getTemporaryUnzipLocation()).toPath().resolve(resultsFileName).toFile());
        properties.store(fileWriter, "results");
        fileWriter.close();

        if (getOriginalFile().exists())
            Files.delete(getOriginalFile().toPath());

        ZipParameters zipParameters = new ZipParameters();
        zipParameters.setIncludeRootFolder(false);
        getOriginalZipFile().createZipFileFromFolder(getTemporaryUnzipLocation(), zipParameters, false, 0);
        close();
    }

    public void close() throws IOException {
        File tempFolder = new File(getTemporaryUnzipLocation());
        if (tempFolder.exists())
            FileUtils.deleteDirectory(tempFolder);
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
        return Common.getInstance().getAndCreateAppDataPathAsFile().toPath().resolve("results").resolve(getOriginalFile().getName().replace("." + fileExtension, "")).toAbsolutePath().toString();
    }

    public Map<Integer, Boolean> getResults() {
        return results;
    }

    public void setResults(Map<Integer, Boolean> results) {
        this.results = results;
        checkMapIndeces();
    }

    private void checkMapIndeces() {
        for (Map.Entry<Integer, Boolean> res : getResults().entrySet()) {
            if (!getQuestionFile().containsId(res.getKey()))
                throw new IllegalArgumentException("Found question id in results that is not contained in the question file: " + res.getKey());
        }
    }

    public QuestionFile getQuestionFile() {
        return questionFile;
    }

    private void setQuestionFile(QuestionFile questionFile) {
        this.questionFile = questionFile;
        checkMapIndeces();
    }
}
