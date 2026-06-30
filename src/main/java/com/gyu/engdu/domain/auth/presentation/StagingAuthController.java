package com.gyu.engdu.domain.auth.presentation;

import com.gyu.engdu.domain.auth.application.FakeLoginService;
import com.gyu.engdu.domain.auth.application.OAuthLoginService;
import com.gyu.engdu.domain.auth.application.dto.response.AuthTokenServiceResponse;
import com.gyu.engdu.domain.auth.presentation.dto.response.AuthTokenResponse;
import com.gyu.engdu.domain.auth.domain.OAuthProvider;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Profile("!prod")
public class StagingAuthController {

  private static final long REFRESH_TOKEN_MAX_AGE_SECONDS = 14 * 24 * 60 * 60;
  private static final long FAKE_LOGIN_USER_ID = 1L;
  private final OAuthLoginService oauthLoginService;
  private final FakeLoginService fakeLoginService;

  @Value("${oauth.google.local-login-uri}")
  private String localLoginUri;

  @Value("${oauth.google.local-redirect-uri}")
  private String localRedirectUri;

  @GetMapping("/local/url")
  public ResponseEntity<Void> redirectLocalGoogleLoginUrl() {
    return ResponseEntity
        .status(HttpStatus.TEMPORARY_REDIRECT)
        .location(URI.create(localLoginUri))
        .build();
  }

  @GetMapping("/local/signup/oauth")
  public ResponseEntity<AuthTokenResponse> loginByGoogleLocal(@RequestParam("code") String code) {
    OAuthProvider provider = OAuthProvider.GOOGLE;
    AuthTokenServiceResponse authTokenServiceResponse = oauthLoginService.login(provider, code,
        localRedirectUri);
    ResponseCookie cookie = createCookie(authTokenServiceResponse.refreshToken().getRawToken(),
        REFRESH_TOKEN_MAX_AGE_SECONDS);
    return ResponseEntity.ok()
        .header(HttpHeaders.SET_COOKIE, cookie.toString())
        .body(AuthTokenResponse.from(authTokenServiceResponse));
  }

  @GetMapping("/fake/signup/oauth")
  public ResponseEntity<AuthTokenResponse> fakeLogin() {
    AuthTokenServiceResponse authTokenServiceResponse = fakeLoginService.fakeLogin(
        FAKE_LOGIN_USER_ID);
    ResponseCookie cookie = createCookie(authTokenServiceResponse.refreshToken().getRawToken(),
        REFRESH_TOKEN_MAX_AGE_SECONDS);
    return ResponseEntity.ok()
        .header(HttpHeaders.SET_COOKIE, cookie.toString())
        .body(AuthTokenResponse.from(authTokenServiceResponse));
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
