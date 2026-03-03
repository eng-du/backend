package com.gyu.engdu.domain.engdu.application;

import com.gyu.engdu.domain.engdu.application.dto.request.GenerateEngduRequest;
import com.gyu.engdu.domain.engdu.application.dto.response.EngduPartResponse;
import com.gyu.engdu.domain.engdu.application.dto.response.GeneratedEngduResponse;
import com.gyu.engdu.domain.engdu.domain.Article;
import com.gyu.engdu.domain.engdu.domain.ArticleChunk;
import com.gyu.engdu.domain.engdu.domain.Engdu;
import com.gyu.engdu.domain.engdu.domain.EngduRepository;
import com.gyu.engdu.domain.engdu.domain.Part;
import com.gyu.engdu.domain.engdu.domain.Question;
import com.gyu.engdu.domain.engdu.domain.enums.PartType;
import java.util.List;
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

  public Long create(Long userId, String level, String topic) {
    Engdu engdu = Engdu.of(userId, level, topic);
    engduRepository.save(engdu);
    return engdu.getId();
  }

  /**
   * LLM으로 INITIAL Part의 Article/Question을 생성하고 저장합니다.
   * Part는 외부(Listener)에서 락으로 조회한 것을 재사용하여 중복 생성을 방지합니다.
   */
  public EngduPartResponse generateInitialPart(Part part) {
    Engdu engdu = part.getEngdu();

    GenerateEngduRequest engduRequest = GenerateEngduRequest.of(
        engdu.getTopic(),
        engdu.getLevel(),
        PartType.INITIAL,
        null);

    GeneratedEngduResponse engduResponse = engduClient.generateEngdu(engduRequest);

    engdu.changeTitle(engduResponse.title());
    return saveAndFlushPart(engdu, part, engduResponse);
  }

  /**
   * LLM으로 COMPLETE Part의 Article/Question을 생성하고 저장합니다.
   * Part는 외부(Listener)에서 락으로 조회한 것을 재사용하여 중복 생성을 방지합니다.
   */
  public EngduPartResponse generateNextPart(Part part) {
    Engdu engdu = part.getEngdu();

    String previousArticleContent = getPreviousArticleContent(engdu);

    GenerateEngduRequest engduRequest = GenerateEngduRequest.of(
        engdu.getTopic(),
        engdu.getLevel(),
        PartType.COMPLETE,
        previousArticleContent);

    GeneratedEngduResponse engduResponse = engduClient.generateEngdu(engduRequest);
    return saveAndFlushPart(engdu, part, engduResponse);
  }

  private EngduPartResponse saveAndFlushPart(Engdu engdu, Part part, GeneratedEngduResponse engduResponse) {
    Article article = engduResponse.article().toEntity(part);

    List<Question> questions = engduResponse.questions().stream()
        .map(questionDto -> {
          Question question = questionDto.toEntity(part);
          questionDto.choices().forEach(choiceDto -> choiceDto.toEntity(question));
          return question;
        })
        .toList();

    // question Id를 받아오기 위해 flush 한다.
    engduRepository.flush();

    return EngduPartResponse.of(engdu, article, questions);
  }

  private String getPreviousArticleContent(Engdu engdu) {
    return engdu.getParts().stream()
        .findFirst()
        .map(Part::getArticle)
        .map(article -> article.getChunks().stream()
            .map(ArticleChunk::getEn)
            .collect(Collectors.joining(" ")))
        .orElse("");
  }

}