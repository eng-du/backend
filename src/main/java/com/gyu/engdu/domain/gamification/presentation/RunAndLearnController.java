package com.gyu.engdu.domain.gamification.presentation;

import com.gyu.engdu.domain.gamification.application.CreateRunAndLearnSessionService;
import com.gyu.engdu.domain.gamification.application.EndRunAndLearnSessionService;
import com.gyu.engdu.domain.gamification.application.RunAndLearnQueryService;
import com.gyu.engdu.domain.gamification.application.StartRunAndLearnSessionService;
import com.gyu.engdu.domain.gamification.application.dto.request.EndRunAndLearnSessionRequest;
import com.gyu.engdu.domain.gamification.application.dto.response.CreateRunAndLearnSessionResponse;
import com.gyu.engdu.domain.gamification.application.dto.response.EndRunAndLearnSessionResponse;
import com.gyu.engdu.domain.gamification.application.dto.response.RunAndLearnQuestionResponse;
import com.gyu.engdu.domain.gamification.application.dto.response.StartRunAndLearnSessionResponse;
import com.gyu.engdu.domain.gamification.presentation.dto.request.RunAndLearnQuestionRequest;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/run-and-learn")
@RequiredArgsConstructor
public class RunAndLearnController {

    private final CreateRunAndLearnSessionService createRunAndLearnSessionService;
    private final StartRunAndLearnSessionService startRunAndLearnSessionService;
    private final RunAndLearnQueryService runAndLearnQueryService;
    private final EndRunAndLearnSessionService endRunAndLearnSessionService;

    @PostMapping
    public ResponseEntity<CreateRunAndLearnSessionResponse> createSession(
            @AuthenticationPrincipal(expression = "userId") Long userId) {
        int seed = ThreadLocalRandom.current().nextInt();
        Long sessionId = createRunAndLearnSessionService.create(userId, seed);

        return ResponseEntity.ok(CreateRunAndLearnSessionResponse.of(sessionId));
    }

    @PostMapping("/{sessionId}/start")
    public ResponseEntity<StartRunAndLearnSessionResponse> startSession(
            @PathVariable("sessionId") Long sessionId,
            @AuthenticationPrincipal(expression = "userId") Long userId) {
        StartRunAndLearnSessionResponse response = startRunAndLearnSessionService.start(userId,
                sessionId, LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{sessionId}/question")
    public ResponseEntity<List<RunAndLearnQuestionResponse>> getQuestions(
            @PathVariable("sessionId") Long sessionId,
            @AuthenticationPrincipal(expression = "userId") Long userId,
            @Valid RunAndLearnQuestionRequest request) {

        List<RunAndLearnQuestionResponse> response = runAndLearnQueryService.getQuestions(userId,
                sessionId, request.startIndex(), request.count());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{sessionId}/end")
    public ResponseEntity<EndRunAndLearnSessionResponse> endSession(@PathVariable("sessionId") Long sessionId,
            @AuthenticationPrincipal(expression = "userId") Long userId,
            @Valid @RequestBody EndRunAndLearnSessionRequest request) {

        EndRunAndLearnSessionResponse response = endRunAndLearnSessionService.endSession(userId, sessionId, request, LocalDateTime.now());

        return ResponseEntity.ok(response);
    }
}
