package com.gyu.engdu.domain.gamification.application.dto.response;

public record CreateRunAndLearnSessionResponse(Long sessionId) {

    public static CreateRunAndLearnSessionResponse of(Long sessionId) {
        return new CreateRunAndLearnSessionResponse(sessionId);
    }
}
