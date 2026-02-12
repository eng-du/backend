package com.gyu.engdu.domain.engdu.exception;

import com.gyu.engdu.exception.ErrorCode;
import com.gyu.engdu.exception.InternalServerException;

public class EngduGenerate5xxException extends InternalServerException {
    public EngduGenerate5xxException(int statusCode) {
        super(ErrorCode.ENGDU_GENERATE_5XX,
                String.format("LLM API 5XX 에러가 발생했습니다. [statusCode=%d]", statusCode));
    }
}
