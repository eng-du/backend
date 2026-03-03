package com.gyu.engdu.domain.engdu.application.dto.response;

import com.gyu.engdu.domain.engdu.domain.Article;
import com.gyu.engdu.domain.engdu.domain.ArticleChunk;
import com.gyu.engdu.domain.engdu.domain.Choice;
import com.gyu.engdu.domain.engdu.domain.Engdu;
import com.gyu.engdu.domain.engdu.domain.Question;
import com.gyu.engdu.domain.engdu.domain.enums.LikeStatus;
import com.gyu.engdu.domain.engdu.domain.enums.PartStatus;
import com.gyu.engdu.domain.engdu.domain.enums.PartType;

import java.util.List;

public record EngduDetailResponse(
        Long engduId,
        Meta meta,
        Parts parts) {

    public static EngduDetailResponse fromEntity(Engdu engdu) {
        Part initial = engdu.getParts().stream()
                .filter(p -> p.getPartType() == PartType.INITIAL && p.getStatus() == PartStatus.DONE)
                .findFirst()
                .map(Part::fromEntity)
                .orElse(null);

        Part complete = engdu.getParts().stream()
                .filter(p -> p.getPartType() == PartType.COMPLETE && p.getStatus() == PartStatus.DONE)
                .findFirst()
                .map(Part::fromEntity)
                .orElse(null);

        return new EngduDetailResponse(
                engdu.getId(),
                new Meta(engdu.getTitle(), engdu.getTopic(), engdu.getLikeStatus()),
                new Parts(initial, complete));
    }

    public record Meta(
            String title,
            String topic,
            LikeStatus likeStatus) {
    }

    public record Parts(
            Part INITIAL,
            Part COMPLETE) {
    }

    public record Part(
            ArticleResponse article,
            List<QuestionResponse> questions) {

        public static Part fromEntity(com.gyu.engdu.domain.engdu.domain.Part part) {
            return new Part(
                    ArticleResponse.of(part.getArticle()),
                    part.getQuestions().stream()
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
