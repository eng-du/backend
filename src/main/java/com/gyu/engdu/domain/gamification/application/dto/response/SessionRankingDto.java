package com.gyu.engdu.domain.gamification.application.dto.response;

public record SessionRankingDto(
        int score,
        int rank
) {
    public static SessionRankingDto of(int score, int rank) {
        return new SessionRankingDto(score, rank);
    }
}
