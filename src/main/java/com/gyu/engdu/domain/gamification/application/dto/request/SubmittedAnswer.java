package com.gyu.engdu.domain.gamification.application.dto.request;

import jakarta.validation.constraints.NotNull;

public record SubmittedAnswer(
        @NotNull(message = "문제 ID는 필수입니다.") Long questionId,
        @NotNull(message = "제출 답안은 필수입니다.") Integer userAnswer
) {

}
