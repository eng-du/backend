package com.gyu.engdu.domain.gamification.exception;

import com.gyu.engdu.global.exception.ErrorCode;
import com.gyu.engdu.global.exception.ForbiddenException;

public class RunAndLearnSessionForbiddenAccessException extends ForbiddenException {

    public RunAndLearnSessionForbiddenAccessException(Long userId, Long sessionId, Long ownerId) {
        super(
                ErrorCode.RUN_AND_LEARN_FORBIDDEN_ACCESS,
                String.format(
                        "사용자의 스피드 퀴즈 세션이 아닙니다. [userId=%d, sessionId=%d, ownerId=%d]",
                        userId,
                        sessionId,
                        ownerId
                )
        );
    }
}
