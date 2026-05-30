package com.gyu.engdu.domain.gamification.application.cache;

import java.util.List;
import java.util.Optional;

public interface RunAndLearnCache {

    // 특정 세션의 문제 리스트를 캐시에 저장
    void saveSessionQuestionIds(Long sessionId, List<Long> questionIds);

    // 특정 세션의 문제 리스트를 캐시에서 조회
    Optional<List<Long>> getSessionQuestionIds(Long sessionId);

    // 특정 세션의 캐시 삭제
    void removeSessionQuestionIds(Long sessionId);

    // 전체 문제 리스트를 캐시에 저장
    void saveAllQuestionIds(List<Long> questionIds);

    // 전체 문제 리스트를 캐시에서 조회
    Optional<List<Long>> getAllQuestionIds();

}
