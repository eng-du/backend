package com.gyu.engdu.domain.user.exception;

import com.gyu.engdu.exception.ErrorCode;
import com.gyu.engdu.exception.ValidationException;

public class UserNameTooLongException extends ValidationException {
    public UserNameTooLongException(Long userId, String inputName) {
        super(ErrorCode.USER_NAME_TOO_LONG,
                String.format("사용자 이름이 너무 깁니다. [userId=%d, inputName=%s, length=%d]",
                        userId, inputName, inputName.length()));
    }
}
