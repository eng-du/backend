package com.gyu.engdu.domain.engdu.application;

import com.gyu.engdu.domain.engdu.application.dto.request.GenerateEngduRequest;
import com.gyu.engdu.domain.engdu.application.dto.response.GeneratedEngduResponse;
import com.gyu.engdu.domain.engdu.domain.Engdu;
import com.gyu.engdu.domain.engdu.domain.EngduRepository;
import com.gyu.engdu.domain.engdu.domain.enums.PartType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreateEngduService {

  private final EngduClient engduClient;
  private final EngduRepository engduRepository;
  private final PartCommandService partCommandService;

  @Transactional
  public Long create(Long userId, String level, String topic) {
    Engdu engdu = Engdu.of(userId, level, topic);
    engduRepository.save(engdu);
    return engdu.getId();
  }

  public void generatePart(Long partId, PartType step) {
    // [TX 1]: 짧은 Read-only TX로 LLM 요청 데이터 로드
    GenerateEngduRequest request = partCommandService.buildLlmRequest(partId, step);

    // 트랜잭션 없이 LLM API 호출
    GeneratedEngduResponse response = engduClient.generateEngdu(request);

    // [TX 3]: 파트 디비 영속 및 상태 변경(DONE)을 커밋
    partCommandService.saveResultAndMarkDone(partId, response);
  }
}