package com.gyu.engdu.domain.engdu.presentation.dto.response;

import com.gyu.engdu.domain.engdu.domain.Article;
import com.gyu.engdu.domain.engdu.domain.Engdu;
import com.gyu.engdu.domain.engdu.domain.Question;
import java.util.List;

public record EngduDetailResponse(
    String title,
    String topic,
    List<ArticleDto> articles,
    List<QuestionDto> questions
) {

  public static EngduDetailResponse fromEntity(Engdu engdu) {
    String title = engdu.getTitle();
    String topic = engdu.getTopic();
    List<Article> articles = engdu.getArticles();
    List<Question> questions = engdu.getQuestions();

    List<ArticleDto> articleDtos = articles.stream()
        .map(article -> new ArticleDto(article.getContent(), article.getTranslation()))
        .toList();

    List<QuestionDto> questionDtos = questions.stream().map(question -> {
      List<ChoiceDto> choices = question.getChoices().stream()
          .map(choice -> new ChoiceDto(
              choice.getSeq(),
              choice.getContent(),
              choice.getExplanation()
          ))
          .toList();

      return new QuestionDto(question.getContent(), choices);
    }).toList();

    return new EngduDetailResponse(title, topic, articleDtos, questionDtos);
  }

  public record ArticleDto(
      String content,
      String translation
  ) {

  }

  public record QuestionDto(
      String content,
      List<ChoiceDto> choices
  ) {

  }

  public record ChoiceDto(
      Byte seq,
      String content,
      String explanation
  ) {

  }
}
