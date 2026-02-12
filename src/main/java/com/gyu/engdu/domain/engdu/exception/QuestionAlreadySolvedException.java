package com.gyu.engdu.domain.engdu.exception;

import com.gyu.engdu.exception.ErrorCode;
import com.gyu.engdu.exception.ValidationException;

public class QuestionAlreadySolvedException extends ValidationException {
    public QuestionAlreadySolvedException(Long questionId) {
        super(ErrorCode.QUESTION_ALREADY_SOLVED,
                String.format("이미 해결한 문제입니다. [questionId=%d]", questionId));
    }
}
