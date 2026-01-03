package com.gyu.engdu.domain.engdu.application.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.gyu.engdu.domain.engdu.domain.Article;
import com.gyu.engdu.domain.engdu.domain.ArticleChunk;
import com.gyu.engdu.domain.engdu.domain.Choice;
import com.gyu.engdu.domain.engdu.domain.Engdu;
import com.gyu.engdu.domain.engdu.domain.Question;

import com.gyu.engdu.domain.engdu.domain.enums.Category;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GeneratedEngduResponse(
    String title,
    List<ArticleDto> articles,
    List<QuestionDto> questions) {

  public record ArticleDto(
      List<ChunkDto> chunks) {

    public Article toEntity(Engdu engdu) {
      Article article = Article.of(engdu);
      chunks.forEach(chunk -> ArticleChunk.of(chunk.en(), chunk.kor(), article));
      return article;
    }

  }

  public record ChunkDto(
      String en,
      String kor) {
  }

  public record QuestionDto(
      String content,
      String type,
      List<ChoiceDto> choices,
      Byte answer) {

    public Question toEntity(Engdu engdu) {
      Category category = Category.valueOf(type);
      return Question.of(this.answer, this.content, category, engdu);
    }

  }

  public record ChoiceDto(

      Byte seq,
      String content,
      String explanation) {

    public Choice toEntity(Question question) {
      return Choice.of(this.content, this.explanation, this.seq, question);
    }
  }
}