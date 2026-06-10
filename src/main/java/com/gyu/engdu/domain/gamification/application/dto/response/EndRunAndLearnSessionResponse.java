package com.gyu.engdu.domain.gamification.application.dto.response;

public record EndRunAndLearnSessionResponse(
        long weeklyRank,
        long allTimeRank
) {
    public static EndRunAndLearnSessionResponse of(long weeklyRank, long allTimeRank) {
        return new EndRunAndLearnSessionResponse(weeklyRank, allTimeRank);
    }
}
