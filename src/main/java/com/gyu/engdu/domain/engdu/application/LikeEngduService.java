package com.gyu.engdu.domain.engdu.application;

import com.gyu.engdu.domain.engdu.domain.Engdu;
import com.gyu.engdu.domain.engdu.domain.enums.LikeStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class LikeEngduService {

  private final EngduQueryService engduQueryService;

  public void like(Long userId, Long engduId, LikeStatus likeStatus) {
    Engdu engdu = engduQueryService.findExistingEngdu(engduId);
    engdu.validateOwner(userId);
    engdu.changeLikeStatus(likeStatus);
  }

}
