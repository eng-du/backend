package com.gyu.engdu.domain.learning.exception;

import com.gyu.engdu.exception.ErrorCode;
import com.gyu.engdu.exception.NotFoundException;

public class PhrasalVerbNotFoundException extends NotFoundException {
    public PhrasalVerbNotFoundException() {
        super(ErrorCode.PHRASAL_NOT_FOUND,
                "구동사를 찾을 수 없습니다. [데이터베이스가 비어있음]");
    }
}
