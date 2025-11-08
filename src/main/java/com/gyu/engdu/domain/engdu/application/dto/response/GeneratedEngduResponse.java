package com.gyu.engdu.domain.engdu.application.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.gyu.engdu.domain.engdu.domain.Article;
import com.gyu.engdu.domain.engdu.domain.Choice;
import com.gyu.engdu.domain.engdu.domain.Engdu;
import com.gyu.engdu.domain.engdu.domain.Question;
import com.gyu.engdu.domain.engdu.domain.enums.Category;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GeneratedEngduResponse(
    String title,
    List<ArticleDto> articles,
    List<QuestionDto> questions
) {

  public record ArticleDto(
      String content,
      String translation
  ) {

    public Article toEntity(Engdu engdu) {
      return Article.of(this.content, this.translation, engdu);
    }

  }

  public record QuestionDto(
      String content,
      String type,
      List<ChoiceDto> choices,
      Byte answer
  ) {

    public Question toEntity(Engdu engdu) {
      Category category = Category.valueOf(type);
      return Question.of(this.answer, this.content, category, engdu);
    }

  }

  public record ChoiceDto(

      Byte seq,
      String content,
      String explanation
  ) {

    public Choice toEntity(Question question) {
      return Choice.of(this.content, this.explanation, this.seq, question);
    }
  }
}