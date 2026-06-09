package com.gyu.engdu.domain.gamification.application;

import com.gyu.engdu.domain.gamification.application.dto.response.RunAndLearnRankingResult;
import com.gyu.engdu.domain.gamification.domain.RunAndLearnRanking;
import com.gyu.engdu.domain.gamification.domain.RunAndLearnRankingRepository;
import com.gyu.engdu.domain.gamification.domain.RunAndLearnSeasonCalculator;
import com.gyu.engdu.domain.gamification.domain.enums.RankingType;
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
    public RunAndLearnRankingResult updateAndGetRanks(User user, int score, LocalDateTime endedAt) {
        int currentSeason = RunAndLearnSeasonCalculator.calculateSeason(endedAt);

        // 주간 랭킹 업데이트 및 등수 조회
        RunAndLearnRanking weeklyNewRanking = RunAndLearnRanking.createWeeklyRanking(user,
                currentSeason,
                score,
                endedAt
        );
        RunAndLearnRanking weeklyRanking = upsertRanking(weeklyNewRanking, score, endedAt);
        int myWeeklyRank = calculateMyRank(RankingType.WEEKLY, currentSeason, weeklyRanking.getBestScore());


        // 역대 랭킹 업데이트 및 등수 조회
        RunAndLearnRanking allTimeNewRanking = RunAndLearnRanking.createAllTimeRanking(
                user,
                score,
                endedAt
        );
        RunAndLearnRanking allTimeRanking = upsertRanking(allTimeNewRanking, score, endedAt);
        int myAllTimeRank = calculateMyRank(RankingType.ALL_TIME, allTimeRanking.getSeason(), allTimeRanking.getBestScore());

        return RunAndLearnRankingResult.of(myWeeklyRank, myAllTimeRank);
    }

    private int calculateMyRank(RankingType rankingType, int season, int myBestScore) {
        int higherScoreCount = rankingRepository.countByRankingTypeAndSeasonAndBestScoreGreaterThan(
                rankingType,
                season,
                myBestScore
        );
        return higherScoreCount + 1;
    }

    private RunAndLearnRanking upsertRanking(RunAndLearnRanking newRanking, int score,
            LocalDateTime achievedAt) {
        RunAndLearnRanking ranking = rankingRepository.findByUserIdAndRankingTypeAndSeason(
                        newRanking.getUser().getId(), newRanking.getRankingType(), newRanking.getSeason())
                .orElseGet(() -> rankingRepository.save(newRanking));

        ranking.updateScoreIfHigher(score, achievedAt);
        return ranking;
    }
}
