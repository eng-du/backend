package com.gyu.engdu.domain.engdu.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

import com.gyu.engdu.domain.engdu.domain.Article;
import com.gyu.engdu.domain.engdu.domain.ArticleChunk;
import com.gyu.engdu.domain.engdu.domain.Engdu;
import com.gyu.engdu.domain.engdu.domain.EngduRepository;
import com.gyu.engdu.domain.engdu.domain.Part;
import com.gyu.engdu.domain.engdu.domain.Question;
import com.gyu.engdu.domain.engdu.domain.enums.Category;
import com.gyu.engdu.domain.engdu.domain.enums.EngduSortKey;
import com.gyu.engdu.domain.engdu.domain.enums.PartType;
import com.gyu.engdu.domain.engdu.domain.enums.SolvedFilter;
import com.gyu.engdu.domain.engdu.application.dto.response.EngduDetailResponse;
import com.gyu.engdu.domain.engdu.presentation.dto.response.EngduSummaryResponse;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@ActiveProfiles("test")
@SpringBootTest
class EngduQueryServiceTest {

  @Autowired
  private EngduRepository engduRepository;

  @Autowired
  private EngduQueryService engduQueryService;

  @DisplayName("잉듀 목록을 최신순으로 정렬한다.")
  @Test
  void searchEngdu() {
    // given
    Long userId = 1L;
    int pageNum = 0;
    int size = 2;
    EngduSortKey engduSortKey = EngduSortKey.CREATED_AT;
    Sort.Direction direction = Direction.DESC;
    SolvedFilter solvedFilter = SolvedFilter.ALL;

    Engdu engdu1 = createEngdu(userId, "topic1", true);
    Engdu engdu2 = createEngdu(userId, "topic2", false);
    Engdu engdu3 = createEngdu(userId, "topic3", true);

    engduRepository.saveAll(List.of(engdu1, engdu2, engdu3));

    // when
    Page<EngduSummaryResponse> result = engduQueryService.searchEngdu(userId, pageNum, size,
        engduSortKey, direction, solvedFilter);

    // then
    assertThat(result.getContent()).hasSize(2);
    assertThat(result.getTotalElements()).isEqualTo(3);
    assertThat(result.getContent().get(0).topic()).isEqualTo("topic3");
    assertThat(result.getContent().get(1).topic()).isEqualTo("topic2");
  }

  @DisplayName("해결된 잉듀만 필터링하여 조회한다.")
  @Test
  void searchEngdu2() {
    // given
    Long userId = 1L;
    int pageNum = 0;
    int size = 10;
    EngduSortKey engduSortKey = EngduSortKey.CREATED_AT;
    Sort.Direction direction = Direction.DESC;
    SolvedFilter solvedFilter = SolvedFilter.TRUE;

    Engdu engdu1 = createEngdu(userId, "topic1", true);
    Engdu engdu2 = createEngdu(userId, "topic2", false);
    Engdu engdu3 = createEngdu(userId, "topic3", true);

    engduRepository.saveAll(List.of(engdu1, engdu2, engdu3));

    // when
    Page<EngduSummaryResponse> result = engduQueryService.searchEngdu(userId, pageNum, size,
        engduSortKey, direction, solvedFilter);

    // then
    assertThat(result.getContent()).hasSize(2);
    assertThat(result.getContent()).extracting("topic")
        .containsExactly("topic3", "topic1");
  }

  @DisplayName("해결되지 않은 잉듀만 필터링하여 조회한다.")
  @Test
  void searchEngdu3() {
    // given
    Long userId = 1L;
    int pageNum = 0;
    int size = 10;
    EngduSortKey engduSortKey = EngduSortKey.CREATED_AT;
    Sort.Direction direction = Direction.DESC;
    SolvedFilter solvedFilter = SolvedFilter.FALSE;

    Engdu engdu1 = createEngdu(userId, "topic1", true);
    Engdu engdu2 = createEngdu(userId, "topic2", false);
    Engdu engdu3 = createEngdu(userId, "topic3", true);

    engduRepository.saveAll(List.of(engdu1, engdu2, engdu3));

    // when
    Page<EngduSummaryResponse> result = engduQueryService.searchEngdu(userId, pageNum, size,
        engduSortKey, direction, solvedFilter);

    // then
    assertThat(result.getContent()).hasSize(1);
    assertThat(result.getContent().get(0).topic()).isEqualTo("topic2");
  }

  @DisplayName("잉듀 상세 정보를 조회하여 질문 목록을 조회할 수 있다.")
  @Test
  void findDetailEngdu() {
    // given
    Long userId = 1L;
    Engdu engdu = createEngdu(userId, "Detailed topic", false);
    Part part = Part.of(PartType.INITIAL, engdu);

    createArticle(part, List.of("Chunk1", "Chunk2"), List.of("한국어청크1", "한국어청크2"));

    Question question1 = createQuestion(part, (byte) 1, "Question Content1", Category.GRAMMAR, true);
    Question question2 = createQuestion(part, (byte) 2, "Question Content2", Category.VOCA, false);

    engduRepository.save(engdu);

    // when
    EngduDetailResponse response = engduQueryService.findDetailEngdu(userId, engdu.getId());

    // then
    assertThat(response.engduId()).isEqualTo(engdu.getId());
    assertThat(response.parts()).hasSize(1);
    assertThat(response.parts().get(0).questions()).hasSize(2)
        .extracting("questionId", "answer", "content")
        .containsExactlyInAnyOrder(
            tuple(question1.getId(), (byte) 1, "Question Content1"),
            tuple(question2.getId(), null, "Question Content2"));

    assertThat(response.parts().get(0).article().chunks()).hasSize(2)
        .extracting("en", "kor")
        .containsExactly(
            tuple("Chunk1", "한국어청크1"),
            tuple("Chunk2", "한국어청크2"));
  }

  private void createArticle(Part part, List<String> ens, List<String> kors) {
    Article article = Article.of(part);
    for (int i = 0; i < ens.size(); i++) {
      ArticleChunk.of(ens.get(i), kors.get(i), article);
    }
  }

  private Engdu createEngdu(Long userId, String topic, boolean isAllSolved) {
    return Engdu.builder()
        .userId(userId)
        .topic(topic)
        .isAllSolved(isAllSolved)
        .build();
  }

  private Question createQuestion(Part part, byte answer, String content, Category category, boolean isCorrected) {
    Question question = Question.builder()
        .answer(answer)
        .content(content)
        .category(category)
        .isCorrected(isCorrected)
        .build();
    question.setPart(part);
    return question;
  }
}