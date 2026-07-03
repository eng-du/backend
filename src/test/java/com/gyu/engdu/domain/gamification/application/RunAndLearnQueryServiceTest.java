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
import com.gyu.engdu.domain.gamification.exception.InvalidRunAndLearnStatusException;
import com.gyu.engdu.domain.gamification.application.dto.response.RunAndLearnQuestionResponse;
import com.gyu.engdu.domain.user.domain.Role;
import com.gyu.engdu.domain.user.domain.User;
import com.gyu.engdu.domain.user.domain.UserRepository;
import com.gyu.engdu.domain.gamification.domain.RunAndLearnRanking;
import com.gyu.engdu.domain.gamification.domain.RunAndLearnRankingRepository;
import com.gyu.engdu.domain.gamification.domain.RunAndLearnSeasonCalculator;
import com.gyu.engdu.domain.gamification.domain.enums.RankingType;
import com.gyu.engdu.domain.gamification.application.dto.response.SessionRankingDto;
import com.gyu.engdu.domain.auth.domain.OAuthProvider;
import com.gyu.engdu.domain.gamification.application.dto.response.LeaderboardEntryDto;
import java.time.LocalDateTime;
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
        private RunAndLearnRankingRepository runAndLearnRankingRepository;

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
                                .provider(OAuthProvider.GOOGLE)
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

        @Test
        @DisplayName("주간 리더보드 Top K를 조회할 수 있다.")
        void getTopKRanking() {
                // given
                int lowerScore = 100;
                int higherScore = 200;
                int topK = 5;
                int currentSeason = RunAndLearnSeasonCalculator.calculateSeason(LocalDateTime.now());
                User user1 = userRepository.save(createUser("email1", "sub1", "user1"));
                User user2 = userRepository.save(createUser("email2", "sub2", "user2"));

                runAndLearnRankingRepository.save(
                                RunAndLearnRanking.createWeeklyRanking(user1, currentSeason, lowerScore,
                                                LocalDateTime.now()));
                runAndLearnRankingRepository.save(
                                RunAndLearnRanking.createWeeklyRanking(user2, currentSeason, higherScore,
                                                LocalDateTime.now()));

                // when
                List<LeaderboardEntryDto> result = runAndLearnQueryService.getTopKRanking(RankingType.WEEKLY, topK);

                // then
                assertThat(result).hasSize(2);
                assertThat(result.get(0).rankingInfo().userName()).isEqualTo("user2");
                assertThat(result.get(0).rankingInfo().bestScore()).isEqualTo(higherScore);
                assertThat(result.get(1).rankingInfo().userName()).isEqualTo("user1");
                assertThat(result.get(1).rankingInfo().bestScore()).isEqualTo(lowerScore);
        }

        @Test
        @DisplayName("주간 리더보드 조회 시 동점자가 존재하면, 공동 순위를 부여하고 다음 순위는 건너뛴다.")
        void getTopKRankingWithTies() {
                // given
                int firstPlaceScore = 200;
                int thirdPlaceScore = 100;
                int topK = 5;
                int currentSeason = RunAndLearnSeasonCalculator.calculateSeason(LocalDateTime.now());

                User user1 = userRepository.save(createUser("email1", "sub1", "user1"));
                User user2 = userRepository.save(createUser("email2", "sub2", "user2"));
                User user3 = userRepository.save(createUser("email3", "sub3", "user3"));

                // user1과 user2는 동일한 최고 점수(200), user3은 더 낮은 점수(100)
                runAndLearnRankingRepository.save(
                                RunAndLearnRanking.createWeeklyRanking(user1, currentSeason, firstPlaceScore,
                                                LocalDateTime.now()));
                runAndLearnRankingRepository.save(
                                RunAndLearnRanking.createWeeklyRanking(user2, currentSeason, firstPlaceScore,
                                                LocalDateTime.now()));
                runAndLearnRankingRepository.save(
                                RunAndLearnRanking.createWeeklyRanking(user3, currentSeason, thirdPlaceScore,
                                                LocalDateTime.now()));

                // when
                List<LeaderboardEntryDto> result = runAndLearnQueryService.getTopKRanking(RankingType.WEEKLY, topK);

                // then
                int expectedSize = 3;
                int expectedFirstRank = 1;
                int expectedThirdRank = 3;

                assertThat(result).hasSize(expectedSize);

                // 첫 번째 유저 (1등, 200점)
                assertThat(result.get(0).rank()).isEqualTo(expectedFirstRank);
                assertThat(result.get(0).rankingInfo().bestScore()).isEqualTo(firstPlaceScore);

                // 두 번째 유저 (1등, 200점 - 동점)
                assertThat(result.get(1).rank()).isEqualTo(expectedFirstRank);
                assertThat(result.get(1).rankingInfo().bestScore()).isEqualTo(firstPlaceScore);

                // 세 번째 유저 (3등, 100점 - 순위 건너뜀)
                assertThat(result.get(2).rank()).isEqualTo(expectedThirdRank);
                assertThat(result.get(2).rankingInfo().bestScore()).isEqualTo(thirdPlaceScore);
        }

        @Test
        @DisplayName("내 최고 랭킹 정보를 조회할 수 있다.")
        void getMyRanking() {
                // given
                int lowerScore = 100;
                int higherScore = 200;
                int expectedRank = 2;
                int currentSeason = RunAndLearnSeasonCalculator.calculateSeason(LocalDateTime.now());
                User user1 = userRepository.save(createUser("myEmail@test.com", "mySub", "myName"));
                User user2 = userRepository.save(createUser("other@test.com", "otherSub", "otherName"));

                runAndLearnRankingRepository.save(
                                RunAndLearnRanking.createWeeklyRanking(user1, currentSeason, lowerScore,
                                                LocalDateTime.now()));
                runAndLearnRankingRepository.save(
                                RunAndLearnRanking.createWeeklyRanking(user2, currentSeason, higherScore,
                                                LocalDateTime.now()));

                // when
                LeaderboardEntryDto result = runAndLearnQueryService.getMyRanking(user1.getId(), RankingType.WEEKLY);

                // then
                assertThat(result).isNotNull();
                assertThat(result.rank()).isEqualTo(expectedRank);
                assertThat(result.rankingInfo().userName()).isEqualTo("myName");
                assertThat(result.rankingInfo().bestScore()).isEqualTo(lowerScore);
        }

        @Test
        @DisplayName("특정 점수를 기반으로 예상 등수를 조회할 수 있다.")
        void getExpectedRanking() {
                // given
                int myScore = 300;
                int higherScore = 500;
                int expectedRank = 2;
                int currentSeason = RunAndLearnSeasonCalculator.calculateSeason(LocalDateTime.now());
                User otherUser = userRepository.save(createUser("email4", "sub4", "otherUser"));

                // 나보다 높은 점수를 가진 유저 1명 저장
                runAndLearnRankingRepository
                                .save(RunAndLearnRanking.createWeeklyRanking(otherUser, currentSeason, higherScore,
                                                LocalDateTime.now()));

                // when
                SessionRankingDto result = runAndLearnQueryService.getExpectedRanking(myScore, RankingType.WEEKLY);

                // then
                assertThat(result.score()).isEqualTo(myScore);
                assertThat(result.rank()).isEqualTo(expectedRank);
        }

}
