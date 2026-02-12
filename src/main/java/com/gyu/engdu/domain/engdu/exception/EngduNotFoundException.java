package com.gyu.engdu.domain.engdu.exception;

import com.gyu.engdu.exception.ErrorCode;
import com.gyu.engdu.exception.NotFoundException;

public class EngduNotFoundException extends NotFoundException {
    public EngduNotFoundException(Long engduId) {
        super(ErrorCode.ENGDU_NOT_FOUND,
                String.format("Engdu를 찾을 수 없습니다. [engduId=%d]", engduId));
    }
}
