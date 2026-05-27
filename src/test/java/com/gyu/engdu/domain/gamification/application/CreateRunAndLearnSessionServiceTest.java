package com.gyu.engdu.domain.gamification.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.gyu.engdu.IntegrationTestSupport;
import com.gyu.engdu.domain.gamification.domain.RunAndLearnSession;
import com.gyu.engdu.domain.gamification.domain.RunAndLearnSessionRepository;
import com.gyu.engdu.domain.user.domain.Role;
import com.gyu.engdu.domain.user.domain.User;
import com.gyu.engdu.domain.user.domain.UserRepository;
import com.gyu.engdu.domain.user.exception.UserNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class CreateRunAndLearnSessionServiceTest extends IntegrationTestSupport {

    @Autowired
    private CreateRunAndLearnSessionService createRunAndLearnSessionService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RunAndLearnSessionRepository runAndLearnSessionRepository;

    @Test
    @DisplayName("유저가 세션을 생성하면 DB에 정상적으로 저장되고 ID가 반환된다.")
    void create1() {
        // given
        User user = createUser("test@test.com", "sub123", "testUser");
        User savedUser = userRepository.save(user);

        int seed = 1234;

        // when
        Long sessionId = createRunAndLearnSessionService.create(savedUser.getId(), seed);

        // then
        assertThat(sessionId).isNotNull();
        RunAndLearnSession session = runAndLearnSessionRepository.findById(sessionId).orElseThrow();
        assertThat(session.getUser().getId()).isEqualTo(savedUser.getId());
        assertThat(session.getSeed()).isEqualTo(seed);
        assertThat(session.getScore()).isZero();
        assertThat(session.getStartedAt()).isNull();
    }

    @Test
    @DisplayName("존재하지 않는 유저가 세션을 생성하면 예외가 발생한다.")
    void create2() {
        // given
        Long notExistingUserId = 9999L;
        int seed = 1234;

        // when & then
        assertThatThrownBy(() -> createRunAndLearnSessionService.create(notExistingUserId, seed))
                .isInstanceOf(UserNotFoundException.class);
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
