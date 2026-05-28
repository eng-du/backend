package com.gyu.engdu.domain.gamification.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.gyu.engdu.IntegrationTestSupport;
import com.gyu.engdu.domain.gamification.domain.RunAndLearnSession;
import com.gyu.engdu.domain.gamification.domain.RunAndLearnSessionRepository;
import com.gyu.engdu.domain.gamification.exception.RunAndLearnSessionNotFoundException;
import com.gyu.engdu.domain.gamification.domain.RunAndLearnQuestion;
import com.gyu.engdu.domain.gamification.domain.RunAndLearnQuestionRepository;
import com.gyu.engdu.domain.gamification.exception.RunAndLearnQuestionExhaustedException;
import com.gyu.engdu.domain.gamification.exception.RunAndLearnQuestionNotFoundException;
import com.gyu.engdu.domain.gamification.application.dto.response.RunAndLearnQuestionResponse;
import com.gyu.engdu.domain.user.domain.Role;
import com.gyu.engdu.domain.user.domain.User;
import com.gyu.engdu.domain.user.domain.UserRepository;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class RunAndLearnQueryServiceTest extends IntegrationTestSupport {

        @Autowired
        private RunAndLearnQueryService runAndLearnQueryService;

        @Autowired
        private RunAndLearnSessionRepository runAndLearnSessionRepository;

        @Autowired
        private RunAndLearnQuestionRepository runAndLearnQuestionRepository;

        @Autowired
        private UserRepository userRepository;

        @Test
        @DisplayName("저장된 세션의 ID로 조회하면, 정상적으로 엔티티가 반환된다.")
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
        @DisplayName("존재하지 않는 세션 ID로 조회하려 하면, 예외가 발생한다.")
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

        @Test
        @DisplayName("데이터베이스에 질문이 하나도 없으면, 예외가 발생한다")
        void getQuestions1() {
                // given
                runAndLearnQuestionRepository.deleteAllInBatch();

                User user = userRepository.save(createUser("test@test.com", "sub123", "testUser"));
                RunAndLearnSession session = runAndLearnSessionRepository.save(RunAndLearnSession.of(user, 1234));

                // when & then
                assertThatThrownBy(() -> runAndLearnQueryService.getQuestions(user.getId(), session.getId(), 0, 5))
                                .isInstanceOf(RunAndLearnQuestionNotFoundException.class);
        }

        @Test
        @DisplayName("조회 시작 인덱스가 데이터베이스의 최대 문제 ID보다 크거나 같으면, 예외가 발생한다")
        void getQuestions2() {
                // given
                int questionSize = 5;
                createDummyQuestions(questionSize);
                User user = userRepository.save(createUser("test2@test.com", "sub456", "testUser2"));
                RunAndLearnSession session = runAndLearnSessionRepository.save(RunAndLearnSession.of(user, 1234));

                Long maxId = runAndLearnQuestionRepository.findMaxId().get();
                int startIndex1 = maxId.intValue();
                int startIndex2 = maxId.intValue() + 10;

                // when & then
                assertThatThrownBy(() -> runAndLearnQueryService.getQuestions(user.getId(), session.getId(),
                                startIndex1, 5))
                                .isInstanceOf(RunAndLearnQuestionExhaustedException.class);

                assertThatThrownBy(() -> runAndLearnQueryService.getQuestions(user.getId(), session.getId(),
                                startIndex2, 5))
                                .isInstanceOf(RunAndLearnQuestionExhaustedException.class);
        }

        @Test
        @DisplayName("동일한 시드를 가진 세션에서 문제를 조회하면, 같은 문제들이 반환된다")
        void getQuestions3() {
                // given
                createDummyQuestions(15);
                User user = userRepository.save(createUser("test3@test.com", "sub789", "testUser3"));

                int sameSeed = 777;
                RunAndLearnSession session1 = runAndLearnSessionRepository.save(RunAndLearnSession.of(user, sameSeed));
                RunAndLearnSession session2 = runAndLearnSessionRepository.save(RunAndLearnSession.of(user, sameSeed));

                // when
                List<RunAndLearnQuestionResponse> result1 = runAndLearnQueryService.getQuestions(user.getId(),
                                session1.getId(),
                                0, 10);
                List<RunAndLearnQuestionResponse> result2 = runAndLearnQueryService.getQuestions(user.getId(),
                                session2.getId(),
                                0, 10);

                // then
                List<Long> ids1 = result1.stream().map(RunAndLearnQuestionResponse::id).collect(Collectors.toList());
                List<Long> ids2 = result2.stream().map(RunAndLearnQuestionResponse::id).collect(Collectors.toList());

                assertThat(ids1).containsExactlyElementsOf(ids2);
        }

        @Test
        @DisplayName("다른 시드를 가진 세션에서 문제를 조회하면 다른 문제들이 반환된다")
        void getQuestions4() {
                // given
                createDummyQuestions(15);
                User user = userRepository.save(createUser("test4@test.com", "sub000", "testUser4"));

                int seed = 111;
                int anotherSeed = 222;

                RunAndLearnSession session1 = runAndLearnSessionRepository.save(RunAndLearnSession.of(user, seed));
                RunAndLearnSession session2 = runAndLearnSessionRepository
                                .save(RunAndLearnSession.of(user, anotherSeed));

                // when
                List<RunAndLearnQuestionResponse> result1 = runAndLearnQueryService.getQuestions(user.getId(),
                                session1.getId(),
                                0, 10);
                List<RunAndLearnQuestionResponse> result2 = runAndLearnQueryService.getQuestions(user.getId(),
                                session2.getId(),
                                0, 10);

                // then
                List<Long> ids1 = result1.stream().map(RunAndLearnQuestionResponse::id).collect(Collectors.toList());
                List<Long> ids2 = result2.stream().map(RunAndLearnQuestionResponse::id).collect(Collectors.toList());

                assertThat(ids1).isNotEqualTo(ids2);
        }

        private void createDummyQuestions(int count) {

                List<RunAndLearnQuestion> questions = IntStream.range(0, count)
                                .mapToObj(i -> RunAndLearnQuestion.builder()
                                                .question("Question " + i)
                                                .answer(1)
                                                .choice1("Choice 1")
                                                .choice2("Choice 2")
                                                .choice3("Choice 3")
                                                .explanation("Explanation " + i)
                                                .build())
                                .collect(Collectors.toList());
                runAndLearnQuestionRepository.saveAll(questions);
        }
}
