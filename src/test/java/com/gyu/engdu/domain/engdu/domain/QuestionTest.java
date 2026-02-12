package com.gyu.engdu.domain.engdu.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.gyu.engdu.domain.engdu.domain.enums.Category;
import com.gyu.engdu.domain.engdu.exception.QuestionAlreadySolvedException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class QuestionTest {

  @DisplayName("문제 생성 시 기본 문제 해결 상태는 false 이다.")
  @Test
  void create() {
    // given
    Engdu engdu = new Engdu();
    byte answer = 1;
    // when
    Question question = Question.of(answer, "질문 내용", Category.COMPREHENSION, engdu);

    // then
    assertThat(question.isCorrected()).isFalse();
  }

  @DisplayName("정답을 제출하면 문제 해결 상태를 true로 설정하고 true를 반환한다.")
  @Test
  void solve() {
    // given
    byte questionAnswer = 1;
    byte userAnswer = 1;
    boolean isCorrected = false;
    Question question = createQuestion(questionAnswer, isCorrected);

    // when
    boolean result = question.solve(userAnswer);

    // then
    assertThat(question.isCorrected()).isTrue();
    assertThat(result).isTrue();
  }

  @DisplayName("오답을 제출하면 false를 반환한다.")
  @Test
  void solve2() {
    // given
    byte questionAnswer = 1;
    byte userAnswer = 2;
    boolean isCorrected = false;
    Question question = createQuestion(questionAnswer, isCorrected);

    // when
    boolean result = question.solve(userAnswer);

    // then
    assertThat(question.isCorrected()).isFalse();
    assertThat(result).isFalse();
  }

  @DisplayName("이미 해결한 문제를 다시 풀이할 경우 예외를 반환한다.")
  @Test
  void solve3() {
    // given
    byte questionAnswer = 1;
    byte userAnswer = 1;
    boolean isCorrected = true;
    Question question = createQuestion(questionAnswer, isCorrected);

    // when & then
    assertThatThrownBy(() -> question.solve(userAnswer))
        .isInstanceOf(QuestionAlreadySolvedException.class);

  }

  private Question createQuestion(byte answer, boolean isCorrected) {
    return Question.builder()
        .answer(answer)
        .isCorrected(isCorrected)
        .build();
  }

}