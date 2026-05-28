package com.gyu.engdu.domain.gamification.presentation.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record RunAndLearnQuestionRequest(
        @NotNull(message = "startIndex는 필수입니다.")
        @Min(value = 0, message = "startIndex는 0 이상이어야 합니다.")
        Integer startIndex,

        @NotNull(message = "count는 필수입니다.")
        @Min(value = 1, message = "count는 1 이상이어야 합니다.")
        @Max(value = 30, message = "count는 30 이하이어야 합니다.")
        Integer count
) {
}
