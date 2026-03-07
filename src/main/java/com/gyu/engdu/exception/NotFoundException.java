package com.gyu.engdu.exception;

public abstract class NotFoundException extends CustomException {

    protected NotFoundException(ErrorCode errorCode, String detailMessage) {
        super(errorCode, detailMessage);
    }
}
