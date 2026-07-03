package com.gyu.engdu.domain.auth.presentation;

import com.gyu.engdu.domain.auth.application.LogoutService;
import com.gyu.engdu.domain.auth.application.OAuthLoginService;
import com.gyu.engdu.domain.auth.application.ReissueTokenService;
import com.gyu.engdu.domain.auth.application.dto.response.AuthTokenServiceResponse;
import com.gyu.engdu.domain.auth.domain.OAuthProvider;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private static final long REFRESH_TOKEN_MAX_AGE_SECONDS = 14 * 24 * 60 * 60;
    private final OAuthLoginService oauthLoginService;
    private final ReissueTokenService reissueTokenService;
    private final LogoutService logoutService;

    @Value("${oauth.google.login-uri}")
    private String loginUri;

    @Value("${oauth.google.redirect-uri}")
    private String redirectUri;

    @GetMapping("/url")
    public ResponseEntity<Void> redirectGoogleLoginUrl() {
        return ResponseEntity
                .status(HttpStatus.TEMPORARY_REDIRECT)
                .location(URI.create(loginUri))
                .build();
    }

    // 하위 호환성을 위한 기존 엔드포인트
    @GetMapping("/signup/oauth")
    public ResponseEntity<AuthTokenResponse> loginByGoogle(@RequestParam("code") String code) {
        return loginByProvider(OAuthProvider.GOOGLE, code);
    }

    // 신규 동적 제공자 엔드포인트
    @GetMapping("/signup/oauth/{provider}")
    public ResponseEntity<AuthTokenResponse> loginByProvider(
            @PathVariable("provider") OAuthProvider provider,
            @RequestParam("code") String code
    ) {
        AuthTokenServiceResponse authTokenServiceResponse = oauthLoginService.login(provider, code,
                redirectUri);
        ResponseCookie cookie = createCookie(authTokenServiceResponse.refreshToken().getRawToken(),
                REFRESH_TOKEN_MAX_AGE_SECONDS);
        AuthTokenResponse authTokenResponse = AuthTokenResponse.from(authTokenServiceResponse);
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(authTokenResponse);
    }

    @GetMapping("/reissue")
    public ResponseEntity<AuthTokenResponse> reissue(
            @CookieValue("refresh-token") String refreshToken
    ) {
        AuthTokenServiceResponse authTokenServiceResponse = reissueTokenService.reissue(
                refreshToken,
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
            @CookieValue("refresh-token") String refreshToken
    ) {
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
