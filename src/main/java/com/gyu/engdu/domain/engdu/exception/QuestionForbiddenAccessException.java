package com.gyu.engdu.domain.engdu.exception;

import com.gyu.engdu.exception.ErrorCode;
import com.gyu.engdu.exception.ForbiddenException;

public class QuestionForbiddenAccessException extends ForbiddenException {
    public QuestionForbiddenAccessException(Long engduId, Long questionId) {
        super(ErrorCode.QUESTION_FORBIDDEN_ACCESS,
                String.format("Engdu에 속하지 않은 Question입니다. [engduId=%d, questionId=%d]",
                        engduId, questionId));
    }
}
