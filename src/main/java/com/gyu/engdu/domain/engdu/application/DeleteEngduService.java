package com.gyu.engdu.domain.engdu.application;

import com.gyu.engdu.domain.engdu.domain.Engdu;
import com.gyu.engdu.domain.engdu.domain.EngduRepository;
import com.gyu.engdu.exception.CustomException;
import com.gyu.engdu.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class DeleteEngduService {

  private final EngduRepository engduRepository;
  private final EngduQueryService engduQueryService;

  public void delete(Long userId, Long engduId) {
    Engdu engdu = engduQueryService.findExistingEngdu(engduId);
    Long engduOwnerId = engdu.getUserId();
    validateEngduOwnerShip(userId, engduOwnerId);
    engduRepository.delete(engdu);
  }

  private void validateEngduOwnerShip(Long userId, Long engduUserId) {
    if(!engduUserId.equals(userId)){
      throw new CustomException(ErrorCode.ENGDU_FORBIDDEN_ACCESS);
    }
  }
}
