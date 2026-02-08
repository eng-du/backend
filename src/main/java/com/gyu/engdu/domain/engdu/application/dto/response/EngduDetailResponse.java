package com.gyu.engdu.domain.engdu.application.dto.response;

import com.gyu.engdu.domain.engdu.domain.Article;
import com.gyu.engdu.domain.engdu.domain.ArticleChunk;
import com.gyu.engdu.domain.engdu.domain.Choice;
import com.gyu.engdu.domain.engdu.domain.Engdu;
import com.gyu.engdu.domain.engdu.domain.Question;
import java.util.List;

public record EngduDetailResponse(
        Long engduId,
        Meta meta,
        List<Part> parts) {

    public static EngduDetailResponse fromEntity(Engdu engdu) {
        List<Part> parts = new java.util.ArrayList<>();
        List<Article> articles = engdu.getArticles();
        List<Question> questions = engdu.getQuestions();

        for (int i = 0; i < articles.size(); i++) {
            Article article = articles.get(i);
            int firstQuestionIdx = i * 2;
            int secondQuestionIdx = firstQuestionIdx + 1;

            List<Question> partQuestions = questions.subList(firstQuestionIdx, secondQuestionIdx + 1);
            parts.add(Part.of(article, partQuestions));
        }

        return new EngduDetailResponse(
                engdu.getId(),
                new Meta(engdu.getTitle(), engdu.getTopic()),
                parts);
    }

    public record Meta(String title, String topic) {
    }

    public record Part(
            ArticleResponse article,
            List<QuestionResponse> questions) {
        public static Part of(Article article, List<Question> questions) {
            return new Part(
                    ArticleResponse.of(article),
                    questions.stream()
                            .map(QuestionResponse::fromEntity)
                            .toList());
        }
    }

    public record ArticleResponse(List<ChunkResponse> chunks) {
        public static ArticleResponse of(Article article) {
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
