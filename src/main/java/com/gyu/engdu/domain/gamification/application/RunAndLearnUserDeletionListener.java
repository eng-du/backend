package com.gyu.engdu.domain.gamification.application;

import com.gyu.engdu.domain.gamification.domain.RunAndLearnRankingRepository;
import com.gyu.engdu.domain.gamification.domain.RunAndLearnSessionRepository;
import com.gyu.engdu.domain.user.domain.event.UserDeletedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RunAndLearnUserDeletionListener {

    private final RunAndLearnRankingRepository runAndLearnRankingRepository;
    private final RunAndLearnSessionRepository runAndLearnSessionRepository;

    @EventListener
    public void handleUserDeletedEvent(UserDeletedEvent event) {
        Long userId = event.userId();
        runAndLearnRankingRepository.deleteByUserId(userId);
        runAndLearnSessionRepository.deleteByUserId(userId);
    }
}
