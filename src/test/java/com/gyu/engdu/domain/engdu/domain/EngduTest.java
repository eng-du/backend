package com.gyu.engdu.domain.engdu.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import com.gyu.engdu.domain.engdu.domain.enums.LikeStatus;
import com.gyu.engdu.domain.engdu.exception.EngduForbiddenAccessException;
import com.gyu.engdu.domain.engdu.exception.EngduLikeAlreadyProcessedException;
import com.gyu.engdu.domain.engdu.exception.EngduTitleTooLongException;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class EngduTest {

  @DisplayName("잉듀 생성 시 기본 잉듀 해결 상태는 false이다.")
  @Test
  void create() {
    // given
    Long userId = 1L;
    String topic = "테스트 잉듀 주제";
    String level = "BEGINNER";

    // when
    Engdu engdu = Engdu.of(userId, level, topic);

    // then
    assertThat(engdu.isAllSolved()).isFalse();
  }

  @DisplayName("잉듀 생성 시 기본 잉듀 좋아요 상태는 NONE 이다.")
  @Test
  void create2() {
    // given
    Long userId = 1L;
    String topic = "테스트 잉듀 주제";
    String level = "BEGINNER";

    // when
    Engdu engdu = Engdu.of(userId, level, topic);

    // then
    assertThat(engdu.getLikeStatus()).isEqualTo(LikeStatus.NONE);
  }

  @DisplayName("잉듀의 좋아요 상태를 변경할 수 있다.")
  @Test
  void like() {
    // given
    Long userId = 1L;
    Engdu noneEngdu = createEngdu(userId, "test topic", LikeStatus.NONE);

    // when
    noneEngdu.changeLikeStatus(LikeStatus.LIKE);

    // then
    assertThat(noneEngdu.getLikeStatus()).isEqualTo(LikeStatus.LIKE);
  }

  @DisplayName("이미 좋아요 상태가 결정된 잉듀는 상태를 변경할 수 없다.")
  @Test
  void cannotChangeLikeStatusOnceSet() {
    // given
    Long userId = 1L;
    Engdu likedEngdu = createEngdu(userId, "test topic", LikeStatus.LIKE);
    Engdu dislikedEngdu = createEngdu(userId, "test topic", LikeStatus.DISLIKE);

    // when & then
    assertThatThrownBy(() -> likedEngdu.changeLikeStatus(LikeStatus.NONE))
        .isInstanceOf(EngduLikeAlreadyProcessedException.class);

    assertThatThrownBy(() -> dislikedEngdu.changeLikeStatus(LikeStatus.LIKE))
        .isInstanceOf(EngduLikeAlreadyProcessedException.class);
  }

  @DisplayName("사용자가 잉듀의 소유자가 아니라면, validateOwner 메서드는 예외를 발생시킨다.")
  @Test
  void validateNonOwnerId() {
    // given
    Long engduOwnerId = 1L;
    Long nonOwnerId = 2L;
    Engdu engdu = createEngdu(engduOwnerId, "test topic");

    // when & then
    assertThatThrownBy(() -> engdu.validateOwner(nonOwnerId))
        .isInstanceOf(EngduForbiddenAccessException.class);
  }

  @DisplayName("사용자가 잉듀의 소유자라면, validateOwner 메서드를 통과한다.")
  @Test
  void validateOwner() {
    // given
    Long engduOwnerId = 1L;
    Engdu engdu = createEngdu(engduOwnerId, "test topic");

    // when & then
    assertThatCode(() -> engdu.validateOwner(engduOwnerId))
        .doesNotThrowAnyException();
  }

  @DisplayName("정답을 제출하면 solvedCount를 1증가시키고 true를 반환한다.")
  @Test
  void submission() {
    // given
    Engdu engdu = createEngdu(1L, "test topic");
    byte userAnswer = 1;
    Long questionId = 1L;

    Question question1 = mock(Question.class);
    Question question2 = mock(Question.class);

    given(question1.getId()).willReturn(questionId);

    Part part = mock(Part.class);
    given(part.getQuestions()).willReturn(List.of(question1, question2));
    given(part.solveQuestion(questionId, userAnswer)).willReturn(true);
    given(part.isAllSolved()).willReturn(false); // question2가 남아있어 Part 미완성

    engdu.getParts().add(part);

    // when
    boolean result = engdu.submission(questionId, userAnswer);

    // then
    assertThat(result).isTrue();
    assertThat(engdu.getSolvedCount()).isEqualTo(1);
    assertThat(engdu.isAllSolved()).isFalse();
  }

  @DisplayName("오답을 제출하면 solvedCount는 유지되고 false를 반환한다.")
  @Test
  void submission2() {
    // given
    Engdu engdu = createEngdu(1L, "test topic");
    byte wrongUserAnswer = 2;
    Long questionId = 1L;

    Question question1 = mock(Question.class);
    given(question1.getId()).willReturn(questionId);

    Part part = mock(Part.class);
    given(part.getQuestions()).willReturn(List.of(question1));
    given(part.solveQuestion(questionId, wrongUserAnswer)).willReturn(false);

    engdu.getParts().add(part);

    // when
    boolean result = engdu.submission(questionId, wrongUserAnswer);

    // then
    assertThat(result).isFalse();
    assertThat(engdu.getSolvedCount()).isZero();
    assertThat(engdu.isAllSolved()).isFalse();
  }

  // Part1, Part2 두 파트를 모두 풀어야 Engdu.isAllSolved = true
  @DisplayName("모든 파트의 문제를 다 풀면 Engdu의 모든 문제 해결 상태를 true로 변경한다.")
  @Test
  void submission3() {
    // given
    Engdu engdu = createEngdu(1L, "test topic");
    byte userAnswer = 1;

    // Part1: question1(id=1), question2(id=2)
    Question q1 = mock(Question.class);
    Question q2 = mock(Question.class);
    given(q1.getId()).willReturn(1L);
    given(q2.getId()).willReturn(2L);

    Part part1 = mock(Part.class);
    given(part1.getQuestions()).willReturn(List.of(q1, q2));
    given(part1.solveQuestion(1L, userAnswer)).willReturn(true);
    given(part1.solveQuestion(2L, userAnswer)).willReturn(true);

    // submission(q1) 후: false, submission(q2) 후: true (이후 계속 true)
    given(part1.isAllSolved()).willReturn(false, true, true, true);

    // Part2: question3(id=3), question4(id=4)
    Question q3 = mock(Question.class);
    Question q4 = mock(Question.class);
    given(q3.getId()).willReturn(3L);
    given(q4.getId()).willReturn(4L);

    Part part2 = mock(Part.class);
    given(part2.getQuestions()).willReturn(List.of(q3, q4));
    given(part2.solveQuestion(3L, userAnswer)).willReturn(true);
    given(part2.solveQuestion(4L, userAnswer)).willReturn(true);
    // submission(q3) 후: false, submission(q4) 후: true
    given(part2.isAllSolved()).willReturn(false, true);

    engdu.getParts().add(part1);
    engdu.getParts().add(part2);

    // Part1 문제 모두 풀기
    engdu.submission(1L, userAnswer);
    engdu.submission(2L, userAnswer);
    assertThat(engdu.isAllSolved()).isFalse(); // Part2 아직 미해결

    // Part2 문제 모두 풀기
    engdu.submission(3L, userAnswer);

    // 마지막 문제 제출
    // when
    boolean result = engdu.submission(4L, userAnswer);

    // then
    assertThat(result).isTrue();
    assertThat(engdu.isAllSolved()).isTrue();
    assertThat(engdu.getSolvedCount()).isEqualTo(4);
  }

  @DisplayName("잉듀의 제목을 변경할 때 150자를 초과하면 예외가 발생한다.")
  @Test
  void changeTitle() {
    // given
    Engdu engdu = createEngdu(1L, "test topic");
    String longTitle = "a".repeat(151);

    // when & then
    assertThatThrownBy(() -> engdu.changeTitle(longTitle))
        .isInstanceOf(EngduTitleTooLongException.class);
  }

  private Engdu createEngdu(Long userId, String topic) {
    return Engdu.builder()
        .userId(userId)
        .topic(topic)
        .level("BEGINNER")
        .build();
  }

  private Engdu createEngdu(Long userId, String topic, LikeStatus likeStatus) {
    return Engdu.builder()
        .userId(userId)
        .topic(topic)
        .level("BEGINNER")
        .likeStatus(likeStatus)
        .build();
  }

}