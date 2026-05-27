package com.gyu.engdu.domain.gamification.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.gyu.engdu.domain.gamification.exception.RunAndLearnSessionAlreadyStartedException;
import com.gyu.engdu.domain.gamification.exception.RunAndLearnSessionForbiddenAccessException;
import com.gyu.engdu.domain.user.domain.Role;
import com.gyu.engdu.domain.user.domain.User;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

class RunAndLearnSessionTest {

    @Test
    @DisplayName("사용자와 세션 소유자가 일치하면, validateOwner 메서드 검증에 성공한다.")
    void validateOwner1() {
        // given
        Long ownerId = 1L;
        int seed = 12345;
        User user = createUser(ownerId);

        RunAndLearnSession session = RunAndLearnSession.of(user, seed);

        // when & then
        assertThatCode(() -> session.validateOwner(ownerId))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("사용자와 세션 소유자가 다르면, 예외가 발생한다")
    void validateOwner2() {
        // given
        Long ownerId = 1L;
        Long otherUserId = 2L;
        int seed = 1234;
        User user = createUser(ownerId);

        RunAndLearnSession session = RunAndLearnSession.of(user, seed);

        // when & then
        assertThatThrownBy(() -> session.validateOwner(otherUserId))
                .isInstanceOf(RunAndLearnSessionForbiddenAccessException.class);
    }

    @Test
    @DisplayName("스피드 퀴즈 세션 시작에 성공한다")
    void start1() {
        // given
        User user = createUser(1L);
        int seed = 12345;
        LocalDateTime startTime = LocalDateTime.of(2026, 5, 27, 14, 32);
        RunAndLearnSession session = RunAndLearnSession.of(user, seed);

        // when
        session.start(startTime);

        // then
        assertThat(session.getStartedAt()).isEqualTo(startTime);
    }

    @Test
    @DisplayName("이미 시작된 세션을 다시 시작하면, 예외가 발생한다")
    void start2() {
        // given
        User user = createUser(1L);
        int seed = 12345;
        LocalDateTime startTime = LocalDateTime.of(2023, 1, 1, 0, 0);
        RunAndLearnSession session = RunAndLearnSession.of(user, seed);

        // 처음 세션 시작
        session.start(startTime);

        // when & then
        assertThatThrownBy(() -> session.start(startTime))
                .isInstanceOf(RunAndLearnSessionAlreadyStartedException.class);
    }

    @Test
    @DisplayName("세션 생성 시 score의 초기값은 0이다.")
    void init() {
        // given
        User user = createUser(1L);
        int seed = 12345;

        // when
        RunAndLearnSession session = RunAndLearnSession.of(user, seed);

        // then
        assertThat(session.getScore()).isZero();
    }

    private User createUser(Long id) {
        User user = User.builder()
                .email("test@test.com")
                .role(Role.ROLE_USER)
                .sub("sub123")
                .name("testUser")
                .build();
        ReflectionTestUtils.setField(user, "id", id);
        return user;
    }
}
