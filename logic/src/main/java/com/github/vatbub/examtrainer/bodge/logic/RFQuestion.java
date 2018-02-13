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
