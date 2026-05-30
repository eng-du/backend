package com.gyu.engdu.domain.gamification.exception;

import com.gyu.engdu.global.exception.ValidationException;
import com.gyu.engdu.global.exception.ErrorCode;

public class RunAndLearnAllCorrectException extends ValidationException {

    public RunAndLearnAllCorrectException() {
        super(ErrorCode.RUN_AND_LEARN_ALL_CORRECT, "모든 문제를 맞추었습니다. 정상적인 종료가 아닙니다.");
    }
}
