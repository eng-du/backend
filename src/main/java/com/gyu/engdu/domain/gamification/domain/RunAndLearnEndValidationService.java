package com.gyu.engdu.domain.gamification.domain;

import com.gyu.engdu.domain.gamification.application.dto.request.SubmittedAnswer;
import com.gyu.engdu.domain.gamification.exception.RunAndLearnAllCorrectException;
import com.gyu.engdu.domain.gamification.exception.RunAndLearnInvalidEndException;
import com.gyu.engdu.domain.gamification.exception.RunAndLearnScoreMismatchException;
import com.gyu.engdu.domain.gamification.exception.RunAndLearnSequenceMismatchException;
import com.gyu.engdu.domain.gamification.exception.RunAndLearnWrongAnswerBeforeEndException;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class RunAndLearnEndValidationService {

    private static final int SCORE_PER_QUESTION = 10;

    /**
     * 사용자가 세션에서 정해진 순서의 문제를 풀었는지 검증.
     * 사용자가 푼 문제들이 올바른 답인지 검증. 단, 마지막 문제는 오답만 가능
     * 사용자가 클라이언트에서 계산한 점수와 서버에서 계산한 점수가 일치하는지 검증
     **/
    public void validate(
            RunAndLearnSession session,
            int totalQuestions,
            List<SubmittedAnswer> submittedAnswers,
            List<RunAndLearnQuestion> expectedQuestions,
            int clientTotalScore) {

        validateQuestionSequence(submittedAnswers, expectedQuestions);

        validateAnswers(submittedAnswers, expectedQuestions, totalQuestions);

        int correctCount = submittedAnswers.size() - 1;
        validateTotalScore(clientTotalScore, correctCount);
    }

    // 사용자가 세션에서 정상적으로 섞은 순서대로 문제를 풀었는지 검증
    private void validateQuestionSequence(List<SubmittedAnswer> submittedAnswers,
            List<RunAndLearnQuestion> expectedQuestions) {

        for (int i = 0; i < submittedAnswers.size(); i++) {
            SubmittedAnswer submission = submittedAnswers.get(i);
            Long expectedQuestionId = expectedQuestions.get(i).getId();

            if (!expectedQuestionId.equals(submission.questionId())) {
                throw new RunAndLearnSequenceMismatchException();
            }
        }
    }

    // 사용자가 푼 답안들이 올바른 답인지 검증. 단, 마지막 문제는 오답만 가능
    private void validateAnswers(List<SubmittedAnswer> submittedAnswers,
            List<RunAndLearnQuestion> expectedQuestions,
            int totalQuestions) {
        int submitCount = submittedAnswers.size();

        for (int i = 0; i < submitCount - 1; i++) {
            SubmittedAnswer submission = submittedAnswers.get(i);
            RunAndLearnQuestion question = expectedQuestions.get(i);

            boolean isCorrect = question.getAnswer().equals(submission.userAnswer());

            if (!isCorrect) {
                throw new RunAndLearnWrongAnswerBeforeEndException();
            }
        }

        // 마지막 문제 검증
        SubmittedAnswer lastSubmission = submittedAnswers.get(submitCount - 1);
        RunAndLearnQuestion lastQuestion = expectedQuestions.get(submitCount - 1);
        boolean isLastCorrect = lastQuestion.getAnswer().equals(lastSubmission.userAnswer());

        // 마지막 문제가 맞다면 정상적인 게임 종료 방법이 아님
        if (isLastCorrect) {
            if (submitCount == totalQuestions) {
                throw new RunAndLearnAllCorrectException();
            }
            throw new RunAndLearnInvalidEndException();
        }
    }

    // 클라이언트와 서버의 점수가 일치하는지 검증
    private void validateTotalScore(int clientTotalScore, int correctCount) {
        long serverScore = (long) correctCount * SCORE_PER_QUESTION;

        if (clientTotalScore != serverScore) {
            throw new RunAndLearnScoreMismatchException();
        }
    }

}
