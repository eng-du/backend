package com.gyu.engdu.domain.engdu.exception;

import com.gyu.engdu.exception.ErrorCode;
import com.gyu.engdu.exception.ExternalServiceException;

public class EngduGenerate4xxException extends ExternalServiceException {
    public EngduGenerate4xxException(int statusCode) {
        super(ErrorCode.ENGDU_GENERATE_4XX,
                String.format("LLM API 4XX 에러가 발생했습니다. [statusCode=%d]", statusCode));
    }
}
