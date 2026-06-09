package com.gyu.engdu.domain.gamification.application;

import com.gyu.engdu.domain.gamification.domain.RunAndLearnRanking;
import com.gyu.engdu.domain.gamification.domain.RunAndLearnRankingRepository;
import com.gyu.engdu.domain.gamification.domain.RunAndLearnSeasonCalculator;
import com.gyu.engdu.domain.user.domain.User;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateRunAndLearnRankingService {

    private final RunAndLearnRankingRepository rankingRepository;

    @Transactional
    public void updateRanking(User user, int score, LocalDateTime endedAt) {
        int currentSeason = RunAndLearnSeasonCalculator.calculateSeason(endedAt);

        // 주간 랭킹 업데이트
        RunAndLearnRanking weeklyNewRanking = RunAndLearnRanking.createWeeklyRanking(user,
                currentSeason,
                score,
                endedAt
        );
        upsertRanking(weeklyNewRanking, score, endedAt);

        // 역대 랭킹 업데이트
        RunAndLearnRanking allTimeNewRanking = RunAndLearnRanking.createAllTimeRanking(
                user,
                score,
                endedAt
        );
        upsertRanking(allTimeNewRanking, score, endedAt);
    }

    private RunAndLearnRanking upsertRanking(
            RunAndLearnRanking newRanking, int score,
            LocalDateTime achievedAt
    ) {
        RunAndLearnRanking ranking = rankingRepository.findByUserIdAndRankingTypeAndSeason(
                        newRanking.getUser().getId(), newRanking.getRankingType(), newRanking.getSeason())
                .orElseGet(() -> rankingRepository.save(newRanking));

        ranking.updateScoreIfHigher(score, achievedAt);
        return ranking;
    }
}
