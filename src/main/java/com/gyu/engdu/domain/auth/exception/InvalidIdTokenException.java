package com.gyu.engdu.domain.auth.exception;

import com.gyu.engdu.exception.ErrorCode;
import com.gyu.engdu.exception.InternalServerException;

public class InvalidIdTokenException extends InternalServerException {
    public InvalidIdTokenException(String reason) {
        super(ErrorCode.INVALID_ID_TOKEN,
                String.format("ID Token 파싱에 실패했습니다. [reason=%s]", reason));
    }
}
