package com.gyu.engdu.domain.gamification.domain;

import com.gyu.engdu.domain.gamification.domain.enums.RankingType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RunAndLearnRankingRepository extends JpaRepository<RunAndLearnRanking, Long> {

    Optional<RunAndLearnRanking> findByUserIdAndRankingTypeAndSeason(
            Long userId,
            RankingType rankingType,
            int season
    );

    /**
     * 특정 랭킹 타입과 시즌에서, 입력받은 점수보다 높은 점수를 기록한 유저의 수를 반환합니다. SELECT count(*) FROM run_and_learn_ranking
     * WHERE ranking_type = ? AND season = ? AND best_score > ?
     */
    int countByRankingTypeAndSeasonAndBestScoreGreaterThan(
            RankingType rankingType,
            int season,
            int bestScore
    );

    /**
     * 특정 랭킹 타입과 시즌에서 최고 점수 순, 달성 시간 순으로 랭킹을 조회합니다. (Pageable을 통해 갯수 조절) SELECT * FROM
     * run_and_learn_ranking WHERE ranking_type = ? AND season = ? ORDER BY best_score DESC,
     * achieved_at ASC LIMIT ?
     */
    List<RunAndLearnRanking> findByRankingTypeAndSeasonOrderByBestScoreDescAchievedAtAsc(
            RankingType rankingType,
            int season, Pageable pageable
    );

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("delete from RunAndLearnRanking r where r.user.id = :userId")
    void deleteByUserId(@Param("userId") Long userId);

    List<RunAndLearnRanking> findAllByUserId(Long userId);
}
