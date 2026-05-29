spackage com.gyu.engdu.domain.gamification.domain;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.gyu.engdu.domain.gamification.application.dto.request.SubmittedAnswer;
import com.gyu.engdu.domain.gamification.exception.RunAndLearnAllCorrectException;
import com.gyu.engdu.domain.gamification.exception.RunAndLearnInvalidEndException;
import com.gyu.engdu.domain.gamification.exception.RunAndLearnScoreMismatchException;
import com.gyu.engdu.domain.gamification.exception.RunAndLearnSequenceMismatchException;
import com.gyu.engdu.domain.gamification.exception.RunAndLearnWrongAnswerBeforeEndException;
import com.gyu.engdu.domain.user.domain.Role;
import com.gyu.engdu.domain.user.domain.User;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

class RunAndLearnEndValidationServiceTest {

    private final Long MAX_QUESTION_ID = 5L;
    private RunAndLearnEndValidationService validationService;
    private RunAndLearnSession session;

    @BeforeEach
    void setUp() {
        validationService = new RunAndLearnEndValidationService();
        User user = User.builder().email("test@test.com").role(Role.ROLE_USER).sub("sub123")
                .name("user").build();
        ReflectionTestUtils.setField(user, "id", 1L);
        session = RunAndLearnSession.of(user, 12345);
    }

    @Test
    @DisplayName("모든 검증을 통과하면 예외가 발생하지 않는다.")
    void validateSuccess() {
        // given
        List<RunAndLearnQuestion> questions = List.of(
                createRunAndLearnQuestion(1L, 1),
                createRunAndLearnQuestion(2L, 2),
                createRunAndLearnQuestion(3L, 3));

        List<SubmittedAnswer> submissions = List.of(
                new SubmittedAnswer(1L, 1),
                new SubmittedAnswer(2L, 2),
                new SubmittedAnswer(3L, 999));

        int clientTotalScore = 20;

        // when & then
        assertThatCode(
                () -> validationService.validate(session, MAX_QUESTION_ID, submissions, questions,
                        clientTotalScore))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("제출된 답변의 문제 ID 순서가 예상과 다르면 예외가 발생한다")
    void validateWrongSequence() {
        // given
        List<RunAndLearnQuestion> questions = List.of(
                createRunAndLearnQuestion(1L, 1));

        List<SubmittedAnswer> submissions = List.of(
                new SubmittedAnswer(2L, 1));

        int clientTotalScore = 0;

        // when & then
        assertThatThrownBy(
                () -> validationService.validate(session, MAX_QUESTION_ID, submissions, questions,
                        clientTotalScore))
                .isInstanceOf(RunAndLearnSequenceMismatchException.class);
    }

    @Test
    @DisplayName("마지막 문제가 아닌데 오답이 있으면 예외가 발생한다")
    void validateWrongAnswerBeforeEnd() {
        // given
        List<RunAndLearnQuestion> questions = List.of(
                createRunAndLearnQuestion(1L, 1),
                createRunAndLearnQuestion(2L, 2));

        List<SubmittedAnswer> submissions = List.of(
                new SubmittedAnswer(1L, 999),
                new SubmittedAnswer(2L, 999));

        int clientTotalScore = 0;

        // when & then
        assertThatThrownBy(
                () -> validationService.validate(session, MAX_QUESTION_ID, submissions, questions,
                        clientTotalScore))
                .isInstanceOf(RunAndLearnWrongAnswerBeforeEndException.class);
    }

    @Test
    @DisplayName("모든 문제를 맞추면 예외가 발생한다")
    void validateAllCorrect() {
        // given
        List<RunAndLearnQuestion> questions = new ArrayList<>();
        List<SubmittedAnswer> submissions = new ArrayList<>();

        for (long i = 1; i <= MAX_QUESTION_ID; i++) {
            questions.add(createRunAndLearnQuestion(i, (int) i));
            submissions.add(new SubmittedAnswer(i, (int) i));
        }

        int allCorrectScore = 50;

        // when & then
        assertThatThrownBy(
                () -> validationService.validate(session, MAX_QUESTION_ID, submissions, questions,
                        allCorrectScore))
                .isInstanceOf(RunAndLearnAllCorrectException.class);
    }

    @Test
    @DisplayName("마지막 문제를 맞추고 끝나면 예외가 발생한다")
    void validateCorrectLastAnswerButNotAll() {
        // given
        List<RunAndLearnQuestion> questions = List.of(
                createRunAndLearnQuestion(1L, 1),
                createRunAndLearnQuestion(2L, 2),
                createRunAndLearnQuestion(3L, 3));

        List<SubmittedAnswer> submissions = List.of(
                new SubmittedAnswer(1L, 1),
                new SubmittedAnswer(2L, 2),
                new SubmittedAnswer(3L, 3));

        int clientTotalScore = 30;

        // when & then
        assertThatThrownBy(
                () -> validationService.validate(session, MAX_QUESTION_ID, submissions, questions,
                        clientTotalScore))
                .isInstanceOf(RunAndLearnInvalidEndException.class);
    }

    @Test
    @DisplayName("클라이언트 점수가 계산된 점수와 다르면 예외가 발생한다")
    void validateWrongScore() {
        // given
        List<RunAndLearnQuestion> questions = List.of(
                createRunAndLearnQuestion(1L, 1),
                createRunAndLearnQuestion(2L, 2));

        List<SubmittedAnswer> submissions = List.of(
                new SubmittedAnswer(1L, 1),
                new SubmittedAnswer(2L, 999));

        int wrongClientScore = 20;

        // when & then
        assertThatThrownBy(
                () -> validationService.validate(session, MAX_QUESTION_ID, submissions, questions,
                        wrongClientScore))
                .isInstanceOf(RunAndLearnScoreMismatchException.class);
    }

    private RunAndLearnQuestion createRunAndLearnQuestion(Long id, int answer) {
        RunAndLearnQuestion question = RunAndLearnQuestion.builder()
                .question("Q" + id)
                .answer(answer)
                .build();
        ReflectionTestUtils.setField(question, "id", id);
        return question;
    }
}
