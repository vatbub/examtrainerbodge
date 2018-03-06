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


import com.github.vatbub.examtrainer.bodge.logic.Question;
import com.github.vatbub.examtrainer.bodge.logic.QuestionFile;
import com.github.vatbub.examtrainer.bodge.logic.results.ResultListFile;

import java.util.ArrayList;
import java.util.List;

public class QuizGenerator {
    private ResultListFile resultListFile;
    private QuestionFile masterQuestionFile;
    private List<Integer> questionsAskedInThisQuiz;
    private StatsCalculator statsCalculator;

    public QuizGenerator() {
        this(null);
    }

    public QuizGenerator(ResultListFile resultListFile) {
        this(resultListFile, resultListFile.getMasterQuestionFile());
    }

    public QuizGenerator(ResultListFile resultListFile, QuestionFile masterQuestionFile) {
        setResultListFile(resultListFile);
        setMasterQuestionFile(masterQuestionFile);
        resetQuiz();
    }

    public void resetQuiz() {
        questionsAskedInThisQuiz = new ArrayList<>();
    }

    public ResultListFile getResultListFile() {
        return resultListFile;
    }

    public void setResultListFile(ResultListFile resultListFile) {
        this.resultListFile = resultListFile;
        setStatsCalculator(new StatsCalculator(resultListFile));
    }

    public StatsCalculator getStatsCalculator() {
        return statsCalculator;
    }

    private void setStatsCalculator(StatsCalculator statsCalculator) {
        this.statsCalculator = statsCalculator;
    }

    public QuestionFile getMasterQuestionFile() {
        return masterQuestionFile;
    }

    public void setMasterQuestionFile(QuestionFile masterQuestionFile) {
        this.masterQuestionFile = masterQuestionFile;
    }

    public Question getNextRandomQuestion(boolean powerMode) {
        // get available ids
        List<Question> availableQuestions = new ArrayList<>();
        for (Question question : getMasterQuestionFile().getQuestions()) {
            if (!questionsAskedInThisQuiz.contains(question.getId()) && (!powerMode || !getStatsCalculator().isGivenQuestionAtExamLevel(question)))
                availableQuestions.add(question);
        }

        int randomIndex = (int) Math.round(Math.random() * (availableQuestions.size() - 1));
        questionsAskedInThisQuiz.add(randomIndex);
        return availableQuestions.get(randomIndex);
    }
}
