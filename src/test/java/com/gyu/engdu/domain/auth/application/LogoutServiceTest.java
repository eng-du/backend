package com.gyu.engdu.domain.auth.application;

import com.gyu.engdu.IntegrationTestSupport;
import com.gyu.engdu.domain.auth.domain.RefreshToken;
import com.gyu.engdu.domain.auth.domain.RefreshTokenRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class LogoutServiceTest extends IntegrationTestSupport {

  @Autowired
  LogoutService logoutService;

  @Autowired
  RefreshTokenRepository refreshTokenRepository;

  @DisplayName("로그아웃시 사용자 기기의 리프레시 토큰 엔티티가 삭제된다.")
  @Test
  void logout() {
    //given
    String rawRefreshToken = "test_token";
    RefreshToken refreshToken = createRefreshToken(1L, rawRefreshToken);
    refreshTokenRepository.save(refreshToken);

    //when
    logoutService.logout(rawRefreshToken);

    //then
    boolean isDeleted = refreshTokenRepository.findByRawToken(rawRefreshToken).isEmpty();
    Assertions.assertThat(isDeleted).isTrue();

  }

  private RefreshToken createRefreshToken(Long userId, String rawToken) {
    return RefreshToken.builder()
        .userId(userId)
        .rawToken(rawToken)
        .build();
  }
}