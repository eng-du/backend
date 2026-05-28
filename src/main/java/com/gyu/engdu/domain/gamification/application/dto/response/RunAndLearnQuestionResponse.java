package com.gyu.engdu.domain.gamification.application.dto.response;

import com.gyu.engdu.domain.gamification.domain.RunAndLearnQuestion;

public record RunAndLearnQuestionResponse(
        Long id,
        String question,
        Integer answer,
        String choice1,
        String choice2,
        String choice3,
        String explanation
) {
    public static RunAndLearnQuestionResponse of(RunAndLearnQuestion question) {
        return new RunAndLearnQuestionResponse(
                question.getId(),
                question.getQuestion(),
                question.getAnswer(),
                question.getChoice1(),
                question.getChoice2(),
                question.getChoice3(),
                question.getExplanation()
        );
    }
}
