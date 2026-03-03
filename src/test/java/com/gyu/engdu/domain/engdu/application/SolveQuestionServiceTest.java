package com.gyu.engdu.domain.engdu.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.gyu.engdu.IntegrationTestSupport;
import com.gyu.engdu.domain.engdu.domain.Engdu;
import com.gyu.engdu.domain.engdu.domain.EngduRepository;
import com.gyu.engdu.domain.engdu.domain.Part;
import com.gyu.engdu.domain.engdu.domain.Question;
import com.gyu.engdu.domain.engdu.domain.enums.PartType;
import com.gyu.engdu.domain.engdu.exception.QuestionAlreadySolvedException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class SolveQuestionServiceTest extends IntegrationTestSupport {

  @Autowired
  private SolveQuestionService solveQuestionService;

  @Autowired
  private EngduRepository engduRepository;

  @DisplayName("정답 제출 시 solvedCount를 1증가시키고 true를 반환한다.")
  @Test
  void solve() {
    // given
    Long userId = 1L;
    byte userAnswer = 1;
    byte questionAnswer = 1;
    Engdu engdu = createEngdu(userId);
    Part part = Part.of(PartType.INITIAL, engdu);
    Question question = createQuestion(questionAnswer, false);
    question.setPart(part);

    engduRepository.save(engdu);

    // when
    boolean result = solveQuestionService.solve(userId, engdu.getId(), question.getId(),
        userAnswer);

    // then
    assertThat(result).isTrue();
    assertThat(engdu.getSolvedCount()).isEqualTo(1);
  }

  @DisplayName("정답 제출 시 모든 잉듀의 문제를 해결했다면 모든 문제 해결 상태를 true로 변경한다.")
  @Test
  void solve2() {
    // given
    Long userId = 1L;
    byte userAnswer = 1;
    byte questionAnswer = 1;
    Engdu engdu = createEngdu(userId);
    Part part = Part.of(PartType.INITIAL, engdu);
    Question question1 = createQuestion(questionAnswer, false);
    Question question2 = createQuestion(questionAnswer, false);
    Question question3 = createQuestion(questionAnswer, false);

    question1.setPart(part);
    question2.setPart(part);
    question3.setPart(part);

    engduRepository.save(engdu);
    solveQuestionService.solve(userId, engdu.getId(), question1.getId(), userAnswer);
    solveQuestionService.solve(userId, engdu.getId(), question2.getId(), userAnswer);

    // when
    boolean result = solveQuestionService.solve(userId, engdu.getId(), question3.getId(),
        userAnswer);

    // then
    assertThat(result).isTrue();
    assertThat(part.isAllSolved()).isTrue();
    assertThat(engdu.isAllSolved()).isTrue();
    assertThat(engdu.getSolvedCount()).isEqualTo(3);
  }

  @DisplayName("오답 제출 시 solvedCount는 증가하지 않고 false를 반환한다.")
  @Test
  void solve3() {
    // given
    Long userId = 1L;
    byte userAnswer = 1;
    byte questionAnswer = 2;
    Engdu engdu = createEngdu(userId);
    Part part = Part.of(PartType.INITIAL, engdu);
    Question question = createQuestion(questionAnswer, false);
    question.setPart(part);

    engduRepository.save(engdu);

    // when
    boolean result = solveQuestionService.solve(userId, engdu.getId(), question.getId(),
        userAnswer);

    // then
    assertThat(result).isFalse();
    assertThat(engdu.getSolvedCount()).isZero();
  }

  @DisplayName("이미 푼 문제를 다시 풀면 예외가 발생한다.")
  @Test
  void solve4() {
    // given
    Long userId = 1L;
    byte userAnswer = 1;
    byte questionAnswer = 1;
    Engdu engdu = createEngdu(userId);
    Part part = Part.of(PartType.INITIAL, engdu);
    Question question = createQuestion(questionAnswer, true);
    question.setPart(part);

    engduRepository.save(engdu);

    // when & then
    assertThatThrownBy(() -> solveQuestionService.solve(userId, engdu.getId(), question.getId(),
        userAnswer))
        .isInstanceOf(QuestionAlreadySolvedException.class);
  }

  private Engdu createEngdu(Long userId) {
    Engdu engdu = Engdu.builder()
        .userId(userId)
        .topic("test topic")
        .build();
    return engdu;
  }

  private Question createQuestion(byte answer, boolean isCorrected) {
    return Question.builder().answer(answer).isCorrected(isCorrected).build();
  }
}