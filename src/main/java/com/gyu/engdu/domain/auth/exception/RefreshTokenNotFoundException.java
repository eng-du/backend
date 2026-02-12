package com.gyu.engdu.domain.auth.exception;

import com.gyu.engdu.exception.ErrorCode;
import com.gyu.engdu.exception.NotFoundException;

public class RefreshTokenNotFoundException extends NotFoundException {
    public RefreshTokenNotFoundException(String rawToken) {
        super(ErrorCode.REFRESH_TOKEN_NOT_FOUND,
                String.format("리프레시 토큰을 찾을 수 없습니다. [rawToken=%s]", rawToken));
    }
}
