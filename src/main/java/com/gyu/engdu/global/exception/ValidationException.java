package com.gyu.engdu.global.exception;

public abstract class ValidationException extends CustomException {

    protected ValidationException(ErrorCode errorCode) {
        super(errorCode);
    }

    protected ValidationException(ErrorCode errorCode, String detailMessage) {
        super(errorCode, detailMessage);
    }
}
