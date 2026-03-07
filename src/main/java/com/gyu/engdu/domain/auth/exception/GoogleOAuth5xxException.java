package com.gyu.engdu.domain.auth.exception;

import com.gyu.engdu.exception.ErrorCode;
import com.gyu.engdu.exception.InternalServerException;

public class GoogleOAuth5xxException extends InternalServerException {
    public GoogleOAuth5xxException(int statusCode) {
        super(ErrorCode.GOOGLE_5XX,
                String.format("Google OAuth 5XX 에러가 발생했습니다. [statusCode=%d]", statusCode));
    }
}
