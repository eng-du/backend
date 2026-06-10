package com.gyu.engdu.domain.gamification.application;

import com.gyu.engdu.domain.gamification.application.dto.response.StartRunAndLearnSessionResponse;
import java.time.LocalDateTime;
import java.util.concurrent.ThreadLocalRandom;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RunAndLearnSessionFacade {

    private final CreateRunAndLearnSessionService createRunAndLearnSessionService;
    private final StartRunAndLearnSessionService startRunAndLearnSessionService;

    @Transactional
    public StartRunAndLearnSessionResponse createAndStart(Long userId, LocalDateTime startTime) {
        int seed = ThreadLocalRandom.current().nextInt();
        Long sessionId = createRunAndLearnSessionService.create(userId, seed);

        startRunAndLearnSessionService.start(userId, sessionId, startTime);

        return StartRunAndLearnSessionResponse.of(sessionId, startTime);
    }
}
