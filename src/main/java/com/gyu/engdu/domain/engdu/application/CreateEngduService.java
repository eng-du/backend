package com.gyu.engdu.domain.engdu.application;

import com.gyu.engdu.domain.engdu.application.dto.request.GenerateEngduRequest;
import com.gyu.engdu.domain.engdu.application.dto.response.GeneratedEngduResponse;
import com.gyu.engdu.domain.engdu.domain.ArticleChunk;
import com.gyu.engdu.domain.engdu.domain.Engdu;
import com.gyu.engdu.domain.engdu.domain.EngduRepository;
import com.gyu.engdu.domain.engdu.domain.Question;
import com.gyu.engdu.domain.engdu.domain.enums.EngduCreationStep;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class CreateEngduService {

  private final EngduClient engduClient;
  private final EngduRepository engduRepository;
  private final EngduQueryService engduQueryService;

  public Long create(Long userId, String level, String topic) {
    Engdu engdu = Engdu.of(userId, level, topic);
    engduRepository.save(engdu);
    return engdu.getId();
  }

  public Long generateInitialContent(Long userId, Long engduId) {
    Engdu engdu = engduQueryService.findExistingEngdu(engduId);
    engdu.validateOwner(userId);

    GenerateEngduRequest engduRequest = GenerateEngduRequest.of(
        engdu.getTopic(),
        engdu.getLevel(),
        EngduCreationStep.INITIAL,
        null
    );

    GeneratedEngduResponse engduResponse = engduClient.generateEngdu(engduRequest);

    engdu.changeTitle(engduResponse.title());
    saveContent(engdu, engduResponse);

    return engdu.getId();
  }

  public Long generateNextContent(Long userId, Long engduId) {
    Engdu engdu = engduQueryService.findExistingEngdu(engduId);
    engdu.validateOwner(userId);

    String previousArticleContent = getPreviousArticleContent(engdu);

    GenerateEngduRequest engduRequest = GenerateEngduRequest.of(
        engdu.getTopic(),
        engdu.getLevel(),
        EngduCreationStep.COMPLETE,
        previousArticleContent
    );

    GeneratedEngduResponse engduResponse = engduClient.generateEngdu(engduRequest);
    saveContent(engdu, engduResponse);

    return engdu.getId();
  }

  private void saveContent(Engdu engdu, GeneratedEngduResponse engduResponse) {
    engduResponse.article().toEntity(engdu);
    engduResponse.questions().forEach((questionDto) -> {
      Question question = questionDto.toEntity(engdu);
      questionDto.choices().forEach(choiceDto -> choiceDto.toEntity(question));
    });
  }

  private String getPreviousArticleContent(Engdu engdu) {
    return engdu.getArticles().stream()
        .findFirst()
        .map(article -> article.getChunks().stream()
            .map(ArticleChunk::getEn)
            .collect(Collectors.joining(" ")))
        .orElse("");
  }

}
