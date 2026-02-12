package com.gyu.engdu.exception;

public abstract class ExternalServiceException extends CustomException {

    protected ExternalServiceException(ErrorCode errorCode, String detailMessage) {
        super(errorCode, detailMessage);
    }
}
