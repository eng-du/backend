package com.gyu.engdu.domain.gamification.exception;

import com.gyu.engdu.global.exception.ErrorCode;
import com.gyu.engdu.global.exception.NotFoundException;

public class RunAndLearnQuestionNotFoundException extends NotFoundException {

    public RunAndLearnQuestionNotFoundException() {
        super(
                ErrorCode.RUN_AND_LEARN_QUESTION_NOT_FOUND,
                "런앤런 문제를 찾을 수 없습니다."
        );
    }
}
