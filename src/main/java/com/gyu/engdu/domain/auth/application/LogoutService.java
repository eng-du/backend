package com.gyu.engdu.domain.auth.application;

import com.gyu.engdu.domain.auth.domain.RefreshToken;
import com.gyu.engdu.domain.auth.domain.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LogoutService {

  private final RefreshTokenRepository refreshTokenRepository;
  private final TokenQueryService tokenQueryService;

  @Transactional
  public void logout(String rawRefreshToken) {
    RefreshToken refreshToken = tokenQueryService.findExistingRefreshToken(rawRefreshToken);
    refreshTokenRepository.delete(refreshToken);
  }
}
