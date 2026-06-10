package com.gyu.engdu.domain.gamification.application.dto.response;

public record LeaderboardEntryDto(
        int rank,
        RankingInfoDto rankingInfo
) {
    public static LeaderboardEntryDto of(int rank, RankingInfoDto rankingInfo) {
        return new LeaderboardEntryDto(rank, rankingInfo);
    }
}
