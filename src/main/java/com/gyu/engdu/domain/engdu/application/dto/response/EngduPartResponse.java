package com.gyu.engdu.domain.engdu.application.dto.response;

import com.gyu.engdu.domain.engdu.domain.Article;
import com.gyu.engdu.domain.engdu.domain.ArticleChunk;
import com.gyu.engdu.domain.engdu.domain.Choice;
import com.gyu.engdu.domain.engdu.domain.Engdu;
import com.gyu.engdu.domain.engdu.domain.Question;
import java.util.List;

public record EngduPartResponse(
    Long engduId,
    Meta meta,
    Part part) {

  public static EngduPartResponse of(Engdu engdu, Article article,
      List<Question> questions) {
    return new EngduPartResponse(
        engdu.getId(),
        new Meta(engdu.getTitle(), engdu.getTopic()),
        Part.of(article, questions));
  }

  public record Meta(String title, String topic) {

  }

  public record Part(
      ArticleResponse article,
      List<QuestionResponse> questions) {

    public static Part of(com.gyu.engdu.domain.engdu.domain.Article article,
        List<Question> questions) {
      return new Part(
          ArticleResponse.of(article),
          questions.stream()
              .map(QuestionResponse::fromEntity)
              .toList());
    }
  }

  public record ArticleResponse(List<ChunkResponse> chunks) {

    public static ArticleResponse of(com.gyu.engdu.domain.engdu.domain.Article article) {
      return new ArticleResponse(
          article.getChunks().stream()
              .map(ChunkResponse::fromEntity)
              .toList());
    }
  }

  public record ChunkResponse(String en, String kor) {

    public static ChunkResponse fromEntity(ArticleChunk chunk) {
      return new ChunkResponse(chunk.getEn(), chunk.getKor());
    }
  }

  public record QuestionResponse(
      Long questionId,
      String content,
      Byte answer,
      boolean isCorrected,
      List<ChoiceResponse> choices) {

    public static QuestionResponse fromEntity(Question question) {

      Byte answer = question.isCorrected() ? question.getAnswer() : null;
      return new QuestionResponse(
          question.getId(),
          question.getContent(),
          answer,
          question.isCorrected(),
          question.getChoices().stream()
              .map(ChoiceResponse::fromEntity)
              .toList());
    }
  }

  public record ChoiceResponse(
      Byte seq,
      String content,
      String explanation) {

    public static ChoiceResponse fromEntity(Choice choice) {
      return new ChoiceResponse(
          choice.getSeq(),
          choice.getContent(),
          choice.getExplanation());
    }
  }
}
