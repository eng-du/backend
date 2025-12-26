package com.gyu.engdu.domain.auth.application;

import com.gyu.engdu.domain.auth.application.dto.response.AuthTokenServiceResponse;
import com.gyu.engdu.domain.auth.domain.RefreshToken;
import com.gyu.engdu.domain.auth.domain.RefreshTokenRepository;
import com.gyu.engdu.domain.user.application.UserQueryService;
import com.gyu.engdu.domain.user.domain.User;
import com.gyu.engdu.exception.CustomException;
import com.gyu.engdu.exception.ErrorCode;
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

  public AuthTokenServiceResponse reissue(String rawRefreshToken, Date date) {
    RefreshToken refreshToken = findExistingRefreshToken(rawRefreshToken);
    Long userId = parseTokenService.extractUserId(refreshToken);
    User user = userQueryService.findExistingUser(userId);
    AuthTokenServiceResponse newAuthToken = createTokenService.createAuthToken(user.getId(),
        user.getRole(), date);
    refreshTokenRepository.delete(refreshToken);
    persistTokenService.persistRefreshToken(newAuthToken.refreshToken());
    return newAuthToken;
  }

  private RefreshToken findExistingRefreshToken(String rawToken) {
    return refreshTokenRepository.findByRawToken(rawToken)
        .orElseThrow(() -> new CustomException(ErrorCode.REFRESH_TOKEN_NOT_FOUND));
  }
}
