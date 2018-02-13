package com.github.vatbub.examtrainer.bodge.logic;

import java.awt.*;

public class TextQuestion extends Question {
    private TextAnswer correctAnswer;

    @Override
    public Answer getCorrectAnswer() {
        return correctAnswer;
    }

    @Override
    public void setCorrectAnswer(Answer correctAnswer) {
        this.correctAnswer = (TextAnswer) correctAnswer;
    }

    @Override
    public Types getType() {
        return Types.TEXT;
    }

    @Override
    protected void parseAndSetCorrectAnswer(String stringToParse) {
        TextAnswer res = new TextAnswer();
        res.setValue(stringToParse);
        setCorrectAnswer(res);
    }

    public static class TextAnswer implements Answer {
        private String value;

        @Override
        public EvaluationResult evaluateUserResponse(Answer userResponse) {
            if (!(userResponse instanceof TextAnswer))
                return EvaluationResult.INCORRECT;
            TextAnswer userAnswer = (TextAnswer) userResponse;

            if (userAnswer.getValue().equals(getValue()))
                return EvaluationResult.CORRECT;

            return EvaluationResult.INCONCLUSIVE;
        }

        @Override
        public String toSaveString() {
            return getValue();
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}
