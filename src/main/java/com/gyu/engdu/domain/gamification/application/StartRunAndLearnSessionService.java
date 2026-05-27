package com.gyu.engdu.domain.gamification.application;

import com.gyu.engdu.domain.gamification.domain.RunAndLearnSession;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StartRunAndLearnSessionService {

    private final RunAndLearnQueryService runAndLearnQueryService;

    @Transactional
    public void start(Long userId, Long sessionId, LocalDateTime startTime) {
        RunAndLearnSession session = runAndLearnQueryService.findExistingSession(sessionId);

        session.validateOwner(userId);
        session.start(startTime);
    }
}
