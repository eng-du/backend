package com.gyu.engdu.domain.engdu.application;

import com.gyu.engdu.domain.engdu.application.dto.request.GenerateEngduRequest;
import com.gyu.engdu.domain.engdu.application.dto.response.GeneratedEngduResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreateEngduService {

  private final EngduClient engduClient;

  public Long create(String topic, String level) {
    GenerateEngduRequest engduRequest = GenerateEngduRequest.of(topic, level);
    GeneratedEngduResponse engduResponse = engduClient.generateEngdu(engduRequest);

    return 1L;
  }

}
