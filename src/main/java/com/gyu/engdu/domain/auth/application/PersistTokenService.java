package com.gyu.engdu.domain.auth.application;

import com.gyu.engdu.domain.auth.domain.RefreshToken;
import com.gyu.engdu.domain.auth.domain.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class PersistTokenService {

  private final RefreshTokenRepository refreshTokenRepository;

  public RefreshToken persistRefreshToken(RefreshToken refreshToken) {
    return refreshTokenRepository.save(refreshToken);
  }
}
