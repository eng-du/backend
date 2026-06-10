package com.gyu.engdu.domain.gamification.exception;

import com.gyu.engdu.global.exception.ErrorCode;
import com.gyu.engdu.global.exception.ValidationException;

public class InvalidRunAndLearnSeasonDateException extends ValidationException {
    public InvalidRunAndLearnSeasonDateException() {
        super(ErrorCode.RUN_AND_LEARN_INVALID_SEASON_DATE);
    }

    public InvalidRunAndLearnSeasonDateException(String message) {
        super(ErrorCode.RUN_AND_LEARN_INVALID_SEASON_DATE, message);
    }
}
