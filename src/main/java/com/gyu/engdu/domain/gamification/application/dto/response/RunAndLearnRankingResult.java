package com.gyu.engdu.domain.gamification.application.dto.response;

public record RunAndLearnRankingResult(
        long weeklyRank,
        long allTimeRank
) {
    public static RunAndLearnRankingResult of(long weeklyRank, long allTimeRank) {
        return new RunAndLearnRankingResult(weeklyRank, allTimeRank);
    }
}
