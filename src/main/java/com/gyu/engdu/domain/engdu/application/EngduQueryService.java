package com.gyu.engdu.domain.engdu.application;

import com.gyu.engdu.domain.engdu.domain.Engdu;
import com.gyu.engdu.domain.engdu.domain.EngduRepository;
import com.gyu.engdu.exception.CustomException;
import com.gyu.engdu.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
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
}
