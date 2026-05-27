package com.gyu.engdu.domain.gamification.exception;

import com.gyu.engdu.global.exception.ErrorCode;
import com.gyu.engdu.global.exception.NotFoundException;

public class RunAndLearnSessionNotFoundException extends NotFoundException {

    public RunAndLearnSessionNotFoundException(Long sessionId) {
        super(
                ErrorCode.RUN_AND_LEARN_NOT_FOUND,
                String.format("스피드 퀴즈 세션을 찾을 수 없습니다. [sessionId=%d]", sessionId)
        );
    }
}
