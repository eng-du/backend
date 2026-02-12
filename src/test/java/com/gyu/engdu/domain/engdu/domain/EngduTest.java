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
    Long question1Id = 1L;
    byte userAnswer = 1;

    Question question1 = mock(Question.class);
    Question question2 = mock(Question.class);
    Question question3 = mock(Question.class);

    given(question1.getId()).willReturn(question1Id);
    given(question1.solve(userAnswer)).willReturn(true);

    engdu.getQuestions().add(question1);
    engdu.getQuestions().add(question2);
    engdu.getQuestions().add(question3);

    // when
    boolean result = engdu.submission(question1Id, userAnswer);

    // then
    assertThat(engdu.isAllSolved()).isFalse();
    assertThat(engdu.getSolvedCount()).isEqualTo(1);
    assertThat(result).isTrue();
  }

  @DisplayName("오답을 제출하면 solvedCount는 유지되고 false를 반환한다.")
  @Test
  void submission2() {
    // given
    Engdu engdu = createEngdu(1L, "test topic");
    Long question1Id = 1L;
    byte userAnswer = 2;

    Question question1 = mock(Question.class);
    Question question2 = mock(Question.class);
    Question question3 = mock(Question.class);

    given(question1.getId()).willReturn(question1Id);
    given(question1.solve(userAnswer)).willReturn(false);

    engdu.getQuestions().add(question1);
    engdu.getQuestions().add(question2);
    engdu.getQuestions().add(question3);

    // when
    boolean result = engdu.submission(question1Id, userAnswer);

    // then
    assertThat(engdu.isAllSolved()).isFalse();
    assertThat(engdu.getSolvedCount()).isZero();
    assertThat(result).isFalse();
  }

  @DisplayName("모든 문제의 정답을 맞추면 모든 문제 해결 상태를 true로 변경하고 true를 반환한다.")
  @Test
  void submission3() {
    // given
    Engdu engdu = createEngdu(1L, "test topic");
    Long question1Id = 1L;
    Long question2Id = 2L;
    Long question3Id = 3L;
    byte userAnswer = 1;

    Question question1 = mock(Question.class);
    Question question2 = mock(Question.class);
    Question question3 = mock(Question.class);

    given(question1.getId()).willReturn(question1Id);
    given(question1.solve(userAnswer)).willReturn(true);
    given(question2.getId()).willReturn(question2Id);
    given(question2.solve(userAnswer)).willReturn(true);
    given(question3.getId()).willReturn(question3Id);
    given(question3.solve(userAnswer)).willReturn(true);

    engdu.getQuestions().add(question1);
    engdu.getQuestions().add(question2);
    engdu.getQuestions().add(question3);

    engdu.submission(question1Id, userAnswer);
    engdu.submission(question2Id, userAnswer);

    // when
    boolean result = engdu.submission(question3Id, userAnswer);

    // then
    assertThat(engdu.isAllSolved()).isTrue();
    assertThat(engdu.getSolvedCount()).isEqualTo(engdu.getQuestions().size());
    assertThat(result).isTrue();
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