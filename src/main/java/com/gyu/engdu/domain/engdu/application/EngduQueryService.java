package com.gyu.engdu.domain.engdu.application;

import com.gyu.engdu.domain.engdu.domain.Engdu;
import com.gyu.engdu.domain.engdu.domain.EngduRepository;
import com.gyu.engdu.domain.engdu.domain.enums.EngduSortKey;
import com.gyu.engdu.domain.engdu.domain.enums.SolvedFilter;
import com.gyu.engdu.domain.engdu.presentation.dto.response.EngduDetailResponse;
import com.gyu.engdu.domain.engdu.presentation.dto.response.EngduSummaryResponse;
import com.gyu.engdu.exception.CustomException;
import com.gyu.engdu.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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
        .orElseThrow(() -> new CustomException(ErrorCode.ENGDU_NOT_FOUND));
  }

  public Page<EngduSummaryResponse> searchEngdu(Long userId, Integer pageNum, Integer size,
      EngduSortKey sortKey,
      Sort.Direction direction, SolvedFilter solvedFilter) {
    Pageable pageable = PageRequest.of(
        pageNum,
        size,
        Sort.by(direction, sortKey.getProperty())
    );

    switch (solvedFilter) {
      case TRUE, FALSE -> {
        return engduRepository
            .findAllByUserIdAndIsAllSolved(userId, solvedFilter.getProperty(), pageable)
            .map(EngduSummaryResponse::from);
      }

      default -> {
        return engduRepository
            .findAllByUserId(userId, pageable)
            .map(EngduSummaryResponse::from);
      }
    }
  }

  public EngduDetailResponse findDetailEngdu(Long userId, Long engduId) {
    Engdu engdu = this.findExistingEngdu(engduId);
    engdu.validateOwner(userId);
    return EngduDetailResponse.fromEntity(engdu);
  }

}
