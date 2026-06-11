package com.gyu.engdu.domain.gamification.application.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record EndRunAndLearnSessionRequest(
        @NotNull(message = "총 점수는 필수입니다.") Integer clientTotalScore,
        @NotEmpty(message = "제출된 답변 리스트는 비어있을 수 없습니다.") List<SubmittedAnswer> submittedAnswers
) {

}
