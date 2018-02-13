package com.github.vatbub.examtrainer.bodge.logic;

import javafx.animation.KeyFrame;

import java.io.*;
import java.util.Properties;

public abstract class Question {
    private static final String fileExtension = "question";

    public abstract Answer getCorrectAnswer();

    public abstract void setCorrectAnswer(Answer correctAnswer);

    protected static Question fromFile(File fileToRead) throws IOException {
        Properties properties = new Properties();
        properties.load(new FileReader(fileToRead));

        Types type = Types.valueOf(properties.getProperty(Keys.TYPE));

        Question res = null;

        switch (type) {
            case RF:
                res = new RFQuestion();
                break;
            case TEXT:
                res = new TextQuestion();
                break;
        }

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

        properties.store(new FileWriter(fileToSaveIn), "question file");
    }

    protected String getTargetFileName() {
        return getId() + "." + fileExtension;
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
