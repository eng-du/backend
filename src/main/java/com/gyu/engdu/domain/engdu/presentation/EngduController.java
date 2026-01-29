package com.gyu.engdu.domain.engdu.presentation;

import com.gyu.engdu.domain.engdu.application.CreateEngduService;
import com.gyu.engdu.domain.engdu.application.DeleteEngduService;
import com.gyu.engdu.domain.engdu.application.EngduQueryService;
import com.gyu.engdu.domain.engdu.application.LikeEngduService;
import com.gyu.engdu.domain.engdu.application.SolveQuestionService;
import com.gyu.engdu.domain.engdu.domain.enums.EngduSortKey;
import com.gyu.engdu.domain.engdu.domain.enums.SolvedFilter;
import com.gyu.engdu.domain.engdu.presentation.dto.request.CreateEngduRequest;
import com.gyu.engdu.domain.engdu.presentation.dto.request.LikeEngduRequest;
import com.gyu.engdu.domain.engdu.presentation.dto.request.SubmissionEngduRequest;
import com.gyu.engdu.domain.engdu.presentation.dto.response.CreateEngduResponse;
import com.gyu.engdu.domain.engdu.presentation.dto.response.EngduDetailResponse;
import com.gyu.engdu.domain.engdu.presentation.dto.response.EngduPageResponse;
import com.gyu.engdu.domain.engdu.presentation.dto.response.EngduSummaryResponse;
import com.gyu.engdu.domain.engdu.presentation.dto.response.SubmissionEngduResponse;
import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/engdu")
@RequiredArgsConstructor
public class EngduController {

  private final CreateEngduService createEngduService;
  private final DeleteEngduService deleteEngduService;
  private final EngduQueryService engduQueryService;
  private final SolveQuestionService solveQuestionService;
  private final LikeEngduService likeEngduService;

  @PostMapping
  public ResponseEntity<CreateEngduResponse> createEngdu(
      @RequestBody CreateEngduRequest request,
      @AuthenticationPrincipal(expression = "userId") Long userId) {
    String topic = request.topic();
    String level = request.level();
    Long engduId = createEngduService.create(userId, topic, level);

    return ResponseEntity.ok(new CreateEngduResponse(engduId));
  }

  @DeleteMapping("/{engduId}")
  public ResponseEntity<Void> deleteEngdu(
      @PathVariable("engduId") Long engduId,
      @AuthenticationPrincipal(expression = "userId") Long userId) {
    deleteEngduService.delete(userId, engduId);
    return ResponseEntity.noContent().build();
  }

  @GetMapping
  public ResponseEntity<EngduPageResponse<EngduSummaryResponse>> searchEngdu(
      @RequestParam(name = "page", defaultValue = "0") Integer pageNum,
      @RequestParam(name = "size", defaultValue = "6") Integer size,
      @RequestParam(name = "sortKey", defaultValue = "CREATED_AT") EngduSortKey sortKey,
      @RequestParam(name = "direction", defaultValue = "DESC") Sort.Direction direction,
      @RequestParam(name = "isSolved", defaultValue = "ALL") SolvedFilter solvedFilter,
      @AuthenticationPrincipal(expression = "userId") Long userId) {
    // 클라이언트가 1번 페이지 조회하면 백엔드는 0번 페이지를 조회하도록 하기 위함.
    int adjustedPage = Math.max(0, pageNum - 1);
    Page<EngduSummaryResponse> responses = engduQueryService.searchEngdu(userId, adjustedPage, size,
        sortKey, direction, solvedFilter);
    boolean hasEngdu = engduQueryService.existsEngduByUserId(userId);
    return ResponseEntity.ok(EngduPageResponse.from(responses, hasEngdu));
  }

  @GetMapping("/{engduId}")
  public ResponseEntity<EngduDetailResponse> readDetailEngdu(
      @PathVariable("engduId") Long engduId,
      @AuthenticationPrincipal(expression = "userId") Long userId) {
    return ResponseEntity.ok(engduQueryService.findDetailEngdu(userId, engduId));
  }

  @PostMapping("/{engduId}/question/{questionId}/submission")
  public ResponseEntity<SubmissionEngduResponse> submissionEngdu(
      @PathVariable("engduId") Long engduId,
      @PathVariable("questionId") Long questionId,
      @RequestBody SubmissionEngduRequest req,
      @AuthenticationPrincipal(expression = "userId") Long userId) {
    boolean isAnswered = solveQuestionService.solve(userId, engduId, questionId, req.userAnswer());
    return ResponseEntity.ok(new SubmissionEngduResponse(isAnswered));
  }

  @PostMapping("/{engduId}/like")
  public ResponseEntity<Void> likeEngdu(
      @PathVariable("engduId") Long engduId,
      @RequestBody @Valid LikeEngduRequest req,
      @AuthenticationPrincipal(expression = "userId") Long userId) {
    likeEngduService.like(userId, engduId, req.likeStatus());
    return ResponseEntity.ok().build();
  }
}
