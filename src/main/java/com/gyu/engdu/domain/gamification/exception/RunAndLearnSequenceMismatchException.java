package com.gyu.engdu.domain.gamification.exception;

import com.gyu.engdu.global.exception.CustomException;
import com.gyu.engdu.global.exception.ErrorCode;

public class RunAndLearnSequenceMismatchException extends CustomException {

    public RunAndLearnSequenceMismatchException() {
        super(ErrorCode.RUN_AND_LEARN_SEQUENCE_MISMATCH);
    }
}
