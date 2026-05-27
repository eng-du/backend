package com.gyu.engdu.domain.gamification.exception;

import com.gyu.engdu.global.exception.ErrorCode;
import com.gyu.engdu.global.exception.ValidationException;

public class RunAndLearnSessionAlreadyStartedException extends ValidationException {

    public RunAndLearnSessionAlreadyStartedException(Long sessionId) {
        super(
                ErrorCode.RUN_AND_LEARN_ALREADY_STARTED,
                String.format("이미 시작된 스피드 퀴즈 세션입니다. [sessionId=%d]", sessionId)
        );
    }
}