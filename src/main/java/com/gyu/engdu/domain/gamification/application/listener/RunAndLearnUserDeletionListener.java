package com.gyu.engdu.domain.gamification.application.listener;

import com.gyu.engdu.domain.gamification.domain.RunAndLearnRankingRepository;
import com.gyu.engdu.domain.gamification.domain.RunAndLearnSessionRepository;
import com.gyu.engdu.domain.user.domain.event.UserDeletedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
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
