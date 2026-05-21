package com.gyu.engdu.domain.engdu.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.gyu.engdu.domain.engdu.domain.enums.Category;
import com.gyu.engdu.domain.engdu.exception.QuestionAlreadySolvedException;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class QuestionTest {

  @DisplayName("문제 생성 시 기본 문제 해결 상태는 false 이다.")
  @Test
  void create() {
    // given
    Part part = new Part();
    byte answer = 1;
    // when
    Question question = Question.of(answer, "질문 내용", Category.COMPREHENSION, part);

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

  @DisplayName("shuffleChoices를 호출 시 전달받은 순서대로 선택지들의 seq가 갱신되며 정답 번호도 그에 맞게 갱신된다.")
  @Test
  void shuffleChoices() {
    // given
    byte correctAnswerSeq = 1;
    Part part = Part.builder().build();
    Question question = Question.of(correctAnswerSeq, "질문", Category.COMPREHENSION, part);
    Choice correctChoice = Choice.of("정답 내용", "정답 해설", correctAnswerSeq, question);
    Choice wrongChoice1 = Choice.of("오답 내용1", "오답 해설1", (byte) 2, question);
    Choice wrongChoice2 = Choice.of("오답 내용2", "오답 해설2", (byte) 3, question);
    Choice wrongChoice3 = Choice.of("오답 내용3", "오답 해설3", (byte) 4, question);

    List<Byte> randomSeqs = List.of((byte) 4, (byte) 1, (byte) 2, (byte) 3);

    // when
    question.shuffleChoices(randomSeqs);

    // then
    assertThat(correctChoice.getSeq()).isEqualTo((byte) 4);
    assertThat(wrongChoice1.getSeq()).isEqualTo((byte) 1);
    assertThat(wrongChoice2.getSeq()).isEqualTo((byte) 2);
    assertThat(wrongChoice3.getSeq()).isEqualTo((byte) 3);
    assertThat(question.getAnswer()).isEqualTo((byte) 4);
  }

}