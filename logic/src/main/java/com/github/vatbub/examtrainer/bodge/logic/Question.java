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


import javafx.animation.KeyFrame;

import java.io.*;
import java.util.Properties;

public abstract class Question {
    private static final String fileExtension = "question";

    public abstract Answer getCorrectAnswer();

    public abstract void setCorrectAnswer(Answer correctAnswer);

    protected static Question newInstance(Types type) {
        Question res = null;

        switch (type) {
            case RF:
                res = new RFQuestion();
                break;
            case TEXT:
                res = new TextQuestion();
                break;
        }
        return res;
    }

    protected static Question fromFile(File fileToRead) throws IOException {
        Properties properties = new Properties();
        FileReader fileReader = new FileReader(fileToRead);
        properties.load(fileReader);
        fileReader.close();

        Types type = Types.valueOf(properties.getProperty(Keys.TYPE));

        Question res = newInstance(type);

        res.id = Integer.parseInt(properties.getProperty(Keys.ID));
        res.setQuestionText(properties.getProperty(Keys.QUESTION_TEXT));
        res.setTextIfWrongAnswer(properties.getProperty(Keys.TEXT_IF_WRONG_ANSWER));
        if (res.getTextIfWrongAnswer().isEmpty())
            res.setTextIfWrongAnswer(null);

        res.setNumberOfPoints(Integer.parseInt(properties.getProperty(Keys.NUMBER_OF_POINTS)));
        res.setMarked(Boolean.parseBoolean(properties.getProperty(Keys.MARKED)));
        res.parseAndSetCorrectAnswer(properties.getProperty(Keys.CORRECT_ANSWER));

        return res;
    }

    private int id;
    private String questionText;
    private String textIfWrongAnswer;
    private int numberOfPoints;
    private boolean marked;

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public String getTextIfWrongAnswer() {
        return textIfWrongAnswer;
    }

    public void setTextIfWrongAnswer(String textIfWrongAnswer) {
        this.textIfWrongAnswer = textIfWrongAnswer;
    }

    public int getNumberOfPoints() {
        return numberOfPoints;
    }

    public void setNumberOfPoints(int numberOfPoints) {
        this.numberOfPoints = numberOfPoints;
    }

    public boolean isMarked() {
        return marked;
    }

    public void setMarked(boolean marked) {
        this.marked = marked;
    }

    public int getId() {
        return id;
    }

    public abstract Types getType();

    protected abstract void parseAndSetCorrectAnswer(String stringToParse);

    protected void save(File fileToSaveIn) throws IOException {
        Properties properties = new Properties();
        properties.setProperty(Keys.ID, Integer.toString(getId()));
        properties.setProperty(Keys.TYPE, getType().toString());
        properties.setProperty(Keys.QUESTION_TEXT, getQuestionText());
        properties.setProperty(Keys.TEXT_IF_WRONG_ANSWER, getTextIfWrongAnswer() != null ? getTextIfWrongAnswer() : "");
        properties.setProperty(Keys.NUMBER_OF_POINTS, Integer.toString(getNumberOfPoints()));
        properties.setProperty(Keys.MARKED, Boolean.toString(isMarked()));
        properties.setProperty(Keys.CORRECT_ANSWER, getCorrectAnswer().toSaveString());

        FileWriter fileWriter = new FileWriter(fileToSaveIn);
        properties.store(fileWriter, "question file");
        fileWriter.close();
    }

    protected String getTargetFileName() {
        return getId() + "." + fileExtension;
    }

    protected void setId(int id){
        this.id = id;
    }

    public enum Types {
        RF, TEXT
    }

    private static class Keys {
        static final String ID = "id";
        static final String TYPE = "type";
        static final String QUESTION_TEXT = "questionText";
        static final String TEXT_IF_WRONG_ANSWER = "textIfWrongAnswer";
        static final String NUMBER_OF_POINTS = "numberOfPoints";
        static final String MARKED = "marked";
        static final String CORRECT_ANSWER = "correctAnswer";
    }
}
