package com.gyu.engdu.domain.gamification.exception;

import com.gyu.engdu.global.exception.ValidationException;
import com.gyu.engdu.global.exception.ErrorCode;

public class RunAndLearnSequenceMismatchException extends ValidationException {

    public RunAndLearnSequenceMismatchException() {
        super(ErrorCode.RUN_AND_LEARN_SEQUENCE_MISMATCH);
    }
}
