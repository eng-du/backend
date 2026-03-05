package com.gyu.engdu.domain.engdu.application;

import com.gyu.engdu.domain.engdu.application.dto.request.GenerateEngduRequest;
import com.gyu.engdu.domain.engdu.application.dto.response.GeneratedEngduResponse;
import com.gyu.engdu.domain.engdu.domain.ArticleChunk;
import com.gyu.engdu.domain.engdu.domain.Engdu;
import com.gyu.engdu.domain.engdu.domain.Part;
import com.gyu.engdu.domain.engdu.domain.PartRepository;
import com.gyu.engdu.domain.engdu.domain.Question;
import com.gyu.engdu.domain.engdu.domain.enums.PartStatus;
import com.gyu.engdu.domain.engdu.domain.enums.PartType;
import com.gyu.engdu.domain.engdu.exception.PartNotFoundException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PartCommandService {

    // SQS visibility timeout과 동일하게 맞춥니다.
    private static final Duration STALE_CREATING_THRESHOLD = Duration.ofMinutes(2);

    private final PartRepository partRepository;

    @Transactional
    public Optional<Part> claimPartCreation(Long engduId, PartType partType) {
        Part part = partRepository.findByEngduIdAndPartTypeWithLock(engduId, partType)
                .orElseThrow(() -> new PartNotFoundException(engduId, partType));

        if (part.isDone() || part.isProcessing()) {
            return Optional.empty();
        }

        // PENDING, FAILED, 또는 stale CREATING → 재처리 허용
        LocalDateTime retryAllowedAt = LocalDateTime.now().plus(STALE_CREATING_THRESHOLD);
        part.startCreation(retryAllowedAt);
        return Optional.of(part);
    }

    @Transactional(readOnly = true)
    public GenerateEngduRequest buildLlmRequest(Long partId, PartType step) {
        Part part = partRepository.findById(partId)
                .orElseThrow(() -> new PartNotFoundException(partId));

        Engdu engdu = part.getEngdu();
        String previousContent = step == PartType.COMPLETE ? getPreviousArticleContent(engdu) : null;
        return GenerateEngduRequest.of(engdu.getTopic(), engdu.getLevel(), step, previousContent);
    }

    @Transactional
    public void saveResultAndMarkDone(Long partId, PartType partType, GeneratedEngduResponse response) {
        Part part = partRepository.findById(partId)
                .orElseThrow(() -> new PartNotFoundException(partId));

        // INITIAL 파트 저장 시 LLM이 생성한 타이틀을 Engdu에 반영합니다.
        if (partType == PartType.INITIAL) {
            part.getEngdu().changeTitle(response.title());
        }

        response.article().toEntity(part);

        response.questions().forEach(questionDto -> {
            Question question = questionDto.toEntity(part);
            questionDto.choices().forEach(choiceDto -> choiceDto.toEntity(question));
        });

        part.changeStatus(PartStatus.DONE);
    }

    @Transactional
    public void markFailed(Long partId) {
        Part part = partRepository.findById(partId)
                .orElseThrow(() -> new PartNotFoundException(partId));
        part.changeStatus(PartStatus.FAILED);
    }

    private String getPreviousArticleContent(Engdu engdu) {
        return engdu.getParts().stream()
                .findFirst()
                .map(Part::getArticle)
                .map(article -> article.getChunks().stream()
                        .map(ArticleChunk::getEn)
                        .collect(Collectors.joining(" ")))
                .orElse("");
    }
}
