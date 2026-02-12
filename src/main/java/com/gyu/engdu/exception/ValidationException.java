package com.gyu.engdu.exception;

public abstract class ValidationException extends CustomException {

    protected ValidationException(ErrorCode errorCode, String detailMessage) {
        super(errorCode, detailMessage);
    }
}
