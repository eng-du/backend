package com.gyu.engdu.domain.gamification.presentation.dto.request;

import com.gyu.engdu.domain.gamification.domain.enums.RankingType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record RunAndLearnLeaderboardRequest(
        @NotNull RankingType rankingType,
        @NotNull @Max(10) @Min(1) Integer size) {
}
