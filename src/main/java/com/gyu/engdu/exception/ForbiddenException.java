package com.gyu.engdu.exception;

public abstract class ForbiddenException extends CustomException {

    protected ForbiddenException(ErrorCode errorCode, String detailMessage) {
        super(errorCode, detailMessage);
    }
}
