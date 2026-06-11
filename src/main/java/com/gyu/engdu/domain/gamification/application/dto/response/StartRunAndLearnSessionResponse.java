package com.gyu.engdu.domain.gamification.application.dto.response;

import java.time.LocalDateTime;

public record StartRunAndLearnSessionResponse(
        Long sessionId,
        LocalDateTime startTime
) {

    public static StartRunAndLearnSessionResponse of(Long sessionId, LocalDateTime startTime) {
        return new StartRunAndLearnSessionResponse(sessionId, startTime);
    }
}
