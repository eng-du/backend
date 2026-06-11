package com.gyu.engdu.domain.gamification.application;

import com.gyu.engdu.domain.gamification.application.cache.RunAndLearnCacheService;
import com.gyu.engdu.domain.gamification.application.dto.response.LeaderboardEntryDto;
import com.gyu.engdu.domain.gamification.application.dto.response.RankingInfoDto;
import com.gyu.engdu.domain.gamification.application.dto.response.RunAndLearnQuestionResponse;
import com.gyu.engdu.domain.gamification.application.dto.response.SessionRankingDto;
import com.gyu.engdu.domain.gamification.domain.RunAndLearnQuestion;
import com.gyu.engdu.domain.gamification.domain.RunAndLearnQuestionRepository;
import com.gyu.engdu.domain.gamification.domain.RunAndLearnRanking;
import com.gyu.engdu.domain.gamification.domain.RunAndLearnRankingRepository;
import com.gyu.engdu.domain.gamification.domain.RunAndLearnSeasonCalculator;
import com.gyu.engdu.domain.gamification.domain.RunAndLearnSession;
import com.gyu.engdu.domain.gamification.domain.RunAndLearnSessionRepository;
import com.gyu.engdu.domain.gamification.domain.enums.RankingType;
import com.gyu.engdu.domain.gamification.exception.RunAndLearnQuestionExhaustedException;
import com.gyu.engdu.domain.gamification.exception.RunAndLearnQuestionNotFoundException;
import com.gyu.engdu.domain.gamification.exception.RunAndLearnSessionNotFoundException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RunAndLearnQueryService {

    private final RunAndLearnSessionRepository runAndLearnSessionRepository;
    private final RunAndLearnQuestionRepository runAndLearnQuestionRepository;
    private final RunAndLearnRankingRepository runAndLearnRankingRepository;
    private final RunAndLearnCacheService runAndLearnCacheService;

    public RunAndLearnSession findExistingSession(Long sessionId) {
        return runAndLearnSessionRepository.findById(sessionId)
                .orElseThrow(() -> new RunAndLearnSessionNotFoundException(sessionId));
    }

    public List<RunAndLearnQuestionResponse> getQuestions(
            Long userId,
            Long sessionId,
            int startIndex,
            int count
    ) {
        // 세션 조회 후 사용자 검증
        RunAndLearnSession session = findExistingSession(sessionId);
        session.validateOwner(userId);

        // 캐시를 통해 문제 순서 가져오기
        List<Long> sessionQuestionIds = runAndLearnCacheService.getSessionQuestionIds(sessionId,
                session.getSeed());
        int totalQuestions = sessionQuestionIds.size();

        if (totalQuestions == 0) {
            throw new RunAndLearnQuestionNotFoundException();
        }

        if (startIndex >= totalQuestions) {
            throw new RunAndLearnQuestionExhaustedException();
        }

        int endIndex = Math.min(startIndex + count, totalQuestions);
        List<Long> questionIds = sessionQuestionIds.subList(startIndex, endIndex);

        List<RunAndLearnQuestion> questions = getQuestionAndRestoreOrder(questionIds);

        return questions.stream()
                .map(RunAndLearnQuestionResponse::from)
                .toList();
    }

    /**
     * questionId 순서에 맞게 가져오기. findAllById는 순서를 보장하지 않기 때문에 Map을 사용해서 순서 재배치
     */
    public List<RunAndLearnQuestion> getQuestionAndRestoreOrder(List<Long> questionIds) {
        List<RunAndLearnQuestion> questions = runAndLearnQuestionRepository.findAllById(
                questionIds);
        Map<Long, RunAndLearnQuestion> questionMap = questions.stream()
                .collect(Collectors.toMap(RunAndLearnQuestion::getId, Function.identity()));

        return questionIds.stream()
                .filter(questionMap::containsKey)
                .map(questionMap::get)
                .toList();
    }

    public List<LeaderboardEntryDto> getTopKRanking(RankingType rankingType, int k) {
        // 주간 랭킹 조회라면 Calculator로 시즌 게산, 역대 랭킹이라면 season을 0으로 고정
        int season = rankingType == RankingType.WEEKLY
                ? RunAndLearnSeasonCalculator.calculateSeason(LocalDateTime.now())
                : 0;

        List<RankingInfoDto> dtos = runAndLearnRankingRepository
                .findByRankingTypeAndSeasonOrderByBestScoreDescAchievedAtAsc(rankingType,
                        season, org.springframework.data.domain.PageRequest.of(0, k))
                .stream()
                .map(RankingInfoDto::from)
                .toList();

        List<LeaderboardEntryDto> leaderboard = new ArrayList<>();

        // 동점자는 같은 순위, 점수가 낮아지면 다음 순위로 갱신
        int displayRank = 1;

        for (int i = 0; i < dtos.size(); i++) {
            RankingInfoDto dto = dtos.get(i);

            if (i > 0 && dto.bestScore() < dtos.get(i - 1).bestScore()) {
                displayRank = i + 1;
            }

            leaderboard.add(LeaderboardEntryDto.of(displayRank, dto));
        }

        return leaderboard;
    }

    public LeaderboardEntryDto getMyRanking(Long userId, RankingType rankingType) {
        int season = rankingType == RankingType.WEEKLY
                ? RunAndLearnSeasonCalculator.calculateSeason(LocalDateTime.now())
                : 0;

        RunAndLearnRanking myRanking = runAndLearnRankingRepository
                .findByUserIdAndRankingTypeAndSeason(
                        userId,
                        rankingType,
                        season)
                .orElse(null);

        if (myRanking == null) {
            return null;
        }

        int rank = runAndLearnRankingRepository.countByRankingTypeAndSeasonAndBestScoreGreaterThan(
                rankingType, season, myRanking.getBestScore()) + 1;

        return LeaderboardEntryDto.of(rank, RankingInfoDto.from(myRanking));
    }

    public SessionRankingDto getExpectedRanking(int score, RankingType rankingType) {
        LocalDateTime referenceTime = LocalDateTime.now();
        int season =
                rankingType == RankingType.WEEKLY ? RunAndLearnSeasonCalculator.calculateSeason(
                        referenceTime) : 0;

        int rank = runAndLearnRankingRepository.countByRankingTypeAndSeasonAndBestScoreGreaterThan(
                rankingType, season, score) + 1;

        return SessionRankingDto.of(score, rank);
    }

}
