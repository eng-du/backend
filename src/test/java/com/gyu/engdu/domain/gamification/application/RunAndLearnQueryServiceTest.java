package com.gyu.engdu.domain.gamification.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.gyu.engdu.IntegrationTestSupport;
import com.gyu.engdu.domain.gamification.domain.RunAndLearnSession;
import com.gyu.engdu.domain.gamification.domain.RunAndLearnSessionRepository;
import com.gyu.engdu.domain.gamification.exception.RunAndLearnSessionNotFoundException;
import com.gyu.engdu.domain.user.domain.Role;
import com.gyu.engdu.domain.user.domain.User;
import com.gyu.engdu.domain.user.domain.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class RunAndLearnQueryServiceTest extends IntegrationTestSupport {

    @Autowired
    private RunAndLearnQueryService runAndLearnQueryService;

    @Autowired
    private RunAndLearnSessionRepository runAndLearnSessionRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("저장된 세션의 ID로 조회하면 정상적으로 엔티티가 반환된다.")
    void findExistingSession1() {
        // given
        User user = createUser("test@test.com", "sub123", "testUser");
        User savedUser = userRepository.save(user);

        RunAndLearnSession session = RunAndLearnSession.of(savedUser, 1234);
        RunAndLearnSession savedSession = runAndLearnSessionRepository.save(session);

        // when
        RunAndLearnSession foundSession = runAndLearnQueryService.findExistingSession(savedSession.getId());

        // then
        assertThat(foundSession).isNotNull();
        assertThat(foundSession.getId()).isEqualTo(savedSession.getId());
    }

    @Test
    @DisplayName("존재하지 않는 세션 ID로 조회하려 하면 예외가 발생한다.")
    void findExistingSession2() {
        // given
        Long notExistingSessionId = 9999L;

        // when & then
        assertThatThrownBy(() -> runAndLearnQueryService.findExistingSession(notExistingSessionId))
                .isInstanceOf(RunAndLearnSessionNotFoundException.class);
    }

    private User createUser(String email, String sub, String name) {
        return User.builder()
                .email(email)
                .role(Role.ROLE_USER)
                .sub(sub)
                .name(name)
                .build();
    }
}
