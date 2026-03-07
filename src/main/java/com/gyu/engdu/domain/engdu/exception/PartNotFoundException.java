package com.gyu.engdu.domain.engdu.exception;

import com.gyu.engdu.domain.engdu.domain.enums.PartType;
import com.gyu.engdu.exception.ErrorCode;
import com.gyu.engdu.exception.NotFoundException;

public class PartNotFoundException extends NotFoundException {

    public PartNotFoundException(Long partId) {
        super(ErrorCode.PART_NOT_FOUND,
                String.format("파트를 찾을 수 없습니다. [partId=%d]", partId));
    }

    public PartNotFoundException(Long engduId, PartType partType) {
        super(ErrorCode.PART_NOT_FOUND,
                String.format("파트를 찾을 수 없습니다. [engduId=%d, partType=%s]", engduId, partType));
    }
}
