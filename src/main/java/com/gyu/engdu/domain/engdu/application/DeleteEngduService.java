package com.gyu.engdu.domain.engdu.application;

import com.gyu.engdu.domain.engdu.domain.Engdu;
import com.gyu.engdu.domain.engdu.domain.EngduRepository;
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
    engdu.validateOwner(userId);
    engduRepository.delete(engdu);
  }
}
