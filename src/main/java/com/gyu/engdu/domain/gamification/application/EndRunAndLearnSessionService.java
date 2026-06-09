package com.gyu.engdu.domain.gamification.application;

import com.gyu.engdu.domain.gamification.application.cache.RunAndLearnCacheService;
import com.gyu.engdu.domain.gamification.application.dto.request.EndRunAndLearnSessionRequest;
import com.gyu.engdu.domain.gamification.application.dto.response.EndRunAndLearnSessionResponse;
import com.gyu.engdu.domain.gamification.application.dto.response.RunAndLearnRankingResult;
import com.gyu.engdu.domain.gamification.domain.RunAndLearnEndValidationService;
import com.gyu.engdu.domain.gamification.domain.RunAndLearnQuestion;
import com.gyu.engdu.domain.gamification.domain.RunAndLearnSession;
import com.gyu.engdu.domain.gamification.exception.InvalidRunAndLearnPlayException;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class EndRunAndLearnSessionService {

    private final RunAndLearnQueryService runAndLearnQueryService;
    private final RunAndLearnCacheService runAndLearnCacheService;
    private final RunAndLearnEndValidationService runAndLearnEndValidationService;
    private final UpdateRunAndLearnRankingService updateRunAndLearnRankingService;

    public void endSession(
            Long userId, Long sessionId, EndRunAndLearnSessionRequest request,
            LocalDateTime endTime
    ) {
        // 세션 조회 및 소유자 검증
        RunAndLearnSession session = runAndLearnQueryService.findExistingSession(sessionId);
        session.validateOwner(userId);

        // 캐시에서 세션의 문제 순서 가져오기
        List<Long> expectedSessionQuestionIds = runAndLearnCacheService.getSessionQuestionIds(
                sessionId,
                session.getSeed());

        int totalQuestions = expectedSessionQuestionIds.size();
        int submitCount = request.submittedAnswers().size();

        // 제출한 문제 수 오류 검증
        validateSubmitCount(submitCount, totalQuestions);

        List<Long> targetQuestionIds = expectedSessionQuestionIds.subList(0, submitCount);

        List<RunAndLearnQuestion> expectedQuestions = runAndLearnQueryService
                .getQuestionAndRestoreOrder(targetQuestionIds);

        runAndLearnEndValidationService.validate(
                session,
                totalQuestions,
                request.submittedAnswers(),
                expectedQuestions,
                request.clientTotalScore());

        session.end(request.clientTotalScore(), endTime);
        runAndLearnCacheService.removeSessionQuestionIds(sessionId);
        updateRunAndLearnRankingService.updateRanking(session.getUser(), request.clientTotalScore(), endTime);
    }

    private void validateSubmitCount(int submitCount, int totalQuestions) {
        if (submitCount > totalQuestions) {
            throw new InvalidRunAndLearnPlayException("제출된 답변 수가 전체 문제 수보다 많습니다.");
        }
    }
}
