package com.github.vatbub.examtrainer.bodge.logic.calculations;

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


import com.github.vatbub.examtrainer.bodge.logic.Answer;
import com.github.vatbub.examtrainer.bodge.logic.Question;
import com.github.vatbub.examtrainer.bodge.logic.results.ResultFile;
import com.github.vatbub.examtrainer.bodge.logic.results.ResultListFile;

import java.util.ArrayList;
import java.util.List;

public class StatsCalculator {
    private ResultListFile resultListFile;

    public StatsCalculator(ResultListFile resultListFile) {
        setResultListFile(resultListFile);
    }

    public ResultListFile getResultListFile() {
        return resultListFile;
    }

    public void setResultListFile(ResultListFile resultListFile) {
        this.resultListFile = resultListFile;
    }

    public boolean wasGivenQuestionEverAnswered(Question question) {
        return wasGivenQuestionEverAnswered(question.getId());
    }

    public boolean wasGivenQuestionEverAnswered(int id) {
        for (ResultFile resultFile : getResultListFile().getResultFiles()) {
            if (resultFile.getResults().getOrDefault(id, Answer.EvaluationResult.INCONCLUSIVE) != Answer.EvaluationResult.INCONCLUSIVE)
                return true;
        }

        return false;
    }

    public boolean isGivenQuestionAtExamLevel(Question question) {
        return isGivenQuestionAtExamLevel(question.getId());
    }

    public boolean isGivenQuestionAtExamLevel(int id) {
        final int numberOfRequiredCorrectAnswers = 2;
        // cannot be at exam level if we have less than two exam results
        if (getResultListFile().getResultFiles().size() < numberOfRequiredCorrectAnswers)
            return false;

        int correctAnswersCount = 0;
        for (ResultFile resultFile : getResultListFile().getResultFiles()) {
            if (resultFile.getResults().getOrDefault(id, Answer.EvaluationResult.INCONCLUSIVE) == Answer.EvaluationResult.CORRECT)
                correctAnswersCount++;
            else if (resultFile.getResults().getOrDefault(id, Answer.EvaluationResult.INCONCLUSIVE) == Answer.EvaluationResult.INCORRECT)
                // if we encounter one incorrect answer before having two correct ones, the question is not at exam level
                return false;

            if (correctAnswersCount >= numberOfRequiredCorrectAnswers)
                return true;
        }

        // we went through all exam results and did not find enough correct answers
        return false;
    }

    public List<Question> getListOfAnsweredQuestions(){
        List<Question> res = new ArrayList<>();

        for(Question question:getResultListFile().getMasterQuestionFile().getQuestions()){
            if (wasGivenQuestionEverAnswered(question))
                res.add(question);
        }

        return res;
    }

    public List<Question> getListOfUnansweredQuestions(){
        List<Question> res = new ArrayList<>();

        for(Question question:getResultListFile().getMasterQuestionFile().getQuestions()){
            if (!wasGivenQuestionEverAnswered(question))
                res.add(question);
        }

        return res;
    }

    public List<Question> getListOfQuestionsAtExamLevel(){
        List<Question> res = new ArrayList<>();

        for(Question question:getResultListFile().getMasterQuestionFile().getQuestions()){
            if (isGivenQuestionAtExamLevel(question))
                res.add(question);
        }

        return res;
    }

    public List<Question> getListOfQuestionsThatAreNotYetAtExamLevel(){
        List<Question> res = new ArrayList<>();

        for(Question question:getResultListFile().getMasterQuestionFile().getQuestions()){
            if (!isGivenQuestionAtExamLevel(question))
                res.add(question);
        }

        return res;
    }
}
