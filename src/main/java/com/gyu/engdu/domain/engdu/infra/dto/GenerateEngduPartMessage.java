package com.gyu.engdu.domain.engdu.infra.dto;

import com.gyu.engdu.domain.engdu.domain.enums.PartType;

public record GenerateEngduPartMessage(
        Long engduId,
        Long userId,
        PartType step) {

    public static GenerateEngduPartMessage of(Long engduId, Long userId, PartType step) {
        return new GenerateEngduPartMessage(engduId, userId, step);
    }
}
