package com.gyu.engdu.domain.engdu.application;

import com.gyu.engdu.domain.engdu.application.dto.request.GenerateEngduRequest;
import com.gyu.engdu.domain.engdu.application.dto.response.GeneratedEngduResponse;
import com.gyu.engdu.domain.engdu.domain.Engdu;
import com.gyu.engdu.domain.engdu.domain.EngduRepository;
import com.gyu.engdu.domain.engdu.domain.Question;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class CreateEngduService {

  private final EngduClient engduClient;
  private final EngduRepository engduRepository;

  public Long create(Long userId, String topic, String level) {
    GenerateEngduRequest engduRequest = GenerateEngduRequest.of(topic, level);
    GeneratedEngduResponse engduResponse = engduClient.generateEngdu(engduRequest);

    Engdu engdu = Engdu.of(userId, engduResponse.title(), topic);
    engduResponse.articles().forEach(e -> e.toEntity(engdu));

    engduResponse.questions().forEach((questionDto) -> {
      Question question = questionDto.toEntity(engdu);
      questionDto.choices().forEach(choiceDto -> choiceDto.toEntity(question));
    });

    engduRepository.save(engdu);
    return engdu.getId();
  }

}
