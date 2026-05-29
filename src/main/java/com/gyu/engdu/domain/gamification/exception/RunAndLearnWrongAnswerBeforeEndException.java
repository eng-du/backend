package com.gyu.engdu.domain.gamification.exception;

import com.gyu.engdu.global.exception.CustomException;
import com.gyu.engdu.global.exception.ErrorCode;

public class RunAndLearnWrongAnswerBeforeEndException extends CustomException {

    public RunAndLearnWrongAnswerBeforeEndException() {
        super(ErrorCode.RUN_AND_LEARN_WRONG_ANSWER_BEFORE_END);
    }
}
