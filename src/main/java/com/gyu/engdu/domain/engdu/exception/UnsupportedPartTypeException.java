package com.gyu.engdu.domain.engdu.exception;

import com.gyu.engdu.domain.engdu.domain.enums.PartType;
import com.gyu.engdu.global.exception.ErrorCode;
import com.gyu.engdu.global.exception.InternalServerException;

public class UnsupportedPartTypeException extends InternalServerException {

    public UnsupportedPartTypeException(PartType partType) {
        super(ErrorCode.UNKNOWN_ERROR,
                String.format("지원하지 않는 PartType 입니다: %s", partType));
    }
}
