package com.gyu.engdu.global.exception;

public abstract class AuthException extends CustomException {

    protected AuthException(ErrorCode errorCode, String detailMessage) {
        super(errorCode, detailMessage);
    }
}
