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
        List<Part> parts) {

    public static EngduDetailResponse fromEntity(Engdu engdu) {
        List<Part> parts = engdu.getParts().stream()
                .map(Part::fromEntity)
                .toList();

        return new EngduDetailResponse(
                engdu.getId(),
                new Meta(engdu.getTitle(), engdu.getTopic(), engdu.getLikeStatus()),
                parts);
    }

    public record Meta(
            String title,
            String topic,
            LikeStatus likeStatus) {
    }

    public record Part(
            PartType partType,
            PartStatus status,
            ArticleResponse article,
            List<QuestionResponse> questions) {

        /**
         * Part 엔티티를 DTO로 변환합니다.
         * DONE 상태인 경우에만 article/questions를 채우고, 그 외(PENDING/CREATING/FAILED)는 null을
         * 반환합니다.
         * 클라이언트는 status 값으로 폴링 여부를 판단합니다.
         */
        public static Part fromEntity(com.gyu.engdu.domain.engdu.domain.Part part) {
            if (part.getStatus() != PartStatus.DONE) {
                return new Part(part.getPartType(), part.getStatus(), null, null);
            }
            return new Part(
                    part.getPartType(),
                    part.getStatus(),
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
