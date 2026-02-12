package com.gyu.engdu.domain.engdu.exception;

import com.gyu.engdu.exception.ErrorCode;
import com.gyu.engdu.exception.ValidationException;

public class EngduTitleTooLongException extends ValidationException {
    public EngduTitleTooLongException(Long userId, Long engduId, int titleLength) {
        super(ErrorCode.ENGDU_TITLE_TOO_LONG,
                String.format("제목이 너무 깁니다. [userId=%d, engduId=%d, titleLength=%d]",
                        userId, engduId, titleLength));
    }
}
