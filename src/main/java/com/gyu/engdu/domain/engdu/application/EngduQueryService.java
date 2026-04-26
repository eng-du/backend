package com.gyu.engdu.domain.engdu.application;

import com.gyu.engdu.domain.engdu.application.dto.response.EngduDetailResponse;
import com.gyu.engdu.domain.engdu.domain.Engdu;
import com.gyu.engdu.domain.engdu.domain.EngduRepository;
import com.gyu.engdu.domain.engdu.domain.enums.EngduSortKey;
import com.gyu.engdu.domain.engdu.domain.enums.SolvedFilter;
import com.gyu.engdu.domain.engdu.exception.EngduNotFoundException;
import com.gyu.engdu.domain.engdu.presentation.dto.response.EngduSummaryResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class EngduQueryService {

  private final EngduRepository engduRepository;

  public Engdu findExistingEngdu(Long id) {
    return engduRepository.findById(id)
        .orElseThrow(() -> new EngduNotFoundException(id));
  }

  public Page<EngduSummaryResponse> paginationEngdu(
      Long userId,
      Integer pageNum,
      Integer size,
      EngduSortKey sortKey,
      Sort.Direction direction,
      SolvedFilter solvedFilter
  ) {
    Pageable pageable = createPageable(pageNum, size, sortKey, direction);

    // step1: covering index로 빠르게 offset에 해당하는 id 리스트를 조회한다.
    Page<Long> idsPage = findPagedIds(userId, solvedFilter, pageable);
    List<Long> ids = idsPage.getContent();

    // 조회 가능한 id가 없다면 빈 페이지를 반환한다.
    if (ids.isEmpty()) {
      return Page.empty(pageable);
    }

    // step2: id 리스트로 engdu 엔티티의 전체 컬럼을 조회한다.
    List<Engdu> engdus = engduRepository.findByIdIn(ids, pageable.getSort());

    List<EngduSummaryResponse> responses = engdus.stream()
        .map(EngduSummaryResponse::from)
        .toList();

    return new PageImpl<>(responses, pageable, idsPage.getTotalElements());
  }

  public EngduDetailResponse findDetailEngdu(Long userId, Long engduId) {
    Engdu engdu = this.findExistingEngdu(engduId);
    engdu.validateOwner(userId);
    return EngduDetailResponse.fromEntity(engdu);
  }

  public boolean existsEngduByUserId(Long userId) {
    return engduRepository.existsByUserId(userId);
  }

  // 페이징 조회를 위한 Pageable 인터페이스를 생성한다.
  private Pageable createPageable(
      int pageNum,
      int size,
      EngduSortKey sortKey,
      Sort.Direction direction
  ) {
    return PageRequest.of(
        pageNum,
        size,
        Sort.by(direction, sortKey.getProperty())
    );
  }

  /**
   * 페이징 조회의 isAllSolved 조건 유무에 따라 쿼리를 다르게 적용한다. 커버링 인덱스를 이용해 offset에 해당하는 id 리스트를 조회한다.
   */
  private Page<Long> findPagedIds(
      Long userId,
      SolvedFilter solvedFilter,
      Pageable pageable
  ) {
    return switch (solvedFilter) {
      case TRUE, FALSE -> engduRepository
          .findIdsByUserIdAndIsAllSolved(
              userId,
              solvedFilter.getProperty(),
              pageable
          );

      default -> engduRepository
          .findIdsByUserId(userId, pageable);
    };
  }
}
