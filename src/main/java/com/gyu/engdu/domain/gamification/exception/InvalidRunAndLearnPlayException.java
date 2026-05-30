package com.gyu.engdu.domain.gamification.exception;

import com.gyu.engdu.global.exception.ValidationException;
import com.gyu.engdu.global.exception.ErrorCode;

public class InvalidRunAndLearnPlayException extends ValidationException {

    public InvalidRunAndLearnPlayException(String reason) {
        super(ErrorCode.RUN_AND_LEARN_INVALID_PLAY, "유효하지 않은 플레이 감지: " + reason);
    }
}
