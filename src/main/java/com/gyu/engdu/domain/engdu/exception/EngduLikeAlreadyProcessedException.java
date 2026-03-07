package com.gyu.engdu.domain.engdu.exception;

import com.gyu.engdu.domain.engdu.domain.enums.LikeStatus;
import com.gyu.engdu.exception.ErrorCode;
import com.gyu.engdu.exception.ValidationException;

public class EngduLikeAlreadyProcessedException extends ValidationException {
    public EngduLikeAlreadyProcessedException(Long userId, Long engduId, LikeStatus currentLikeStatus) {
        super(ErrorCode.ENGDU_LIKE_ALREADY_PROCESSED,
                String.format("이미 좋아요 상태가 결정되었습니다. [userId=%d, engduId=%d, currentLikeStatus=%s]",
                        userId, engduId, currentLikeStatus));
    }
}
