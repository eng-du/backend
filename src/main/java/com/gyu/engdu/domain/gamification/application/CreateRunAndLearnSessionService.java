package com.gyu.engdu.domain.gamification.application;

import com.gyu.engdu.domain.gamification.domain.RunAndLearnSession;
import com.gyu.engdu.domain.gamification.domain.RunAndLearnSessionRepository;
import com.gyu.engdu.domain.user.application.UserQueryService;
import com.gyu.engdu.domain.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreateRunAndLearnSessionService {

    private final UserQueryService userQueryService;
    private final RunAndLearnSessionRepository runAndLearnSessionRepository;

    @Transactional
    public Long create(Long userId, int seed) {
        User user = userQueryService.findExistingUser(userId);

        RunAndLearnSession session = RunAndLearnSession.of(user, seed);
        runAndLearnSessionRepository.save(session);

        return session.getId();
    }
}
