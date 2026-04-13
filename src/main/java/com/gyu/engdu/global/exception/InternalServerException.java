package com.gyu.engdu.global.exception;

public abstract class InternalServerException extends CustomException {

    protected InternalServerException(ErrorCode errorCode, String detailMessage) {
        super(errorCode, detailMessage);
    }
}
