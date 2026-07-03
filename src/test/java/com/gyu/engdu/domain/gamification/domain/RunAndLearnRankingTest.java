package com.gyu.engdu.domain.gamification.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.gyu.engdu.domain.gamification.domain.enums.RankingType;
import com.gyu.engdu.domain.user.domain.Role;
import com.gyu.engdu.domain.user.domain.User;
import com.gyu.engdu.domain.auth.domain.OAuthProvider;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RunAndLearnRankingTest {

    @Test
    @DisplayName("주간 랭킹 엔티티를 생성하면 랭킹 타입이 WEEKLY이고 전달한 시즌이 설정된다.")
    void createWeeklyRanking() {
        // given
        User user = createUser();
        int season = 1;
        int bestScore = 150;
        LocalDateTime achievedAt = LocalDateTime.of(2026, 6, 1, 10, 0);

        // when
        RunAndLearnRanking weeklyRanking = RunAndLearnRanking.createWeeklyRanking(user, season, bestScore, achievedAt);

        // then
        assertThat(weeklyRanking.getUser()).isEqualTo(user);
        assertThat(weeklyRanking.getRankingType()).isEqualTo(RankingType.WEEKLY);
        assertThat(weeklyRanking.getSeason()).isEqualTo(season);
        assertThat(weeklyRanking.getBestScore()).isEqualTo(bestScore);
        assertThat(weeklyRanking.getAchievedAt()).isEqualTo(achievedAt);
    }

    @Test
    @DisplayName("역대 랭킹 엔티티를 생성하면 랭킹 타입이 ALL_TIME이고 시즌이 0으로 고정된다.")
    void createAllTimeRanking() {
        // given
        User user = createUser();
        int bestScore = 200;
        LocalDateTime achievedAt = LocalDateTime.of(2026, 6, 1, 10, 0);

        // when
        RunAndLearnRanking allTimeRanking = RunAndLearnRanking.createAllTimeRanking(user, bestScore, achievedAt);

        // then
        assertThat(allTimeRanking.getUser()).isEqualTo(user);
        assertThat(allTimeRanking.getRankingType()).isEqualTo(RankingType.ALL_TIME);
        assertThat(allTimeRanking.getSeason()).isZero();
        assertThat(allTimeRanking.getBestScore()).isEqualTo(bestScore);
        assertThat(allTimeRanking.getAchievedAt()).isEqualTo(achievedAt);
    }

    @Test
    @DisplayName("기존보다 더 높은 점수를 업데이트하면 최고 점수와 달성 시간이 갱신된다.")
    void updateScoreIfHigher_higherScore() {
        // given
        User user = createUser();
        LocalDateTime originalTime = LocalDateTime.of(2026, 6, 1, 10, 0);
        int currentScore = 100;
        RunAndLearnRanking ranking = RunAndLearnRanking.createWeeklyRanking(user, 1, currentScore, originalTime);

        // when
        int higherScore = 150;
        LocalDateTime newTime = originalTime.plusDays(1);
        ranking.updateScoreIfHigher(higherScore, newTime);

        // then
        assertThat(ranking.getBestScore()).isEqualTo(higherScore);
        assertThat(ranking.getAchievedAt()).isEqualTo(newTime);
    }

    @Test
    @DisplayName("기존보다 낮은 점수를 업데이트하면 최고 점수와 달성 시간이 갱신되지 않는다.")
    void updateScoreIfHigher_lowerScore() {
        // given
        User user = createUser();
        LocalDateTime originalTime = LocalDateTime.of(2026, 6, 1, 10, 0);
        int currentScore = 100;
        int lowerScore = 50;
        RunAndLearnRanking ranking = RunAndLearnRanking.createWeeklyRanking(user, 1, currentScore, originalTime);

        // when
        ranking.updateScoreIfHigher(lowerScore, originalTime.plusDays(1));

        // then
        assertThat(ranking.getBestScore()).isEqualTo(currentScore);
        assertThat(ranking.getAchievedAt()).isEqualTo(originalTime);
    }

    @Test
    @DisplayName("기존과 같은 점수를 업데이트하면 최고 점수와 달성 시간이 갱신되지 않는다.")
    void updateScoreIfHigher_equalScore() {
        // given
        User user = createUser();
        LocalDateTime originalTime = LocalDateTime.of(2026, 6, 1, 10, 0);
        int currentScore = 100;
        int equalScore = 100;
        RunAndLearnRanking ranking = RunAndLearnRanking.createWeeklyRanking(user, 1, currentScore, originalTime);

        // when
        ranking.updateScoreIfHigher(equalScore, originalTime.plusDays(2));

        // then
        assertThat(ranking.getBestScore()).isEqualTo(currentScore);
        assertThat(ranking.getAchievedAt()).isEqualTo(originalTime);
    }

    private User createUser() {
        return User.builder()
                .email("test@test.com")
                .role(Role.ROLE_USER)
                .sub("sub123")
                .name("testUser")
                .provider(OAuthProvider.GOOGLE)
                .build();
    }
}
