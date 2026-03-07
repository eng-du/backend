package com.gyu.engdu.domain.auth.application;

import com.gyu.engdu.domain.auth.application.dto.response.AuthTokenServiceResponse;
import com.gyu.engdu.domain.auth.domain.RefreshToken;
import com.gyu.engdu.domain.auth.domain.RefreshTokenRepository;
import com.gyu.engdu.domain.user.application.UserQueryService;
import com.gyu.engdu.domain.user.domain.User;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ReissueTokenService {

  private final RefreshTokenRepository refreshTokenRepository;
  private final ParseTokenService parseTokenService;
  private final UserQueryService userQueryService;
  private final CreateTokenService createTokenService;
  private final PersistTokenService persistTokenService;
  private final TokenQueryService tokenQueryService;

  public AuthTokenServiceResponse reissue(String rawRefreshToken, Date date) {
    RefreshToken refreshToken = tokenQueryService.findExistingRefreshToken(rawRefreshToken);
    Long userId = parseTokenService.extractUserId(refreshToken);
    User user = userQueryService.findExistingUser(userId);
    AuthTokenServiceResponse newAuthToken = createTokenService.createAuthToken(user.getId(),
        user.getRole(), date);
    refreshTokenRepository.delete(refreshToken);
    persistTokenService.persistRefreshToken(newAuthToken.refreshToken());
    return newAuthToken;
  }

}
