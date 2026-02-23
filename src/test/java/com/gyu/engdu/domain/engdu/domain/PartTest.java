package com.gyu.engdu.domain.engdu.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import com.gyu.engdu.domain.engdu.domain.enums.PartType;
import com.gyu.engdu.domain.engdu.exception.QuestionForbiddenAccessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PartTest {

    @DisplayName("정답을 제출하면 true를 반환한다.")
    @Test
    void solveQuestion_correct() {
        // given
        Part part = createPart(PartType.INITIAL);
        byte userAnswer = 1;
        Long questionId = 1L;

        Question question = mock(Question.class);
        given(question.getId()).willReturn(questionId);
        given(question.solve(userAnswer)).willReturn(true);
        given(question.isCorrected()).willReturn(true); // checkAndMarkAllSolved 호출 시 사용

        part.getQuestions().add(question);

        // when
        boolean result = part.solveQuestion(questionId, userAnswer);

        // then
        assertThat(result).isTrue();
    }

    @DisplayName("오답을 제출하면 false를 반환한다.")
    @Test
    void solveQuestion_wrong() {
        // given
        Part part = createPart(PartType.INITIAL);
        byte wrongAnswer = 2;
        Long questionId = 1L;

        Question question = mock(Question.class);
        given(question.getId()).willReturn(questionId);
        given(question.solve(wrongAnswer)).willReturn(false);

        part.getQuestions().add(question);

        // when
        boolean result = part.solveQuestion(questionId, wrongAnswer);

        // then
        assertThat(result).isFalse();
        assertThat(part.isAllSolved()).isFalse();
    }

    @DisplayName("파트에 존재하지 않는 문제를 제출하면 예외가 발생한다.")
    @Test
    void solveQuestion_questionNotInPart() {
        // given
        Engdu engdu = mock(Engdu.class);
        given(engdu.getParts()).willReturn(new java.util.ArrayList<>());
        Part part = Part.of(PartType.INITIAL, engdu);

        Long existQuestionId = 1L;
        Long nonExistQuestionId = 99L;
        byte userAnswer = 1;

        Question question = mock(Question.class);
        given(question.getId()).willReturn(existQuestionId);

        part.getQuestions().add(question);

        // when & then
        assertThatThrownBy(() -> part.solveQuestion(nonExistQuestionId, userAnswer))
                .isInstanceOf(QuestionForbiddenAccessException.class);
    }

    @DisplayName("파트 내 모든 문제를 정답으로 제출하면 isAllSolved가 true가 된다.")
    @Test
    void solveAllQuestions_isAllSolvedTrue() {
        // given
        Part part = createPart(PartType.INITIAL);
        byte userAnswer = 1;

        Question question1 = mock(Question.class);
        Question question2 = mock(Question.class);
        given(question1.getId()).willReturn(1L);
        given(question2.getId()).willReturn(2L);
        given(question1.solve(userAnswer)).willReturn(true);
        given(question2.solve(userAnswer)).willReturn(true);

        // 두 문제 모두 풀린 후 checkAndMarkAllSolved 호출 시 allMatch(isCorrected)에서 사용
        given(question1.isCorrected()).willReturn(true);
        given(question2.isCorrected()).willReturn(true);

        part.getQuestions().add(question1);
        part.getQuestions().add(question2);

        // when
        part.solveQuestion(1L, userAnswer);
        part.solveQuestion(2L, userAnswer);

        // then
        assertThat(part.isAllSolved()).isTrue();
    }

    @DisplayName("파트 내 일부 문제만 풀면 isAllSolved가 false로 유지된다.")
    @Test
    void solvePartialQuestions_isAllSolvedFalse() {
        // given
        Part part = createPart(PartType.INITIAL);
        byte userAnswer = 1;

        Question question1 = mock(Question.class);
        Question question2 = mock(Question.class);
        given(question1.getId()).willReturn(1L);
        given(question1.solve(userAnswer)).willReturn(true);

        // question1만 풀린 후 checkAndMarkAllSolved → allMatch(isCorrected)
        given(question1.isCorrected()).willReturn(true);
        given(question2.isCorrected()).willReturn(false); // question2 미해결

        part.getQuestions().add(question1);
        part.getQuestions().add(question2);

        // question1만 제출
        // when
        part.solveQuestion(question1.getId(), userAnswer);

        // then
        assertThat(part.isAllSolved()).isFalse();
    }

    private Part createPart(PartType partType) {
        return Part.builder()
                .partType(partType)
                .build();
    }
}
