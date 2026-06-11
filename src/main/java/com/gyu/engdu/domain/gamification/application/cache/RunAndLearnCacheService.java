package com.gyu.engdu.domain.gamification.application.cache;

import com.gyu.engdu.domain.gamification.domain.RunAndLearnQuestionRepository;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RunAndLearnCacheService {

    private final RunAndLearnCache runAndLearnCache;
    private final RunAndLearnQuestionRepository runAndLearnQuestionRepository;

    // 캐시에서 세션의 문제 ID 리스트를 조회하거나 생성 후 반환
    public List<Long> getSessionQuestionIds(Long sessionId, int seed) {
        log.info("세션 문제 캐시 조회 요청: sessionId={}", sessionId);
        return runAndLearnCache.getSessionQuestionIds(sessionId)
                .orElseGet(() -> createAndCacheShuffledQuestionIds(sessionId, seed));
    }

    // 전체 문제 ID를 조회한 뒤 시드에 맞게 섞어 세션 캐시에 저장하고 반환
    private List<Long> createAndCacheShuffledQuestionIds(Long sessionId, int seed) {
        List<Long> allIds = getAllQuestionIds();

        // 세션의 시드 번호에 맞게 런앤런의 문제 순서를 섞음
        List<Long> sessionQuestionIds = new ArrayList<>(allIds);
        Collections.shuffle(sessionQuestionIds, new Random(seed));

        log.info("새로운 세션 문제 캐시 등록: sessionId={}", sessionId);
        runAndLearnCache.saveSessionQuestionIds(sessionId, sessionQuestionIds);

        return sessionQuestionIds;
    }

    // 캐시에서 전체 문제 ID 리스트를 조회하거나 DB에서 조회 후 반환
    public List<Long> getAllQuestionIds() {
        log.info("전체 문제 캐시 조회 요청");
        return runAndLearnCache.getAllQuestionIds()
                .orElseGet(this::fetchAndCacheAllQuestionIds);
    }

    // DB에서 전체 문제 ID 목록을 조회하여 캐시에 저장한 후 반환
    private List<Long> fetchAndCacheAllQuestionIds() {
        List<Long> allIds = runAndLearnQuestionRepository.findAllIds();
        log.info("전체 문제 캐시 등록");
        runAndLearnCache.saveAllQuestionIds(allIds);
        return allIds;
    }

    // 세션의 캐시 데이터를 삭제
    public void removeSessionQuestionIds(Long sessionId) {
        log.info("세션 문제 캐시 삭제: sessionId={}", sessionId);
        runAndLearnCache.removeSessionQuestionIds(sessionId);
    }
}
