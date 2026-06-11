package com.gyu.engdu.domain.gamification.exception;

import com.gyu.engdu.global.exception.ValidationException;
import com.gyu.engdu.global.exception.ErrorCode;

public class RunAndLearnInvalidEndException extends ValidationException {

    public RunAndLearnInvalidEndException() {
        super(ErrorCode.RUN_AND_LEARN_INVALID_END);
    }
}
