package com.github.vatbub.examtrainer.bodge.logic;

public interface Answer {
    EvaluationResult evaluateUserResponse(Answer userResponse);
    String toSaveString();

    enum EvaluationResult{
        CORRECT, INCORRECT, INCONCLUSIVE
    }
}
