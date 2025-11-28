package com.gyu.engdu.domain.engdu.application;

import com.gyu.engdu.domain.engdu.domain.Engdu;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class SolveQuestionService {

  private final EngduQueryService engduQueryService;

  public boolean solve(Long userId, Long engduId, Long questionId, Byte userAnswer) {
    Engdu engdu = engduQueryService.findExistingEngdu(engduId);
    engdu.validateOwner(userId);
    return engdu.submission(questionId, userAnswer);
  }
}
