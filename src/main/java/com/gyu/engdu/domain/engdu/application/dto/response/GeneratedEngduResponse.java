package com.gyu.engdu.domain.engdu.application.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.gyu.engdu.domain.engdu.domain.Article;
import com.gyu.engdu.domain.engdu.domain.ArticleChunk;
import com.gyu.engdu.domain.engdu.domain.Choice;
import com.gyu.engdu.domain.engdu.domain.Part;
import com.gyu.engdu.domain.engdu.domain.Question;

import com.gyu.engdu.domain.engdu.domain.enums.Category;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GeneratedEngduResponse(
    String title,
    ArticleDto article,
    List<QuestionDto> questions) {

  public record ArticleDto(
      List<List<String>> chunks) {

    public Article toEntity(Part part) {
      Article article = Article.of(part);
      chunks.forEach(chunk -> ArticleChunk.of(chunk.get(0), chunk.get(1), article));
      return article;
    }

  }

  public record QuestionDto(
      String content,
      String type,
      List<ChoiceDto> choices,
      Byte answer) {

    public Question toEntity(Part part) {
      Category category = Category.valueOf(type);
      return Question.of(this.answer, this.content, category, part);
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