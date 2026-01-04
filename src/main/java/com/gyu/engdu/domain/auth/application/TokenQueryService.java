package com.gyu.engdu.domain.auth.application;

import com.gyu.engdu.domain.auth.domain.RefreshToken;
import com.gyu.engdu.domain.auth.domain.RefreshTokenRepository;
import com.gyu.engdu.exception.CustomException;
import com.gyu.engdu.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TokenQueryService {

  private final RefreshTokenRepository refreshTokenRepository;

  public RefreshToken findExistingRefreshToken(String rawToken) {
    return refreshTokenRepository.findByRawToken(rawToken)
        .orElseThrow(() -> new CustomException(ErrorCode.REFRESH_TOKEN_NOT_FOUND));
  }
}
