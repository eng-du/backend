package com.gyu.engdu.domain.gamification.presentation.dto.response;

import com.gyu.engdu.domain.gamification.application.dto.response.RankingInfoDto;
import java.time.LocalDateTime;

public record LeaderboardEntryResponse(
        int rank,
        String userName,
        int bestScore,
        LocalDateTime achievedAt
) {
    public static LeaderboardEntryResponse of(int rank, RankingInfoDto dto) {
        return new LeaderboardEntryResponse(
                rank,
                dto.userName(),
                dto.bestScore(),
                dto.achievedAt()
        );
    }
}
