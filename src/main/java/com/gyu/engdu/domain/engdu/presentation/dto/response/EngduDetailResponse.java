package com.gyu.engdu.domain.engdu.presentation.dto.response;

import com.gyu.engdu.domain.engdu.domain.Article;
import com.gyu.engdu.domain.engdu.domain.Engdu;
import com.gyu.engdu.domain.engdu.domain.Question;
import java.util.List;

public record EngduDetailResponse(
        Long engduId,
        String title,
        String topic,
        List<ArticleDto> articles,
        List<QuestionDto> questions) {

    public static EngduDetailResponse fromEntity(Engdu engdu) {
        Long engduId = engdu.getId();
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
                            choice.getExplanation()))
                    .toList();

            Integer answer = question.isCorrected() ? (int) question.getAnswer() : null;

            return new QuestionDto(
                    question.getId(),
                    question.getContent(),
                    answer,
                    question.isCorrected(),
                    choices);
        }).toList();

        return new EngduDetailResponse(engduId, title, topic, articleDtos, questionDtos);
    }

    public record ArticleDto(
            String content,
            String translation) {

    }

    public record QuestionDto(
            Long questionId,
            String content,
            Integer answer,
            boolean isCorrected,
            List<ChoiceDto> choices) {

    }

    public record ChoiceDto(
            Byte seq,
            String content,
            String explanation) {

    }
}
