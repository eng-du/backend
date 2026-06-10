package com.gyu.engdu.domain.gamification.domain;

import com.gyu.engdu.domain.gamification.exception.InvalidRunAndLearnSeasonDateException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RunAndLearnSeasonCalculator {

    private static final LocalDate BASE_DATE = LocalDate.of(2026, 6, 1);


    /**
     * 2026년 6월 1일(월)은 시즌 1입니다.
     * 매주 월요일마다 시즌이 1씩 증가합니다.
     * 타겟 시간의 시즌을 계산합니다.
     */
    public static int calculateSeason(LocalDateTime targetDateTime) {
        if (targetDateTime == null) {
            throw new InvalidRunAndLearnSeasonDateException("타겟 날짜는 null일 수 없습니다.");
        }

        LocalDate targetDate = targetDateTime.toLocalDate();

        if (targetDate.isBefore(BASE_DATE)) {
            throw new InvalidRunAndLearnSeasonDateException("타겟 날짜는 기준일(2026-06-01) 이전일 수 없습니다.");
        }

        long weeks = ChronoUnit.WEEKS.between(BASE_DATE, targetDate);
        return (int) weeks + 1;
    }
}
