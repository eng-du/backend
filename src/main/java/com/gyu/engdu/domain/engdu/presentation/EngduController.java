package com.gyu.engdu.domain.engdu.presentation;

import com.gyu.engdu.domain.engdu.application.CreateEngduService;
import com.gyu.engdu.domain.engdu.application.DeleteEngduService;
import com.gyu.engdu.domain.engdu.application.EngduQueryService;
import com.gyu.engdu.domain.engdu.application.SolveQuestionService;
import com.gyu.engdu.domain.engdu.domain.enums.EngduSortKey;
import com.gyu.engdu.domain.engdu.domain.enums.SolvedFilter;
import com.gyu.engdu.domain.engdu.presentation.dto.request.CreateEngduRequest;
import com.gyu.engdu.domain.engdu.presentation.dto.request.SubmissionEngduRequest;
import com.gyu.engdu.domain.engdu.presentation.dto.response.EngduSummaryResponse;
import com.gyu.engdu.domain.engdu.presentation.dto.response.SubmissionEngduResponse;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
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

  @PostMapping
  public ResponseEntity<Void> createEngdu(@RequestBody CreateEngduRequest request) {
    Long userId = 1L;
    String topic = request.topic();
    String level = request.level();
    Long engduId = createEngduService.create(userId, topic, level);
    URI location = URI.create("/api/v1/engdu/%d".formatted(engduId));

    return ResponseEntity.created(location).build();
  }

  @DeleteMapping("/{engduId}")
  public ResponseEntity<Void> deleteEngdu(@PathVariable("engduId") Long engduId) {
    deleteEngduService.delete(1L, engduId);
    return ResponseEntity.noContent().build();
  }

  @GetMapping
  public ResponseEntity<Page<EngduSummaryResponse>> searchEngdu(
      @RequestParam(name = "page", defaultValue = "1") Integer pageNum,
      @RequestParam(name = "size", defaultValue = "6") Integer size,
      @RequestParam(name = "sortKey", defaultValue = "CREATED_AT") EngduSortKey sortKey,
      @RequestParam(name = "direction", defaultValue = "DESC") Sort.Direction direction,
      @RequestParam(name = "isSolved", defaultValue = "ALL") SolvedFilter solvedFilter
  ) {
    Long userId = 1L;
    Page<EngduSummaryResponse> responses = engduQueryService.searchEngdu(userId, pageNum, size,
        sortKey, direction, solvedFilter);
    return ResponseEntity.ok(responses);
  }

  @PostMapping("/{engduId}/question/{questionId}/submission")
  public ResponseEntity<SubmissionEngduResponse> submissionEngdu(
      @PathVariable("engduId") Long engduId,
      @PathVariable("questionId") Long questionId,
      @RequestBody SubmissionEngduRequest req
  ) {
    Long userId = 1L;
    boolean isAnswered = solveQuestionService.solve(userId, engduId, questionId, req.userAnswer());
    return ResponseEntity.ok(new SubmissionEngduResponse(isAnswered));
  }
}
