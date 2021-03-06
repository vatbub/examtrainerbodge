package com.github.vatbub.examtrainer.bodge.logic.results;

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


import com.github.vatbub.common.core.logging.FOKLogger;
import com.github.vatbub.examtrainer.bodge.logic.QuestionFile;
import net.lingala.zip4j.exception.ZipException;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;

public class ResultListFile {
    public static final String FILE_EXTENSION = "etrlist";
    private static final String PROPERTIES_LIST_KEY = "files";
    private static final String PROPERTIES_IS_RELATIVE_KEY = "isRelative";
    private static final String FILES_DELIMITER = ";";
    private List<ResultFile> resultFiles;
    private File originalFile;

    public ResultListFile() throws IOException, ZipException {
        this(null);
    }

    public ResultListFile(File fileToRead) throws IOException, ZipException {
        setOriginalFile(fileToRead);
        if (fileToRead != null && fileToRead.exists())
            readFile();
        else{
            setResultFiles(new ArrayList<>());
        }
    }

    private void readFile() throws IOException, ZipException {
        Properties properties = new Properties();
        FileReader fileReader = new FileReader(getOriginalFile());
        properties.load(fileReader);
        fileReader.close();

        setResultFiles(new ArrayList<>());

        boolean isRelative = Boolean.parseBoolean(properties.getProperty(PROPERTIES_IS_RELATIVE_KEY, Boolean.toString(true)));
        String fileList = properties.getProperty(PROPERTIES_LIST_KEY, null);
        if (fileList != null) {
            Path thisPath = getOriginalFile().toPath();
            for (String file : fileList.split(FILES_DELIMITER)) {
                File resolvedFile;
                if (isRelative)
                    resolvedFile = thisPath.resolve(file).toFile();
                else
                    resolvedFile = new File(file);

                getResultFiles().add(new ResultFile(resolvedFile));
            }
        }
    }

    public void save(File fileToSaveIn) throws IOException {
        setOriginalFile(fileToSaveIn);
        save();
    }

    public void save() throws IOException {
        Properties properties = new Properties();

        if (!getResultFiles().isEmpty()) {
            ProcessedFileNameList processedFileNameList = processFileNames(false);

            StringBuilder stringBuilder = new StringBuilder(processedFileNameList.get(0));

            for (int i = 1; i < processedFileNameList.size(); i++) {
                stringBuilder.append(FILES_DELIMITER).append(processedFileNameList.get(i));
            }

            properties.put(PROPERTIES_LIST_KEY, stringBuilder.toString());
            properties.put(PROPERTIES_IS_RELATIVE_KEY, Boolean.toString(processedFileNameList.isRelative));
        }

        FileWriter fileWriter = new FileWriter(getOriginalFile());
        properties.store(fileWriter, "result file list");
        fileWriter.close();
    }


    private ProcessedFileNameList processFileNames(boolean forceAbsolute) {
        ProcessedFileNameList res = new ProcessedFileNameList();
        res.setRelative(!forceAbsolute);

        if (forceAbsolute) {
            for (ResultFile resultFile : getResultFiles()) {
                res.add(resultFile.getOriginalFile().getAbsolutePath());
            }

            return res;
        }

        // try relative
        try {
            Path thisPath = getOriginalFile().toPath();
            for (ResultFile resultFile : getResultFiles()) {
                res.add(thisPath.relativize(resultFile.getOriginalFile().toPath()).toString());
            }

            return res;
        } catch (IllegalArgumentException e) {
            FOKLogger.log(getClass().getName(), Level.SEVERE, "Unable to process relative paths, using absolute paths...", e);
            return processFileNames(true);
        }
    }

    public List<ResultFile> getResultFiles() {
        return resultFiles;
    }

    public void setResultFiles(List<ResultFile> resultFiles) {
        this.resultFiles = resultFiles;
    }

    public File getOriginalFile() {
        return originalFile;
    }

    private void setOriginalFile(File originalFile) {
        this.originalFile = originalFile;
    }

    /**
     * Returns the master question list
     * @return The master question list or {@code null} if no results are containes in this result list file.
     */
    public QuestionFile getMasterQuestionFile(){
        if (getResultFiles().isEmpty())
            return null;
        return getResultFiles().get(0).getQuestionFile();
    }

    private class ProcessedFileNameList extends ArrayList<String> {
        private boolean isRelative;

        public boolean isRelative() {
            return isRelative;
        }

        public void setRelative(boolean relative) {
            isRelative = relative;
        }
    }
}
