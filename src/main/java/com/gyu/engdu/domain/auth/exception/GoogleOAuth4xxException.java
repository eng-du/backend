package com.gyu.engdu.domain.auth.exception;

import com.gyu.engdu.exception.ErrorCode;
import com.gyu.engdu.exception.ExternalServiceException;

public class GoogleOAuth4xxException extends ExternalServiceException {
    public GoogleOAuth4xxException(int statusCode) {
        super(ErrorCode.GOOGLE_4XX,
                String.format("Google OAuth 4XX 에러가 발생했습니다. [statusCode=%d]", statusCode));
    }
}
