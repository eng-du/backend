package com.gyu.engdu.domain.gamification.exception;

import com.gyu.engdu.global.exception.ErrorCode;
import com.gyu.engdu.global.exception.NotFoundException;

public class RunAndLearnQuestionExhaustedException extends NotFoundException {

    public RunAndLearnQuestionExhaustedException() {
        super(
                ErrorCode.RUN_AND_LEARN_QUESTION_EXHAUSTED,
                "더 이상 런앤런 문제가 남아있지 않습니다."
        );
    }
}
