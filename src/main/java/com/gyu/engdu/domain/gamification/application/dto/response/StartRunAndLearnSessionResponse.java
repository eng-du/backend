package com.gyu.engdu.domain.gamification.application.dto.response;

import java.time.LocalDateTime;

public record StartRunAndLearnSessionResponse(
        LocalDateTime startTime
) {

    public static StartRunAndLearnSessionResponse of(LocalDateTime startTime) {
        return new StartRunAndLearnSessionResponse(startTime);
    }
}
