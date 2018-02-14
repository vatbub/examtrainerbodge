package com.github.vatbub.examtrainer.bodge.logic.calculations;

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
