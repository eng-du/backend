package com.gyu.engdu.domain.auth.exception;

import com.gyu.engdu.exception.AuthException;
import com.gyu.engdu.exception.ErrorCode;

public class JwtInvalidException extends AuthException {
    public JwtInvalidException() {
        super(ErrorCode.JWT_INVALID, "JWT 토큰 형식이 유효하지 않습니다.");
    }
}
