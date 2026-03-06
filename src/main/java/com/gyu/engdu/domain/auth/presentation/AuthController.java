package com.gyu.engdu.domain.auth.presentation;

import com.gyu.engdu.domain.auth.application.GoogleOAuthService;
import com.gyu.engdu.domain.auth.application.LogoutService;
import com.gyu.engdu.domain.auth.application.ReissueTokenService;
import com.gyu.engdu.domain.auth.application.dto.response.AuthTokenServiceResponse;
import com.gyu.engdu.domain.auth.presentation.dto.response.AuthTokenResponse;
import java.net.URI;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

  private static final long REFRESH_TOKEN_MAX_AGE_SECONDS = 14 * 24 * 60 * 60;
  private final GoogleOAuthService googleOAuthService;
  private final ReissueTokenService reissueTokenService;
  private final LogoutService logoutService;

  @Value("${oauth.google.login-uri}")
  private String loginUri;

  @Value("${oauth.google.local-login-uri}")
  private String localLoginUri;

  @Value("${oauth.google.redirect-uri}")
  private String redirectUri;

  @Value("${oauth.google.local-redirect-uri}")
  private String localRedirectUri;

  @GetMapping("/url")
  public ResponseEntity<Void> redirectGoogleLoginUrl() {
    return ResponseEntity
        .status(HttpStatus.TEMPORARY_REDIRECT)
        .location(URI.create(loginUri))
        .build();
  }

  @GetMapping("/local/url")
  public ResponseEntity<Void> redirectLocalGoogleLoginUrl() {
    return ResponseEntity
        .status(HttpStatus.TEMPORARY_REDIRECT)
        .location(URI.create(localLoginUri))
        .build();
  }

  // google OAuth 회원가입을 위한 메서드. 인증을 시도하고 토큰을 발급함.
  @GetMapping("/signup/oauth")
  public ResponseEntity<AuthTokenResponse> loginByGoogle(@RequestParam("code") String code) {
    AuthTokenServiceResponse authTokenServiceResponse = googleOAuthService.signUp(code,
        redirectUri);
    ResponseCookie cookie = createCookie(authTokenServiceResponse.refreshToken().getRawToken(),
        REFRESH_TOKEN_MAX_AGE_SECONDS);
    AuthTokenResponse authTokenResponse = AuthTokenResponse.from(authTokenServiceResponse);
    return ResponseEntity.ok()
        .header(HttpHeaders.SET_COOKIE, cookie.toString())
        .body(authTokenResponse);
  }

  // 프론트엔드 개발 환경을 위한 google OAuth 로그인 편의 api.
  @GetMapping("/local/signup/oauth")
  public ResponseEntity<AuthTokenResponse> loginByGoogleLocal(@RequestParam("code") String code) {
    AuthTokenServiceResponse authTokenServiceResponse = googleOAuthService.signUp(code,
        localRedirectUri);
    ResponseCookie cookie = createCookie(authTokenServiceResponse.refreshToken().getRawToken(),
        REFRESH_TOKEN_MAX_AGE_SECONDS);
    AuthTokenResponse authTokenResponse = AuthTokenResponse.from(authTokenServiceResponse);
    return ResponseEntity.ok()
        .header(HttpHeaders.SET_COOKIE, cookie.toString())
        .body(authTokenResponse);
  }

  @GetMapping("/reissue")
  public ResponseEntity<AuthTokenResponse> reissue(
      @CookieValue("refresh-token") String refreshToken) {
    AuthTokenServiceResponse authTokenServiceResponse = reissueTokenService.reissue(refreshToken,
        new Date());
    ResponseCookie cookie = createCookie(authTokenServiceResponse.refreshToken().getRawToken(),
        REFRESH_TOKEN_MAX_AGE_SECONDS);
    AuthTokenResponse authTokenResponse = AuthTokenResponse.from(authTokenServiceResponse);
    return ResponseEntity.ok()
        .header(HttpHeaders.SET_COOKIE, cookie.toString())
        .body(authTokenResponse);
  }

  @PostMapping("/logout")
  public ResponseEntity<Void> logout(
      @CookieValue("refresh-token") String refreshToken) {
    logoutService.logout(refreshToken);
    ResponseCookie deletedCookie = createCookie(refreshToken, 0);
    return ResponseEntity.noContent()
        .header(HttpHeaders.SET_COOKIE, deletedCookie.toString())
        .build();
  }

  private ResponseCookie createCookie(String rawRefreshToken, long maxAgeSeconds) {
    return ResponseCookie.from("refresh-token", rawRefreshToken)
        .httpOnly(true)
        .secure(true)
        .sameSite("Lax")
        .path("/api/v1/auth")
        .maxAge(maxAgeSeconds)
        .build();
  }
}
