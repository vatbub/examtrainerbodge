package com.github.vatbub.examtrainer.bodge.logic.calculations;

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
