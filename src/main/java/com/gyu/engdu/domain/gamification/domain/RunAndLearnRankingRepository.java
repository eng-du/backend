package com.gyu.engdu.domain.gamification.domain;

import com.gyu.engdu.domain.gamification.domain.enums.RankingType;
import com.gyu.engdu.domain.user.domain.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RunAndLearnRankingRepository extends JpaRepository<RunAndLearnRanking, Long> {

       Optional<RunAndLearnRanking> findByUserAndRankingTypeAndSeason(User user, RankingType rankingType, int season);

       /**
        * 특정 랭킹 타입과 시즌에서, 입력받은 점수보다 엄격히 높은 점수를 기록한 유저의 수를 반환합니다.
        * SELECT count(*)
        * FROM run_and_learn_ranking
        * WHERE ranking_type = ? AND season = ? AND best_score > ?
        */
       int countByRankingTypeAndSeasonAndBestScoreGreaterThan(RankingType rankingType, int season, int bestScore);
}
