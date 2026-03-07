package com.gyu.engdu.domain.auth.exception;

import com.gyu.engdu.exception.AuthException;
import com.gyu.engdu.exception.ErrorCode;

public class JwtExpiredException extends AuthException {
    public JwtExpiredException() {
        super(ErrorCode.JWT_EXPIRED, "JWT 토큰이 만료되었습니다.");
    }
}
