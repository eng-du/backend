package com.gyu.engdu.domain.gamification.exception;

import com.gyu.engdu.global.exception.ValidationException;
import com.gyu.engdu.global.exception.ErrorCode;

public class RunAndLearnScoreMismatchException extends ValidationException {

    public RunAndLearnScoreMismatchException() {
        super(ErrorCode.RUN_AND_LEARN_SCORE_MISMATCH);
    }
}
