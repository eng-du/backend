package com.gyu.engdu.domain.gamification.application.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CaffeineRunAndLearnCache implements RunAndLearnCache {

    private static final String ALL_IDS_KEY = "ALL_QUESTION_IDS";

    // 세션별 셔플된 문제 ID 캐시
    private final Cache<Long, List<Long>> sessionCache;

    // 전체 문제 ID 캐시
    private final Cache<String, List<Long>> allIdsCache;

    @Override
    public void saveSessionQuestionIds(Long sessionId, List<Long> questionIds) {
        sessionCache.put(sessionId, questionIds);
    }

    @Override
    public Optional<List<Long>> getSessionQuestionIds(Long sessionId) {
        return Optional.ofNullable(sessionCache.getIfPresent(sessionId));
    }

    @Override
    public void removeSessionQuestionIds(Long sessionId) {
        sessionCache.invalidate(sessionId);
    }

    @Override
    public void saveAllQuestionIds(List<Long> questionIds) {
        allIdsCache.put(ALL_IDS_KEY, questionIds);
    }

    @Override
    public Optional<List<Long>> getAllQuestionIds() {
        return Optional.ofNullable(allIdsCache.getIfPresent(ALL_IDS_KEY));
    }
}
