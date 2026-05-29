package com.gyu.engdu.domain.gamification.application;

import com.gyu.engdu.domain.gamification.application.dto.request.EndRunAndLearnSessionRequest;
import com.gyu.engdu.domain.gamification.domain.RunAndLearnEndValidationService;
import com.gyu.engdu.domain.gamification.domain.RunAndLearnQuestion;
import com.gyu.engdu.domain.gamification.domain.RunAndLearnQuestionRepository;
import com.gyu.engdu.domain.gamification.domain.RunAndLearnSession;
import com.gyu.engdu.domain.gamification.exception.InvalidRunAndLearnPlayException;
import com.gyu.engdu.domain.gamification.exception.RunAndLearnQuestionNotFoundException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.LongStream;
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
    private final RunAndLearnQuestionRepository runAndLearnQuestionRepository;
    private final RunAndLearnEndValidationService runAndLearnEndValidationService;

    public void endSession(Long userId, Long sessionId, EndRunAndLearnSessionRequest request,
            LocalDateTime endTime) {
        RunAndLearnSession session = runAndLearnQueryService.findExistingSession(sessionId);
        session.validateOwner(userId);

        Long maxId = runAndLearnQuestionRepository.findMaxId()
                .orElseThrow(RunAndLearnQuestionNotFoundException::new);

        int submitCount = request.submittedAnswers().size();

        if (submitCount > maxId) {
            throw new InvalidRunAndLearnPlayException("제출된 답변 수가 전체 문제 수보다 많습니다.");
        }

        List<Long> expectedSequence = LongStream.rangeClosed(1, maxId)
                .boxed()
                .collect(Collectors.toList());

        Collections.shuffle(expectedSequence, new Random(session.getSeed()));

        List<Long> expectedQuestionIds = expectedSequence.subList(0, submitCount);

        List<RunAndLearnQuestion> expectedQuestions = runAndLearnQueryService
                .getQuestionAndRestoreOrder(expectedQuestionIds);

        runAndLearnEndValidationService.validate(
                session,
                maxId,
                request.submittedAnswers(),
                expectedQuestions,
                request.clientTotalScore());

        session.end(request.clientTotalScore(), endTime);
    }
}
