package com.gyu.engdu.domain.gamification.application;

import static org.assertj.core.api.Assertions.assertThat;

import com.gyu.engdu.IntegrationTestSupport;
import com.gyu.engdu.domain.gamification.domain.RunAndLearnRanking;
import com.gyu.engdu.domain.gamification.domain.RunAndLearnRankingRepository;
import com.gyu.engdu.domain.gamification.domain.enums.RankingType;
import com.gyu.engdu.domain.user.domain.Role;
import com.gyu.engdu.domain.user.domain.User;
import com.gyu.engdu.domain.user.domain.UserRepository;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class UpdateRunAndLearnRankingServiceTest extends IntegrationTestSupport {

    @Autowired
    private UpdateRunAndLearnRankingService updateRunAndLearnRankingService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RunAndLearnRankingRepository runAndLearnRankingRepository;

    @Test
    @DisplayName("처음으로 런앤런 세션을 종료하여 점수를 등록하면 주간, 역대 모두 1등이다.")
    void updateAndGetRanks_firstScore() {
        // given
        User user = userRepository.save(createUser("user1@test.com", "sub1", "user1"));
        LocalDateTime endedAt = LocalDateTime.of(2026, 6, 1, 10, 0);
        int season1 = 1;
        int score = 100;
        int expectedRank = 1;

        // when
        updateRunAndLearnRankingService.updateRanking(user, score, endedAt);

        // then
        RunAndLearnRanking weeklyRanking = runAndLearnRankingRepository.findByUserIdAndRankingTypeAndSeason(
                user.getId(), RankingType.WEEKLY, season1).orElseThrow();
        RunAndLearnRanking allTimeRanking = runAndLearnRankingRepository.findByUserIdAndRankingTypeAndSeason(
                user.getId(), RankingType.ALL_TIME, 0).orElseThrow();

        int weeklyRank = runAndLearnRankingRepository.countByRankingTypeAndSeasonAndBestScoreGreaterThan(
                RankingType.WEEKLY, season1, weeklyRanking.getBestScore()) + 1;
        int allTimeRank = runAndLearnRankingRepository.countByRankingTypeAndSeasonAndBestScoreGreaterThan(
                RankingType.ALL_TIME, 0, allTimeRanking.getBestScore()) + 1;

        assertThat(weeklyRank).isEqualTo(expectedRank);
        assertThat(allTimeRank).isEqualTo(expectedRank);
        assertThat(weeklyRanking.getBestScore()).isEqualTo(score);
        assertThat(allTimeRanking.getBestScore()).isEqualTo(score);
    }

    @Test
    @DisplayName("유저의 등수는 자신보다 높은 점수를 가진 유저의 수 + 1이다.")
    void updateAndGetRanks_rankIsHigherScoreUserCountPlusOne() {
        // given
        User user1 = userRepository.save(createUser("user1@test.com", "sub1", "user1"));
        User user2 = userRepository.save(createUser("user2@test.com", "sub2", "user2"));
        User user3 = userRepository.save(createUser("user3@test.com", "sub3", "user3"));
        User targetUser = userRepository.save(createUser("target@test.com", "target", "target"));

        LocalDateTime endedAt = LocalDateTime.of(2026, 6, 1, 10, 0);
        int season1 = 1;

        int user1Score = 200;
        int user2Score = 200;
        int user3Score = 150;
        int targetScore = 100;
        int expectedRank = 4;

        updateRunAndLearnRankingService.updateRanking(user1, user1Score, endedAt);
        updateRunAndLearnRankingService.updateRanking(user2, user2Score, endedAt);
        updateRunAndLearnRankingService.updateRanking(user3, user3Score, endedAt);

        // when
        updateRunAndLearnRankingService.updateRanking(targetUser, targetScore, endedAt);

        // then
        RunAndLearnRanking weeklyRanking = runAndLearnRankingRepository.findByUserIdAndRankingTypeAndSeason(
                targetUser.getId(), RankingType.WEEKLY, season1).orElseThrow();
        RunAndLearnRanking allTimeRanking = runAndLearnRankingRepository.findByUserIdAndRankingTypeAndSeason(
                targetUser.getId(), RankingType.ALL_TIME, 0).orElseThrow();

        int weeklyRank = runAndLearnRankingRepository.countByRankingTypeAndSeasonAndBestScoreGreaterThan(
                RankingType.WEEKLY, season1, weeklyRanking.getBestScore()) + 1;
        int allTimeRank = runAndLearnRankingRepository.countByRankingTypeAndSeasonAndBestScoreGreaterThan(
                RankingType.ALL_TIME, 0, allTimeRanking.getBestScore()) + 1;

        assertThat(weeklyRank).isEqualTo(expectedRank);
        assertThat(allTimeRank).isEqualTo(expectedRank);
    }

    @Test
    @DisplayName("기존 최고 점수보다 더 낮은 점수를 등록하면 랭킹 점수가 갱신되지 않는다.")
    void updateAndGetRanks_lowerScore_notUpdated() {
        // given
        User user = userRepository.save(createUser("user1@test.com", "sub1", "user1"));
        LocalDateTime endedAt = LocalDateTime.of(2026, 6, 1, 10, 0);
        int season1 = 1;
        int initialScore = 100;
        int lowerScore = 50;

        updateRunAndLearnRankingService.updateRanking(user, initialScore, endedAt);

        // when
        LocalDateTime later = endedAt.plusMinutes(10);
        updateRunAndLearnRankingService.updateRanking(user, lowerScore, later);

        // then
        RunAndLearnRanking weeklyRanking = runAndLearnRankingRepository.findByUserIdAndRankingTypeAndSeason(
                user.getId(), RankingType.WEEKLY, season1).orElseThrow();

        assertThat(weeklyRanking.getBestScore()).isEqualTo(initialScore); // 50점으로 갱신되지 않음
        assertThat(weeklyRanking.getAchievedAt()).isEqualTo(endedAt); // 처음 달성한 시간 유지
    }

    @Test
    @DisplayName("기존 최고 점수보다 더 높은 점수를 등록하면 랭킹 점수가 갱신된다.")
    void updateAndGetRanks_higherScore_updated() {
        // given
        User user1 = userRepository.save(createUser("user1@test.com", "sub1", "user1"));
        User user2 = userRepository.save(createUser("user2@test.com", "sub2", "user2"));
        LocalDateTime endedAt = LocalDateTime.of(2026, 6, 1, 10, 0);

        int season1 = 1;
        int user1InitialScore = 100;
        int user2Score = 150;
        int user1HigherScore = 200;
        int expectedRank = 1;

        updateRunAndLearnRankingService.updateRanking(user1, user1InitialScore, endedAt);
        updateRunAndLearnRankingService.updateRanking(user2, user2Score, endedAt); // user2가 1등, user1이 2등

        // when
        LocalDateTime later = endedAt.plusMinutes(10);
        updateRunAndLearnRankingService.updateRanking(user1, user1HigherScore, later);

        // then
        RunAndLearnRanking weeklyRanking = runAndLearnRankingRepository.findByUserIdAndRankingTypeAndSeason(
                user1.getId(), RankingType.WEEKLY, season1).orElseThrow();
        RunAndLearnRanking allTimeRanking = runAndLearnRankingRepository.findByUserIdAndRankingTypeAndSeason(
                user1.getId(), RankingType.ALL_TIME, 0).orElseThrow();

        int weeklyRank = runAndLearnRankingRepository.countByRankingTypeAndSeasonAndBestScoreGreaterThan(
                RankingType.WEEKLY, season1, weeklyRanking.getBestScore()) + 1;
        int allTimeRank = runAndLearnRankingRepository.countByRankingTypeAndSeasonAndBestScoreGreaterThan(
                RankingType.ALL_TIME, 0, allTimeRanking.getBestScore()) + 1;

        assertThat(weeklyRank).isEqualTo(expectedRank);
        assertThat(allTimeRank).isEqualTo(expectedRank);
        assertThat(weeklyRanking.getBestScore()).isEqualTo(user1HigherScore);
        assertThat(weeklyRanking.getAchievedAt()).isEqualTo(later);
    }

    @Test
    @DisplayName("새로운 시즌에 이전 역대 최고 점수보다 높은 점수를 등록하면, 주간/역대 점수가 모두 갱신된다.")
    void updateAndGetRanks_differentSeason_higherScore_allTimeUpdated() {
        // given
        int season2 = 2;
        int allTimeSeason = 0;
        User user = userRepository.save(createUser("user1@test.com", "sub1", "user1"));
        LocalDateTime season1Date = LocalDateTime.of(2026, 6, 1, 10, 0); // 시즌 1
        int season1Score = 100;
        int expectedRank = 1;

        updateRunAndLearnRankingService.updateRanking(user, season1Score, season1Date);

        // when
        LocalDateTime season2Date = LocalDateTime.of(2026, 6, 8, 10, 0); // 시즌 2
        int season2HigherScore = 150;
        updateRunAndLearnRankingService.updateRanking(user, season2HigherScore, season2Date);

        // then
        RunAndLearnRanking weeklyRankingSeason2 = runAndLearnRankingRepository.findByUserIdAndRankingTypeAndSeason(
                user.getId(), RankingType.WEEKLY, season2).orElseThrow();

        RunAndLearnRanking allTimeRanking = runAndLearnRankingRepository.findByUserIdAndRankingTypeAndSeason(
                user.getId(), RankingType.ALL_TIME, allTimeSeason).orElseThrow();

        int weeklyRank = runAndLearnRankingRepository.countByRankingTypeAndSeasonAndBestScoreGreaterThan(
                RankingType.WEEKLY, season2, weeklyRankingSeason2.getBestScore()) + 1;
        int allTimeRank = runAndLearnRankingRepository.countByRankingTypeAndSeasonAndBestScoreGreaterThan(
                RankingType.ALL_TIME, allTimeSeason, allTimeRanking.getBestScore()) + 1;

        assertThat(weeklyRank).isEqualTo(expectedRank);
        assertThat(allTimeRank).isEqualTo(expectedRank);
        assertThat(weeklyRankingSeason2.getBestScore()).isEqualTo(season2HigherScore);
        assertThat(allTimeRanking.getBestScore()).isEqualTo(season2HigherScore);
    }

    @Test
    @DisplayName("새로운 시즌에 이전 역대 최고 점수보다 낮은 점수를 등록하면, 주간 점수만 갱신되고 역대 최고 점수는 유지된다.")
    void updateAndGetRanks_differentSeason_lowerScore_allTimeNotUpdated() {
        // given
        User user = userRepository.save(createUser("user1@test.com", "sub1", "user1"));
        LocalDateTime season1Date = LocalDateTime.of(2026, 6, 1, 10, 0); // 시즌 1
        int season1Score = 100;
        int season2 = 2;
        int expectedRank = 1;

        updateRunAndLearnRankingService.updateRanking(user, season1Score, season1Date);

        // when
        LocalDateTime season2Date = LocalDateTime.of(2026, 6, 8, 10, 0); // 시즌 2 (1주일 뒤)
        int season2LowerScore = 50;
        updateRunAndLearnRankingService.updateRanking(user, season2LowerScore, season2Date);

        // then
        RunAndLearnRanking weeklyRankingSeason2 = runAndLearnRankingRepository.findByUserIdAndRankingTypeAndSeason(
                user.getId(), RankingType.WEEKLY, season2).orElseThrow();

        RunAndLearnRanking allTimeRanking = runAndLearnRankingRepository.findByUserIdAndRankingTypeAndSeason(
                user.getId(), RankingType.ALL_TIME, 0).orElseThrow();

        int weeklyRank = runAndLearnRankingRepository.countByRankingTypeAndSeasonAndBestScoreGreaterThan(
                RankingType.WEEKLY, season2, weeklyRankingSeason2.getBestScore()) + 1;
        int allTimeRank = runAndLearnRankingRepository.countByRankingTypeAndSeasonAndBestScoreGreaterThan(
                RankingType.ALL_TIME, 0, allTimeRanking.getBestScore()) + 1;

        assertThat(weeklyRank).isEqualTo(expectedRank);
        assertThat(allTimeRank).isEqualTo(expectedRank);
        assertThat(weeklyRankingSeason2.getBestScore()).isEqualTo(season2LowerScore);
        assertThat(allTimeRanking.getBestScore()).isEqualTo(season1Score); // 역대는 100점 유지
    }

    private User createUser(String email, String sub, String name) {
        return User.builder()
                .email(email)
                .role(Role.ROLE_USER)
                .sub(sub)
                .name(name)
                .build();
    }
}
