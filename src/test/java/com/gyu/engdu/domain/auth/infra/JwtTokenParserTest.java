package com.gyu.engdu.domain.auth.infra;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.gyu.engdu.domain.auth.application.TokenParser;
import com.gyu.engdu.domain.auth.application.TokenProvider;
import com.gyu.engdu.domain.user.domain.Role;
import com.gyu.engdu.exception.CustomException;
import com.gyu.engdu.exception.ErrorCode;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class JwtTokenParserTest {

  @Autowired
  TokenParser tokenParser;
  @Autowired
  TokenProvider tokenProvider;

  @DisplayName("jwt 토큰에서 유저의 권한을 파싱할 수 있다.")
  @Test
  void parseRoleFromJwt() {
    //given
    Role givenRole = Role.ROLE_USER;
    String rawAccessToken = tokenProvider.createRawAccessToken(1L, givenRole, new Date());

    //when
    Role role = tokenParser.parseRoleFromAccessToken(rawAccessToken);

    //then
    assertThat(role).isEqualTo(givenRole);
  }

  @DisplayName("jwt 토큰에서 유저의 아이디를 파싱할 수 있다.")
  @Test
  void parseUserIdFromJwt() {
    //given
    Long givenUserId = 1L;
    String rawAccessToken = tokenProvider.createRawAccessToken(givenUserId, Role.ROLE_USER,
        new Date());

    //when
    Long userId = tokenParser.parseUserIdFromToken(rawAccessToken);

    //then
    assertThat(userId).isEqualTo(givenUserId);
  }

  @DisplayName("유효기간 1시간이 지난 애세스 토큰은 사용할 수 없다.")
  @Test
  void validAccessTokenExpiration() {
    //given
    Instant before1HourInstant = Instant.now().minus(Duration.ofHours(1));
    Date before1Hour = Date.from(before1HourInstant);
    String rawAccessToken = tokenProvider.createRawAccessToken(1L, Role.ROLE_USER, before1Hour);

    //when
    CustomException ex = assertThrows(CustomException.class, () -> {
      tokenParser.parseRoleFromAccessToken(rawAccessToken);
    });

    // then
    assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.JWT_EXPIRED);
  }

  @DisplayName("유효기간 14일이 지난 리프레시 토큰은 사용할 수 없다.")
  @Test
  void validRefreshTokenExpiration() {
    //given
    Instant before14DaysInstant = Instant.now().minus(Duration.ofDays(14));
    Date before14Days = Date.from(before14DaysInstant);
    String rawAccessToken = tokenProvider.createRawRefreshToken(1L, before14Days);

    //when
    CustomException ex = assertThrows(CustomException.class, () -> {
      tokenParser.parseRoleFromAccessToken(rawAccessToken);
    });

    // then
    assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.JWT_EXPIRED);
  }

  @DisplayName("서명이 다른 jwt 토큰은 사용할 수 없다.")
  @Test
  void validTokenSignature() {
    //given
    String invalidJwt = "INVALID_JWT_TOKEN";

    //when
    CustomException ex = assertThrows(CustomException.class, () -> {
      tokenParser.parseUserIdFromToken(invalidJwt);
    });

    // then
    assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.JWT_INVALID);
  }
}