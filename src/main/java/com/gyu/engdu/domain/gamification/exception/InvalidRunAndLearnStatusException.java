package com.gyu.engdu.domain.gamification.exception;

import com.gyu.engdu.domain.gamification.domain.enums.RunAndLearnSessionStatus;
import com.gyu.engdu.global.exception.ValidationException;
import com.gyu.engdu.global.exception.ErrorCode;

public class InvalidRunAndLearnStatusException extends ValidationException {

    public InvalidRunAndLearnStatusException(RunAndLearnSessionStatus status) {
        super(ErrorCode.RUN_AND_LEARN_INVALID_PLAY, "세션의 상태가 올바르지 않습니다. 현재 상태: " + status);
    }
}
