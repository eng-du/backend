package com.gyu.engdu.domain.auth.presentation;

import com.gyu.engdu.domain.auth.application.GoogleOAuthService;
import com.gyu.engdu.domain.auth.application.ReissueTokenService;
import com.gyu.engdu.domain.auth.application.dto.response.AuthTokenServiceResponse;
import com.gyu.engdu.domain.auth.presentation.dto.response.AuthTokenResponse;
import java.net.URI;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

  private final GoogleOAuthService googleOAuthService;
  private final ReissueTokenService reissueTokenService;

  @GetMapping("/url")
  public ResponseEntity<Void> redirectGoogleLoginUrl() {
    //String redirectUrl = "https://accounts.google.com/o/oauth2/auth?scope=https://www.googleapis.com/auth/userinfo.email+https://www.googleapis.com/auth/userinfo.profile&response_type=code&access_type=offline&redirect_uri=https://www.ddeng-gu.shop/api/v1/login/oauth/signup&client_id=184642286173-vkd43ig36jr6ui7e4a5r01mtb81ehdo1.apps.googleusercontent.com";
    String redirectUrl = "https://accounts.google.com/o/oauth2/auth?scope=https://www.googleapis.com/auth/userinfo.email+https://www.googleapis.com/auth/userinfo.profile&response_type=code&access_type=offline&redirect_uri=http://localhost:8080/api/v1/auth/signup/oauth&client_id=184642286173-vkd43ig36jr6ui7e4a5r01mtb81ehdo1.apps.googleusercontent.com";

    return ResponseEntity
        .status(HttpStatus.TEMPORARY_REDIRECT)
        .location(URI.create(redirectUrl))
        .build();
  }

  // google OAuth 회원가입을 위한 메서드. 인증을 시도하고 토큰을 발급함.
  @GetMapping("/signup/oauth")
  public ResponseEntity<AuthTokenResponse> loginByGoogle(@RequestParam("code") String code) {
    AuthTokenServiceResponse authTokenServiceResponse = googleOAuthService.signUp(code);
    long maxAge = 60 * 60 * 24 * 14;
    ResponseCookie cookie = createCookie(authTokenServiceResponse.refreshToken().getRawToken(),
        maxAge);
    AuthTokenResponse authTokenResponse = AuthTokenResponse.from(authTokenServiceResponse);
    return ResponseEntity.ok()
        .header(HttpHeaders.SET_COOKIE, cookie.toString())
        .body(authTokenResponse);
  }

  @GetMapping("/reissue")
  public ResponseEntity<AuthTokenResponse> reissue(
      @CookieValue("refresh-token") String refreshToken) {
    AuthTokenServiceResponse authTokenServiceResponse = reissueTokenService.reissue(refreshToken, new Date());
    long maxAge = 60 * 60 * 24 * 14;
    ResponseCookie cookie = createCookie(authTokenServiceResponse.refreshToken().getRawToken(),
        maxAge);
    AuthTokenResponse authTokenResponse = AuthTokenResponse.from(authTokenServiceResponse);
    return ResponseEntity.ok()
        .header(HttpHeaders.SET_COOKIE, cookie.toString())
        .body(authTokenResponse);
  }

  public ResponseCookie createCookie(String rawRefreshToken, long maxAgeSeconds) {
    return ResponseCookie.from("refresh-token", rawRefreshToken)
        .httpOnly(true)
        .secure(true)
        .sameSite("Lax")
        .path("/api/v1/auth")
        .maxAge(maxAgeSeconds)
        .build();
  }
}
