package com.gyu.engdu.domain.gamification.application;

import com.gyu.engdu.domain.gamification.domain.RunAndLearnSession;
import com.gyu.engdu.domain.gamification.domain.RunAndLearnSessionRepository;
import com.gyu.engdu.domain.gamification.exception.RunAndLearnSessionNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RunAndLearnQueryService {

    private final RunAndLearnSessionRepository runAndLearnSessionRepository;

    public RunAndLearnSession findExistingSession(Long sessionId) {
        return runAndLearnSessionRepository.findById(sessionId)
                .orElseThrow(() -> new RunAndLearnSessionNotFoundException(sessionId));
    }
}
