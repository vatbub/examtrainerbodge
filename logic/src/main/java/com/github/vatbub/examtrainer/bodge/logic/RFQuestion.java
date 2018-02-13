package com.github.vatbub.examtrainer.bodge.logic;

public class RFQuestion extends Question {
    private RFAnswer correctAnswer;

    @Override
    public Answer getCorrectAnswer() {
        return correctAnswer;
    }

    @Override
    public void setCorrectAnswer(Answer correctAnswer) {
        this.correctAnswer = (RFAnswer) correctAnswer;
    }

    @Override
    public Types getType() {
        return Types.RF;
    }

    @Override
    protected void parseAndSetCorrectAnswer(String stringToParse) {
        RFAnswer res = new RFAnswer();
        res.setValue(Boolean.parseBoolean(stringToParse));
        setCorrectAnswer(res);
    }

    public static class RFAnswer implements Answer {

        private boolean value;

        @Override
        public EvaluationResult evaluateUserResponse(Answer userResponse) {
            if (((RFAnswer) userResponse).isValue() == isValue())
                return EvaluationResult.CORRECT;

            return EvaluationResult.INCORRECT;
        }

        @Override
        public String toSaveString() {
            return Boolean.toString(isValue());
        }

        public boolean isValue() {
            return value;
        }

        public void setValue(boolean value) {
            this.value = value;
        }
    }
}
