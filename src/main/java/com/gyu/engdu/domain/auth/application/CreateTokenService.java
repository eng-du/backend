package com.gyu.engdu.domain.auth.application;

import com.gyu.engdu.domain.auth.application.dto.AuthTokenServiceResponse;
import com.gyu.engdu.domain.auth.domain.AccessToken;
import com.gyu.engdu.domain.auth.domain.RefreshToken;
import com.gyu.engdu.domain.user.domain.Role;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreateTokenService {

  private final TokenProvider tokenProvider;

  public AuthTokenServiceResponse createAuthToken(Long userId, Role role, Date issuedAt) {
    AccessToken accessToken = createAccessToken(userId, role, issuedAt);
    RefreshToken refreshToken = createRefreshToken(userId, issuedAt);
    return AuthTokenServiceResponse.of(accessToken, refreshToken);
  }

  private AccessToken createAccessToken(Long userId, Role role, Date issuedAt) {
    String rawAccessToken = tokenProvider.createRawAccessToken(userId, role, issuedAt);
    return AccessToken.of(rawAccessToken);
  }

  private RefreshToken createRefreshToken(Long userId, Date issuedAt) {
    String rawRefreshToken = tokenProvider.createRawRefreshToken(userId, issuedAt);
    return RefreshToken.of(userId, rawRefreshToken);
  }
}
