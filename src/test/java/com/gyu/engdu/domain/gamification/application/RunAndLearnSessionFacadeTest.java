package com.gyu.engdu.domain.gamification.application;

import static org.assertj.core.api.Assertions.assertThat;

import com.gyu.engdu.IntegrationTestSupport;
import com.gyu.engdu.domain.gamification.application.dto.response.StartRunAndLearnSessionResponse;
import com.gyu.engdu.domain.gamification.domain.RunAndLearnSession;
import com.gyu.engdu.domain.gamification.domain.RunAndLearnSessionRepository;
import com.gyu.engdu.domain.gamification.domain.enums.RunAndLearnSessionStatus;
import com.gyu.engdu.domain.user.domain.Role;
import com.gyu.engdu.domain.user.domain.User;
import com.gyu.engdu.domain.user.domain.UserRepository;
import com.gyu.engdu.domain.auth.domain.OAuthProvider;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class RunAndLearnSessionFacadeTest extends IntegrationTestSupport {

    @Autowired
    private RunAndLearnSessionFacade runAndLearnSessionFacade;

    @Autowired
    private RunAndLearnSessionRepository runAndLearnSessionRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("세션 생성과 시작이 순차적으로 진행되어 정상적인 응답과 엔티티 상태를 보장한다.")
    void createAndStart() {
        // given
        User user = createUser("test@test.com", "sub123", "testUser");
        User savedUser = userRepository.save(user);

        LocalDateTime startTime = LocalDateTime.of(2025, 5, 27, 16, 15);

        // when
        StartRunAndLearnSessionResponse response = runAndLearnSessionFacade.createAndStart(savedUser.getId(), startTime);

        // then
        assertThat(response.sessionId()).isNotNull();
        assertThat(response.startTime()).isEqualTo(startTime);

        RunAndLearnSession foundSession = runAndLearnSessionRepository.findById(response.sessionId()).orElseThrow();
        assertThat(foundSession.getStartedAt()).isEqualTo(startTime);
        assertThat(foundSession.getStatus()).isEqualTo(RunAndLearnSessionStatus.PROGRESS);
        assertThat(foundSession.getUser().getId()).isEqualTo(savedUser.getId());
    }

    private User createUser(String email, String sub, String name) {
        return User.builder()
                .email(email)
                .role(Role.ROLE_USER)
                .sub(sub)
                .name(name)
                .provider(OAuthProvider.GOOGLE)
                .build();
    }
}
