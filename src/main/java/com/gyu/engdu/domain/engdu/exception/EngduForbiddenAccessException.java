package com.gyu.engdu.domain.engdu.exception;

import com.gyu.engdu.exception.ErrorCode;
import com.gyu.engdu.exception.ForbiddenException;

public class EngduForbiddenAccessException extends ForbiddenException {
    public EngduForbiddenAccessException(Long userId, Long engduId, Long ownerId) {
        super(ErrorCode.ENGDU_FORBIDDEN_ACCESS,
                String.format("사용자의 Engdu가 아닙니다. [userId=%d, engduId=%d, ownerId=%d]",
                        userId, engduId, ownerId));
    }
}
