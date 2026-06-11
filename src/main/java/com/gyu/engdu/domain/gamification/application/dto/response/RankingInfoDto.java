package com.gyu.engdu.domain.gamification.application.dto.response;

import com.gyu.engdu.domain.gamification.domain.RunAndLearnRanking;
import java.time.LocalDateTime;

public record RankingInfoDto(
        String userName,
        int bestScore,
        LocalDateTime achievedAt
) {
    public static RankingInfoDto from(RunAndLearnRanking ranking) {
        return new RankingInfoDto(
                ranking.getUser().getName(),
                ranking.getBestScore(),
                ranking.getAchievedAt()
        );
    }
}
