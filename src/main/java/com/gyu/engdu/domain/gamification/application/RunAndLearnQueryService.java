package com.gyu.engdu.domain.gamification.application;

import com.gyu.engdu.domain.gamification.application.dto.response.RunAndLearnQuestionResponse;
import com.gyu.engdu.domain.gamification.domain.RunAndLearnQuestion;
import com.gyu.engdu.domain.gamification.domain.RunAndLearnQuestionRepository;
import com.gyu.engdu.domain.gamification.domain.RunAndLearnSession;
import com.gyu.engdu.domain.gamification.domain.RunAndLearnSessionRepository;
import com.gyu.engdu.domain.gamification.exception.RunAndLearnQuestionExhaustedException;
import com.gyu.engdu.domain.gamification.exception.RunAndLearnQuestionNotFoundException;
import com.gyu.engdu.domain.gamification.exception.RunAndLearnSessionNotFoundException;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.LongStream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RunAndLearnQueryService {

    private final RunAndLearnSessionRepository runAndLearnSessionRepository;
    private final RunAndLearnQuestionRepository runAndLearnQuestionRepository;

    public RunAndLearnSession findExistingSession(Long sessionId) {
        return runAndLearnSessionRepository.findById(sessionId)
                .orElseThrow(() -> new RunAndLearnSessionNotFoundException(sessionId));
    }

    public List<RunAndLearnQuestionResponse> getQuestions(Long userId, Long sessionId,
            int startIndex,
            int count) {
        // 세션 조회 후 사용자 검증
        RunAndLearnSession session = findExistingSession(sessionId);
        session.validateOwner(userId);

        Long maxId = validateAndGetMaxId(startIndex);

        // 문제 id 범위 생성
        List<Long> allIds = LongStream.rangeClosed(1, maxId)
                .boxed()
                .collect(Collectors.toList());

        // 시드 기반 문제 순서 섞기
        int seed = session.getSeed();
        Collections.shuffle(allIds, new Random(seed));

        int endIndex = Math.min(startIndex + count, maxId.intValue());
        List<Long> questionIds = allIds.subList(startIndex, endIndex);

        List<RunAndLearnQuestion> questions = runAndLearnQuestionRepository.findAllById(questionIds);

        // 오름차순 정렬
        questions.sort((a, b) -> a.getId().compareTo(b.getId()));

        return questions.stream()
                .map(RunAndLearnQuestionResponse::of)
                .collect(Collectors.toList());
    }

    // 문제의 최대 id를 가져오고 검증
    private Long validateAndGetMaxId(int startIndex) {
        Long maxId = runAndLearnQuestionRepository.findMaxId()
                .orElseThrow(RunAndLearnQuestionNotFoundException::new);

        if (startIndex >= maxId) {
            throw new RunAndLearnQuestionExhaustedException();
        }

        return maxId;
    }
}
