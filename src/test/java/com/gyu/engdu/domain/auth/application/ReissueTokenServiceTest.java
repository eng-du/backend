package com.gyu.engdu.domain.auth.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.gyu.engdu.IntegrationTestSupport;
import com.gyu.engdu.domain.auth.application.dto.response.AuthTokenServiceResponse;
import com.gyu.engdu.domain.auth.domain.RefreshToken;
import com.gyu.engdu.domain.auth.domain.RefreshTokenRepository;
import com.gyu.engdu.domain.auth.exception.JwtExpiredException;
import com.gyu.engdu.domain.auth.exception.RefreshTokenNotFoundException;
import com.gyu.engdu.domain.user.domain.Role;
import com.gyu.engdu.domain.user.domain.User;
import com.gyu.engdu.domain.user.domain.UserRepository;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class ReissueTokenServiceTest extends IntegrationTestSupport {

  @Autowired
  private ReissueTokenService reissueTokenService;

  @Autowired
  private RefreshTokenRepository refreshTokenRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private CreateTokenService createTokenService;

  @Autowired
  private ParseTokenService parseTokenService;

  @DisplayName("주어진 리프레시토큰이 DB에 존재한다면 토큰을 재발급하고 레코드를 삭제한다.")
  @Test
  void reissue() {
    // given
    User user = createUser("test@test.com", Role.ROLE_USER, "test1");
    userRepository.save(user);

    Date now = new Date();
    AuthTokenServiceResponse authToken = createTokenService.createAuthToken(user.getId(),
        user.getRole(), now);
    RefreshToken refreshToken = authToken.refreshToken();
    refreshTokenRepository.save(refreshToken);

    // when
    Date reIssueTime = Date.from(now.toInstant().plusSeconds(10L));
    AuthTokenServiceResponse newAuthToken = reissueTokenService.reissue(refreshToken.getRawToken(),
        reIssueTime);
    RefreshToken newRefreshToken = newAuthToken.refreshToken();
    Long extractUserId = parseTokenService.extractUserId(newRefreshToken);
    boolean empty = refreshTokenRepository.findByRawToken(refreshToken.getRawToken())
        .isEmpty();

    // then
    assertThat(extractUserId).isEqualTo(user.getId());
    assertThat(empty).isTrue();
  }

  @DisplayName("DB에 리프레시 토큰이 없다면 예외가 발생한다.")
  @Test
  void reissue2() {
    // given
    User user = createUser("test@test.com", Role.ROLE_USER, "test1");
    userRepository.save(user);

    Date now = new Date();
    AuthTokenServiceResponse authToken = createTokenService.createAuthToken(user.getId(),
        user.getRole(), now);
    RefreshToken refreshToken = authToken.refreshToken();

    // when & then
    Date reIssueTime = Date.from(now.toInstant().plusSeconds(10L));
    assertThatThrownBy(() -> reissueTokenService.reissue(refreshToken.getRawToken(), reIssueTime))
        .isInstanceOf(RefreshTokenNotFoundException.class);
  }

  @DisplayName("리프레시토큰이 유효기간을 초과하면 예외가 발생한다.")
  @Test
  void reissue3() {
    // given
    User user = createUser("test@test.com", Role.ROLE_USER, "test1");
    userRepository.save(user);

    Instant before14DaysInstant = Instant.now().minus(Duration.ofDays(14));
    Date before14Days = Date.from(before14DaysInstant);
    AuthTokenServiceResponse authToken = createTokenService.createAuthToken(user.getId(),
        user.getRole(), before14Days);
    RefreshToken refreshToken = authToken.refreshToken();
    refreshTokenRepository.save(refreshToken);

    // when & then
    assertThatThrownBy(() -> reissueTokenService.reissue(refreshToken.getRawToken(), new Date()))
        .isInstanceOf(JwtExpiredException.class);
  }

  @DisplayName("이미 재발급에 사용한 리프레시토큰을 다시 재사용하면 예외가 발생한다.")
  @Test
  void reissue4() {
    // given
    User user = createUser("test@test.com", Role.ROLE_USER, "test1");
    userRepository.save(user);

    Date now = new Date();
    AuthTokenServiceResponse authToken = createTokenService.createAuthToken(user.getId(),
        user.getRole(), now);
    RefreshToken refreshToken = authToken.refreshToken();
    refreshTokenRepository.save(refreshToken);

    Date reIssueTime = Date.from(now.toInstant().plusSeconds(10L));
    reissueTokenService.reissue(refreshToken.getRawToken(), reIssueTime);

    // when & then
    assertThatThrownBy(() -> reissueTokenService.reissue(refreshToken.getRawToken(), reIssueTime))
        .isInstanceOf(RefreshTokenNotFoundException.class);
  }

  private User createUser(String email, Role role, String sub) {
    return User.builder()
        .email(email)
        .name("test")
        .role(role)
        .sub(sub)
        .build();
  }
}