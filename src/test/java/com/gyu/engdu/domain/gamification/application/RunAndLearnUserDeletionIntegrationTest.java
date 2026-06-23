package com.gyu.engdu.domain.gamification.application;

import static org.assertj.core.api.Assertions.assertThat;

import com.gyu.engdu.IntegrationTestSupport;
import com.gyu.engdu.domain.gamification.domain.RunAndLearnRanking;
import com.gyu.engdu.domain.gamification.domain.RunAndLearnRankingRepository;
import com.gyu.engdu.domain.gamification.domain.RunAndLearnSession;
import com.gyu.engdu.domain.gamification.domain.RunAndLearnSessionRepository;
import com.gyu.engdu.domain.user.application.DeleteUserService;
import com.gyu.engdu.domain.user.domain.Role;
import com.gyu.engdu.domain.user.domain.User;
import com.gyu.engdu.domain.user.domain.UserRepository;
import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class RunAndLearnUserDeletionIntegrationTest extends IntegrationTestSupport {

    @Autowired
    private DeleteUserService deleteUserService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RunAndLearnRankingRepository runAndLearnRankingRepository;

    @Autowired
    private RunAndLearnSessionRepository runAndLearnSessionRepository;

    @Autowired
    private EntityManager em;

    @Test
    @DisplayName("회원 탈퇴 시 RunAndLearnRanking과 RunAndLearnSession이 삭제된다")
    void deleteUser_ShouldDeleteGamificationData() {
        // given
        User targetUser = createUser("target@test.com", "sub1", "Target");
        userRepository.save(targetUser);

        User otherUser = createUser("other@test.com", "sub2", "Other");
        userRepository.save(otherUser);

        // Target 회원의 데이터 세팅
        runAndLearnRankingRepository.save(
                RunAndLearnRanking.createWeeklyRanking(targetUser, 1, 100, LocalDateTime.now()));
        runAndLearnSessionRepository.save(RunAndLearnSession.of(targetUser, 123));

        // Other 회원의 데이터 세팅 (노이즈)
        runAndLearnRankingRepository.save(
                RunAndLearnRanking.createWeeklyRanking(otherUser, 1, 200, LocalDateTime.now()));
        runAndLearnSessionRepository.save(RunAndLearnSession.of(otherUser, 456));

        em.flush();
        em.clear();
        // when
        deleteUserService.delete(targetUser.getId());

        // then
        assertThat(userRepository.findById(targetUser.getId())).isEmpty();

        List<RunAndLearnRanking> targetRankings = runAndLearnRankingRepository.findAllByUserId(
                targetUser.getId());
        assertThat(targetRankings).isEmpty();

        List<RunAndLearnSession> targetSessions = runAndLearnSessionRepository.findAllByUserId(
                targetUser.getId());
        assertThat(targetSessions).isEmpty();

        // Other 회원의 데이터는 남아있어야 함
        assertThat(userRepository.findById(otherUser.getId())).isPresent();

        List<RunAndLearnRanking> otherRankings = runAndLearnRankingRepository.findAllByUserId(
                otherUser.getId());
        assertThat(otherRankings).hasSize(1);

        List<RunAndLearnSession> otherSessions = runAndLearnSessionRepository.findAllByUserId(
                otherUser.getId());
        assertThat(otherSessions).hasSize(1);
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
