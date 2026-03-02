package com.gyu.engdu.domain.engdu.infra.dto;

import com.gyu.engdu.domain.engdu.domain.enums.PartType;

public record EngduSqsMessage(
        Long engduId,
        Long userId,
        PartType step) {

    public static EngduSqsMessage of(Long engduId, Long userId, PartType step) {
        return new EngduSqsMessage(engduId, userId, step);
    }
}
