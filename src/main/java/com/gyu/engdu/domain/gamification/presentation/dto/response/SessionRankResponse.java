package com.gyu.engdu.domain.gamification.presentation.dto.response;

import com.gyu.engdu.domain.gamification.application.dto.response.SessionRankingDto;

public record SessionRankResponse(
        int score,
        int rank
) {
    public static SessionRankResponse from(SessionRankingDto dto) {
        return new SessionRankResponse(dto.score(), dto.rank());
    }
}
