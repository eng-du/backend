package com.gyu.engdu.domain.engdu.application.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GeneratedEngduResponse(
    String title,
    List<ArticleDto> articles
) {

  public record ArticleDto(
      String content,
      String translation,

      List<QuizDto> quizzes
  ) {

  }

  public record QuizDto(
      String question,
      String type,
      List<ChoiceDto> choices,
      Integer answer
  ) {

  }

  public record ChoiceDto(
      String content,
      String explanation
  ) {

  }
}