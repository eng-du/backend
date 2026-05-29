package com.gyu.engdu.domain.gamification.exception;

import com.gyu.engdu.global.exception.CustomException;
import com.gyu.engdu.global.exception.ErrorCode;

public class RunAndLearnInvalidEndException extends CustomException {

    public RunAndLearnInvalidEndException() {
        super(ErrorCode.RUN_AND_LEARN_INVALID_END);
    }
}
