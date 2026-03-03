package com.gyu.engdu.domain.engdu.application.dto.response;

import com.gyu.engdu.domain.engdu.domain.Part;
import com.gyu.engdu.domain.engdu.domain.enums.PartStatus;
import com.gyu.engdu.domain.engdu.domain.enums.PartType;

public record EngduPartStatusResponse(
        PartType partType,
        PartStatus status,
        EngduPartResponse data) {

    // DONE 상태이면 data에 EngduPartResponse를 담고, 그 외(PENDING/CREATING/FAILED)는 null 반환.
    public static EngduPartStatusResponse fromEntity(Part part) {
        if (part.getStatus() != PartStatus.DONE) {
            return new EngduPartStatusResponse(part.getPartType(), part.getStatus(), null);
        }
        return new EngduPartStatusResponse(
                part.getPartType(),
                part.getStatus(),
                EngduPartResponse.of(part.getEngdu(), part.getArticle(), part.getQuestions()));
    }
}
