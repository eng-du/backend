package com.gyu.engdu.domain.engdu.presentation.dto.response;

import com.gyu.engdu.domain.engdu.domain.Engdu;
import java.time.LocalDateTime;

public record EngduSummaryResponse(
    Long engduId,
    String title,
    String topic,
    Integer solvedCount,
    Integer totalCount,
    Boolean isAllSolved,
    LocalDateTime createdAt
) {

  public static EngduSummaryResponse from(Engdu engdu) {
    return new EngduSummaryResponse(
        engdu.getId(),
        engdu.getTitle(),
        engdu.getTopic(),
        engdu.getSolvedCount(),
        engdu.getQuestions().size(),
        engdu.isAllSolved(),
        engdu.getCreatedAt()
    );
  }
}
