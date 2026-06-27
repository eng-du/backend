package com.gyu.engdu.domain.gamification.application;

import static org.mockito.Mockito.verify;

import com.gyu.engdu.domain.gamification.application.listener.RunAndLearnUserDeletionListener;
import com.gyu.engdu.domain.gamification.domain.RunAndLearnRankingRepository;
import com.gyu.engdu.domain.gamification.domain.RunAndLearnSessionRepository;
import com.gyu.engdu.domain.user.domain.event.UserDeletedEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RunAndLearnUserDeletionListenerTest {

    @InjectMocks
    private RunAndLearnUserDeletionListener runAndLearnUserDeletionListener;

    @Mock
    private RunAndLearnRankingRepository runAndLearnRankingRepository;

    @Mock
    private RunAndLearnSessionRepository runAndLearnSessionRepository;

    @Test
    @DisplayName("UserDeletedEvent가 수신되면 관련된 랭킹과 세션이 삭제된다")
    void handleUserDeletedEvent_ShouldDeleteRelatedData() {
        // given
        Long deletedUserId = 1L;
        UserDeletedEvent event = new UserDeletedEvent(deletedUserId);

        // when
        runAndLearnUserDeletionListener.handleUserDeletedEvent(event);

        // then
        verify(runAndLearnRankingRepository).deleteByUserId(deletedUserId);
        verify(runAndLearnSessionRepository).deleteByUserId(deletedUserId);
    }
}
