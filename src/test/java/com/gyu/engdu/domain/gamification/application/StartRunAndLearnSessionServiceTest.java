package com.gyu.engdu.domain.gamification.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.gyu.engdu.IntegrationTestSupport;
import com.gyu.engdu.domain.gamification.domain.RunAndLearnSession;
import com.gyu.engdu.domain.gamification.domain.RunAndLearnSessionRepository;
import com.gyu.engdu.domain.gamification.exception.RunAndLearnSessionAlreadyStartedException;
import com.gyu.engdu.domain.gamification.exception.RunAndLearnSessionForbiddenAccessException;
import com.gyu.engdu.domain.user.domain.Role;
import com.gyu.engdu.domain.user.domain.User;
import com.gyu.engdu.domain.user.domain.UserRepository;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class StartRunAndLearnSessionServiceTest extends IntegrationTestSupport {

    @Autowired
    private StartRunAndLearnSessionService startRunAndLearnSessionService;

    @Autowired
    private RunAndLearnSessionRepository runAndLearnSessionRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("소유자가 런앤런 세션을 시작하면 시작 시간을 저장한다.")
    void start1() {
        // given
        User user = createUser("test@test.com", "sub123", "testUser");
        User savedUser = userRepository.save(user);

        RunAndLearnSession session = RunAndLearnSession.of(savedUser, 1234);
        RunAndLearnSession savedSession = runAndLearnSessionRepository.save(session);

        LocalDateTime startTime = LocalDateTime.of(2025, 5, 27, 16, 15);

        // when
        startRunAndLearnSessionService.start(savedUser.getId(), savedSession.getId(), startTime);

        // then
        RunAndLearnSession foundSession = runAndLearnSessionRepository.findById(savedSession.getId()).orElseThrow();
        assertThat(foundSession.getStartedAt()).isEqualTo(startTime);
    }

    @Test
    @DisplayName("소유자가 아닌 사용자가 런앤런 세션을 시작하면 예외가 발생한다.")
    void start2() {
        // given
        User owner = createUser("owner@test.com", "sub1", "owner");
        User otherUser = createUser("other@test.com", "sub2", "other");

        userRepository.save(owner);
        userRepository.save(otherUser);

        RunAndLearnSession session = RunAndLearnSession.of(owner, 1234);
        RunAndLearnSession savedSession = runAndLearnSessionRepository.save(session);

        LocalDateTime startTime = LocalDateTime.now();

        // when & then
        assertThatThrownBy(
                () -> startRunAndLearnSessionService.start(otherUser.getId(), savedSession.getId(), startTime))
                .isInstanceOf(RunAndLearnSessionForbiddenAccessException.class);
    }

    @Test
    @DisplayName("이미 시작된 세션을 다시 시작하면 예외가 발생한다.")
    void start3() {
        // given
        User owner = createUser("owner@test.com", "sub1", "owner");
        userRepository.save(owner);

        RunAndLearnSession session = RunAndLearnSession.of(owner, 1234);
        RunAndLearnSession savedSession = runAndLearnSessionRepository.save(session);

        LocalDateTime firstStartTime = LocalDateTime.of(2023, 1, 1, 10, 0);
        startRunAndLearnSessionService.start(owner.getId(), savedSession.getId(), firstStartTime);

        LocalDateTime secondStartTime = LocalDateTime.of(2023, 1, 1, 10, 5);

        // when & then
        assertThatThrownBy(
                () -> startRunAndLearnSessionService.start(owner.getId(), savedSession.getId(), secondStartTime))
                .isInstanceOf(RunAndLearnSessionAlreadyStartedException.class);
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
