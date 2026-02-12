package com.gyu.engdu.domain.user.exception;

import com.gyu.engdu.exception.ErrorCode;
import com.gyu.engdu.exception.NotFoundException;

public class UserNotFoundException extends NotFoundException {
    public UserNotFoundException(Long userId) {
        super(ErrorCode.USER_NOT_FOUND,
                String.format("사용자를 찾을 수 없습니다. [userId=%d]", userId));
    }
}
