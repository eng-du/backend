package com.gyu.engdu.domain.gamification.domain;

import com.gyu.engdu.domain.BaseEntity;
import com.gyu.engdu.domain.gamification.domain.enums.RankingType;
import com.gyu.engdu.domain.user.domain.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "run_and_learn_ranking", uniqueConstraints = {
        @UniqueConstraint(name = "unique_user_ranking_season", columnNames = {"user_id", "ranking_type", "season"})
})
public class RunAndLearnRanking extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "run_and_learn_ranking_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "ranking_type", nullable = false)
    private RankingType rankingType;

    @Column(nullable = false)
    private int season;

    @Column(nullable = false)
    private int bestScore;

    @Column(nullable = false)
    private LocalDateTime achievedAt;

    @Builder(access = AccessLevel.PRIVATE)
    private RunAndLearnRanking(User user, RankingType rankingType, int season, int bestScore, LocalDateTime achievedAt) {
        this.user = user;
        this.rankingType = rankingType;
        this.season = season;
        this.bestScore = bestScore;
        this.achievedAt = achievedAt;
    }

    public static RunAndLearnRanking createWeeklyRanking(User user, int season, int bestScore, LocalDateTime achievedAt) {
        return RunAndLearnRanking.builder()
                .user(user)
                .rankingType(RankingType.WEEKLY)
                .season(season)
                .bestScore(bestScore)
                .achievedAt(achievedAt)
                .build();
    }

    public static RunAndLearnRanking createAllTimeRanking(User user, int bestScore, LocalDateTime achievedAt) {
        return RunAndLearnRanking.builder()
                .user(user)
                .rankingType(RankingType.ALL_TIME)
                .season(0) // 역대 랭킹은 시즌을 0으로 고정
                .bestScore(bestScore)
                .achievedAt(achievedAt)
                .build();
    }

    public void updateScoreIfHigher(int newScore, LocalDateTime achievedAt) {
        if (newScore > this.bestScore) {
            this.bestScore = newScore;
            this.achievedAt = achievedAt;
        }
    }
}
