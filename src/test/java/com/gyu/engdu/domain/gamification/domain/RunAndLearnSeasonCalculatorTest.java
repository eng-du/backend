package com.gyu.engdu.domain.gamification.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.gyu.engdu.domain.gamification.exception.InvalidRunAndLearnSeasonDateException;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RunAndLearnSeasonCalculatorTest {

    @Test
    @DisplayName("2026년 6월 1일(월)은 기준일로 시즌 1이다.")
    void calculateSeason_baseDate_isSeason1() {
        // given
        LocalDateTime baseDate = LocalDateTime.of(2026, 6, 1, 0, 0);
        int season1 = 1;

        // when
        int season = RunAndLearnSeasonCalculator.calculateSeason(baseDate);

        // then
        assertThat(season).isEqualTo(season1);
    }

    @Test
    @DisplayName("같은 주차의 월요일, 수요일, 일요일은 모두 동일한 시즌을 갖는다.")
    void calculateSeason_sameWeek_sameSeason() {
        // given
        LocalDateTime monday = LocalDateTime.of(2026, 6, 1, 0, 0);
        LocalDateTime wednesday = LocalDateTime.of(2026, 6, 3, 0, 0);
        LocalDateTime sunday = LocalDateTime.of(2026, 6, 7, 0, 0);
        int expectedSeason = 1;

        // when
        int mondaySeason = RunAndLearnSeasonCalculator.calculateSeason(monday);
        int wednesdaySeason = RunAndLearnSeasonCalculator.calculateSeason(wednesday);
        int sundaySeason = RunAndLearnSeasonCalculator.calculateSeason(sunday);

        // then
        assertThat(mondaySeason).isEqualTo(expectedSeason);
        assertThat(wednesdaySeason).isEqualTo(expectedSeason);
        assertThat(sundaySeason).isEqualTo(expectedSeason);
    }

    @Test
    @DisplayName("기준일로부터 주차가 지나면 시즌이 1씩 커진다.")
    void calculateSeason_nextWeeks_incrementSeason() {
        // given
        LocalDateTime season1Monday = LocalDateTime.of(2026, 6, 1, 0, 0);
        LocalDateTime season2Monday = LocalDateTime.of(2026, 6, 8, 0, 0);
        LocalDateTime season3Monday = LocalDateTime.of(2026, 6, 15, 0, 0);
        LocalDateTime season10Monday = LocalDateTime.of(2026, 8, 3, 0, 0);

        // when
        int season1 = RunAndLearnSeasonCalculator.calculateSeason(season1Monday);
        int season2 = RunAndLearnSeasonCalculator.calculateSeason(season2Monday);
        int season3 = RunAndLearnSeasonCalculator.calculateSeason(season3Monday);
        int season10 = RunAndLearnSeasonCalculator.calculateSeason(season10Monday);

        // then
        assertThat(season1).isEqualTo(1);
        assertThat(season2).isEqualTo(2);
        assertThat(season3).isEqualTo(3);
        assertThat(season10).isEqualTo(10);
    }

    @Test
    @DisplayName("기준일 이전의 날짜는 예외가 발생한다.")
    void calculateSeason_beforeBaseDate_throwsException() {
        // given
        LocalDateTime beforeBaseDate = LocalDateTime.of(2026, 5, 31, 23, 59);

        // when & then
        assertThatThrownBy(
                () -> RunAndLearnSeasonCalculator.calculateSeason(beforeBaseDate)).isInstanceOf(
                InvalidRunAndLearnSeasonDateException.class);
    }
}
