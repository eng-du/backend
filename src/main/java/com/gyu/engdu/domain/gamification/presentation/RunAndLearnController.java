package com.gyu.engdu.domain.gamification.presentation;

import com.gyu.engdu.domain.gamification.application.EndRunAndLearnSessionService;
import com.gyu.engdu.domain.gamification.application.RunAndLearnQueryService;
import com.gyu.engdu.domain.gamification.application.RunAndLearnSessionFacade;
import com.gyu.engdu.domain.gamification.application.dto.request.EndRunAndLearnSessionRequest;
import com.gyu.engdu.domain.gamification.application.dto.response.CreateRunAndLearnSessionResponse;
import com.gyu.engdu.domain.gamification.application.dto.response.LeaderboardEntryDto;
import com.gyu.engdu.domain.gamification.application.dto.response.RunAndLearnQuestionResponse;
import com.gyu.engdu.domain.gamification.application.dto.response.SessionRankingDto;
import com.gyu.engdu.domain.gamification.application.dto.response.StartRunAndLearnSessionResponse;
import com.gyu.engdu.domain.gamification.domain.enums.RankingType;
import com.gyu.engdu.domain.gamification.presentation.dto.request.RunAndLearnLeaderboardRequest;
import com.gyu.engdu.domain.gamification.presentation.dto.request.RunAndLearnQuestionRequest;
import com.gyu.engdu.domain.gamification.presentation.dto.response.LeaderboardEntryResponse;
import com.gyu.engdu.domain.gamification.presentation.dto.response.SessionRankResponse;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/run-and-learn")
@RequiredArgsConstructor
public class RunAndLearnController {

    private final RunAndLearnSessionFacade runAndLearnSessionFacade;
    private final RunAndLearnQueryService runAndLearnQueryService;
    private final EndRunAndLearnSessionService endRunAndLearnSessionService;

    @PostMapping
    public ResponseEntity<StartRunAndLearnSessionResponse> startSession(
            @AuthenticationPrincipal(expression = "userId") Long userId) {
        StartRunAndLearnSessionResponse response = runAndLearnSessionFacade.createAndStart(userId, LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{sessionId}/question")
    public ResponseEntity<List<RunAndLearnQuestionResponse>> getQuestions(
            @PathVariable("sessionId") Long sessionId,
            @AuthenticationPrincipal(expression = "userId") Long userId,
            @Valid RunAndLearnQuestionRequest request
    ) {

        List<RunAndLearnQuestionResponse> response = runAndLearnQueryService.getQuestions(userId,
                sessionId, request.startIndex(), request.count());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{sessionId}/end")
    public ResponseEntity<Void> endSession(
            @PathVariable("sessionId") Long sessionId,
            @AuthenticationPrincipal(expression = "userId") Long userId,
            @Valid @RequestBody EndRunAndLearnSessionRequest request
    ) {

        endRunAndLearnSessionService.endSession(userId, sessionId, request, LocalDateTime.now());

        return ResponseEntity.ok().build();
    }

    @GetMapping("/leaderboard")
    public ResponseEntity<List<LeaderboardEntryResponse>> getLeaderboard(
            @Valid RunAndLearnLeaderboardRequest request
    ) {

        List<LeaderboardEntryDto> topKDto = runAndLearnQueryService.getTopKRanking(
                request.rankingType(),
                request.size());

        List<LeaderboardEntryResponse> leaderboard = topKDto.stream()
                .map(dto -> LeaderboardEntryResponse.of(dto.rank(), dto.rankingInfo()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(leaderboard);
    }

    @GetMapping("/leaderboard/me")
    public ResponseEntity<LeaderboardEntryResponse> getMyRanking(
            @RequestParam("rankingType") RankingType rankingType,
            @AuthenticationPrincipal(expression = "userId") Long userId
    ) {
        LeaderboardEntryDto myRankingDto = runAndLearnQueryService.getMyRanking(userId,
                rankingType);

        if (myRankingDto == null) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(
                LeaderboardEntryResponse.of(myRankingDto.rank(), myRankingDto.rankingInfo()));
    }

    @GetMapping("/leaderboard/expected")
    public ResponseEntity<SessionRankResponse> getExpectedRanking(
            @RequestParam("score") int score,
            @RequestParam("rankingType") RankingType rankingType
    ) {

        SessionRankingDto sessionRankDto = runAndLearnQueryService.getExpectedRanking(score,
                rankingType);
        return ResponseEntity.ok(SessionRankResponse.from(sessionRankDto));
    }

}
