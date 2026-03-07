package com.gyu.engdu.domain.learning.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.gyu.engdu.IntegrationTestSupport;
import com.gyu.engdu.domain.learning.application.dto.PhrasalVerbResponse;
import com.gyu.engdu.domain.learning.domain.PhrasalVerb;
import com.gyu.engdu.domain.learning.domain.PhrasalVerbRepository;
import com.gyu.engdu.domain.learning.exception.PhrasalVerbNotFoundException;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class PhrasalVerbQueryServiceTest extends IntegrationTestSupport {

    @Autowired
    private PhrasalVerbQueryService phrasalVerbQueryService;

    @Autowired
    private PhrasalVerbRepository phrasalVerbRepository;

    @Test
    @DisplayName("구동사 최대 id값을 구할 때 구동사 엔티티가 없다면 예외를 반환한다.")
    void getPhrasalMaxId() {

        // when & then
        assertThatThrownBy(() -> phrasalVerbQueryService.getMaxId())
                .isInstanceOf(PhrasalVerbNotFoundException.class);
    }

    @Test
    @DisplayName("피벗이 가장 작은 값일 때 구동사 조회에 성공한다.")
    void getPhrasalVerb1() {
        // given
        PhrasalVerb phrasalVerb1 = createPhrasalVerb("verb1");
        PhrasalVerb phrasalVerb2 = createPhrasalVerb("verb2");
        PhrasalVerb phrasalVerb3 = createPhrasalVerb("verb3");
        phrasalVerbRepository.save(phrasalVerb1);
        phrasalVerbRepository.save(phrasalVerb2);
        phrasalVerbRepository.save(phrasalVerb3);

        Long pivot = phrasalVerb1.getId();

        // when
        PhrasalVerbResponse result = phrasalVerbQueryService.getPhrasalVerbByPivot(pivot);

        // then
        assertThat(result.id()).isEqualTo(pivot);
    }

    @Test
    @DisplayName("피벗이 가장 큰 값일 때 구동사 조회에 성공한다.")
    void getPhrasalVerb2() {
        // given
        PhrasalVerb phrasalVerb1 = createPhrasalVerb("verb1");
        PhrasalVerb phrasalVerb2 = createPhrasalVerb("verb2");
        PhrasalVerb phrasalVerb3 = createPhrasalVerb("verb3");
        phrasalVerbRepository.save(phrasalVerb1);
        phrasalVerbRepository.save(phrasalVerb2);
        phrasalVerbRepository.save(phrasalVerb3);

        Long maxId = phrasalVerb3.getId();

        // when
        PhrasalVerbResponse result = phrasalVerbQueryService.getPhrasalVerbByPivot(maxId);

        // then
        assertThat(result.id()).isEqualTo(maxId);
    }

    @Test
    @DisplayName("피벗이 비어있는 id를 가리킨다면 피벗보다 크면서 가장 가까운 id를 가진 구동사를 조회한다.")
    void getPhrasalVerb3() {
        // given
        PhrasalVerb phrasalVerb1 = createPhrasalVerb("verb1");
        PhrasalVerb phrasalVerb2 = createPhrasalVerb("verb2");
        PhrasalVerb phrasalVerb3 = createPhrasalVerb("verb3");
        phrasalVerbRepository.save(phrasalVerb1);
        phrasalVerbRepository.save(phrasalVerb2);
        phrasalVerbRepository.save(phrasalVerb3);

        phrasalVerbRepository.delete(phrasalVerb1);

        long pivot = phrasalVerb1.getId();

        // when
        PhrasalVerbResponse result = phrasalVerbQueryService.getPhrasalVerbByPivot(pivot);

        // then
        assertThat(result.id()).isEqualTo(phrasalVerb2.getId());
    }

    @Test
    @DisplayName("제외 목록이 있다면 특정 구동사를 제외하고 조회한다.")
    void getPhrasalVerb4() {
        // given
        PhrasalVerb phrasalVerb1 = createPhrasalVerb("verb1");
        PhrasalVerb phrasalVerb2 = createPhrasalVerb("verb2");
        PhrasalVerb phrasalVerb3 = createPhrasalVerb("verb3");
        phrasalVerbRepository.save(phrasalVerb1);
        phrasalVerbRepository.save(phrasalVerb2);
        phrasalVerbRepository.save(phrasalVerb3);

        long pivot = phrasalVerb1.getId();
        List<Long> exclusions = List.of(phrasalVerb1.getId(), phrasalVerb2.getId());

        // when
        PhrasalVerbResponse result = phrasalVerbQueryService.getPhrasalVerbByPivotAndExclusion(pivot, exclusions);

        // then
        assertThat(result.id()).isEqualTo(phrasalVerb3.getId());
    }

    private PhrasalVerb createPhrasalVerb(String en) {
        return PhrasalVerb.builder()
                .en(en)
                .kor("kor")
                .exampleSentenceEn("Example Sentence" + en)
                .exampleSentenceKor("Example Sentence Kor" + en)
                .build();
    }
}
