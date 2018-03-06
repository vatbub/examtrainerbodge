package com.github.vatbub.examtrainer.bodge.javafx.exam;

/*-
 * #%L
 * examtrainer.bodge.javafx
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


import com.github.vatbub.common.core.Prefs;
import com.github.vatbub.examtrainer.bodge.logic.Answer;
import com.github.vatbub.examtrainer.bodge.logic.Question;
import com.github.vatbub.examtrainer.bodge.logic.calculations.StatsCalculator;
import com.github.vatbub.examtrainer.bodge.logic.results.ResultFile;
import com.github.vatbub.examtrainer.bodge.logic.results.ResultListFile;
import net.lingala.zip4j.exception.ZipException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ExamManager {
    private static final String LAST_TURBO_MODE_SETTING_PREF_KEY = "lastTurboModeSetting";
    private ResultListFile resultListFile;
    private ResultFile resultFileToSaveNewExamIn;
    private final AfterQuestionAskedRunnable onOkClicked = (question, givenAnswer) -> {
        saveAnswer(question, givenAnswer);
        saveAll();
    };
    private final AfterQuestionAskedRunnable onCancelClicked = (question, givenAnswer) -> saveAll();
    private StatsCalculator statsCalculator;


    public ExamManager(ResultListFile resultListFile, ResultFile resultFileToSaveNewExamIn) {
        setResultListFile(resultListFile);
        setResultFileToSaveNewExamIn(resultFileToSaveNewExamIn);
    }

    public void startExam() throws IOException, ZipException {
        startExam(getLastTurboModeSetting());
    }

    public void startExam(boolean turboModeEnabled) throws IOException, ZipException {
        List<ResultFile> resultFiles = getResultListFile().getResultFiles();
        resultFiles.add(getResultFileToSaveNewExamIn());
        getResultListFile().setResultFiles(resultFiles);

        saveAll();

        showNextQuestion(turboModeEnabled);
    }

    public void showNextQuestion(boolean turboModeEnabled) throws IOException {
        setLastTurboModeSetting(turboModeEnabled);

        List<Question> questionsThatCanBeAsked = getQuestionsThatCanBeAsked(turboModeEnabled);
        Question questionToAsk = questionsThatCanBeAsked.get(randomIntFromRange(0, questionsThatCanBeAsked.size() - 1));

        switch (questionToAsk.getType()) {
            case RF:
                RFExamDialog.show(questionToAsk, this, onOkClicked, onCancelClicked);
                break;
            case TEXT:
                TextExamDialog.show(questionToAsk, this, onOkClicked, onCancelClicked);
                break;
        }
    }

    public List<Question> getQuestionsThatCanBeAsked(boolean turboModeEnabled) {
        List<Question> questions = getResultListFile().getMasterQuestionFile().getQuestions();
        List<Question> questionsThatCanBeAsked = new ArrayList<>();

        for (Question question : questions) {
            if (getResultFileToSaveNewExamIn().getResults().getOrDefault(question.getId(), Answer.EvaluationResult.INCONCLUSIVE) == Answer.EvaluationResult.INCONCLUSIVE) {
                // question was not answered in this session
                if (turboModeEnabled ^ getStatsCalculator().isGivenQuestionAtExamLevel(question))
                    questionsThatCanBeAsked.add(question);
            }
        }

        return questionsThatCanBeAsked;
    }

    public void saveAnswer(Question question, Answer userResponse) {
        Map<Integer, Answer.EvaluationResult> results = getResultFileToSaveNewExamIn().getResults();
        results.put(question.getId(), question.getCorrectAnswer().evaluateUserResponse(userResponse));
    }

    public void saveAll() throws IOException, ZipException {
        getResultFileToSaveNewExamIn().save();
        getResultListFile().save();
    }

    private int randomIntFromRange(int min, int max) {
        return (int) (Math.round(Math.random() * (max - min)) + min);
    }

    public boolean getLastTurboModeSetting() {
        Prefs pref = new Prefs(getClass().getName());
        return Boolean.parseBoolean(pref.getPreference(LAST_TURBO_MODE_SETTING_PREF_KEY, Boolean.toString(true)));
    }

    public void setLastTurboModeSetting(boolean lastTurboModeSetting) {
        Prefs pref = new Prefs(getClass().getName());
        pref.setPreference(LAST_TURBO_MODE_SETTING_PREF_KEY, Boolean.toString(lastTurboModeSetting));
    }

    public ResultListFile getResultListFile() {
        return resultListFile;
    }

    private void setResultListFile(ResultListFile resultListFile) {
        this.resultListFile = resultListFile;
        setStatsCalculator(new StatsCalculator(getResultListFile()));
    }

    public ResultFile getResultFileToSaveNewExamIn() {
        return resultFileToSaveNewExamIn;
    }

    private void setResultFileToSaveNewExamIn(ResultFile resultFileToSaveNewExamIn) {
        this.resultFileToSaveNewExamIn = resultFileToSaveNewExamIn;
    }

    public StatsCalculator getStatsCalculator() {
        return statsCalculator;
    }

    private void setStatsCalculator(StatsCalculator statsCalculator) {
        this.statsCalculator = statsCalculator;
    }

    public interface AfterQuestionAskedRunnable {
        void run(Question question, Answer givenAnswer) throws IOException, ZipException;
    }
}
