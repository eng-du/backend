package com.gyu.engdu.domain.auth.presentation;

import com.gyu.engdu.domain.auth.application.GoogleOAuthService;
import com.gyu.engdu.domain.auth.application.dto.response.AuthTokenServiceResponse;
import com.gyu.engdu.domain.auth.presentation.dto.response.AuthTokenResponse;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

  private final GoogleOAuthService googleOAuthService;

  @GetMapping("/url")
  public ResponseEntity<Void> redirectGoogleLoginUrl() {
    //String redirectUrl = "https://accounts.google.com/o/oauth2/auth?scope=https://www.googleapis.com/auth/userinfo.email+https://www.googleapis.com/auth/userinfo.profile&response_type=code&access_type=offline&redirect_uri=https://www.ddeng-gu.shop/api/v1/login/oauth/signup&client_id=184642286173-vkd43ig36jr6ui7e4a5r01mtb81ehdo1.apps.googleusercontent.com";
    String redirectUrl = "https://accounts.google.com/o/oauth2/auth?scope=https://www.googleapis.com/auth/userinfo.email+https://www.googleapis.com/auth/userinfo.profile&response_type=code&access_type=offline&redirect_uri=http://localhost:8080/api/v1/auth/signup/oauth&client_id=184642286173-vkd43ig36jr6ui7e4a5r01mtb81ehdo1.apps.googleusercontent.com";

    return ResponseEntity
        .status(HttpStatus.TEMPORARY_REDIRECT)
        .location(URI.create(redirectUrl))
        .build();
  }

  //google OAuth 회원가입을 위한 메서드. 인증을 시도하고 토큰을 발급함.
  @GetMapping("/signup/oauth")
  public AuthTokenResponse loginByGoogle(@RequestParam("code") String code) {
    AuthTokenServiceResponse authToken = googleOAuthService.signUp(code);
    return AuthTokenResponse.from(authToken);
  }

//  //TODO: 리프레시 토큰으로 재발급
//  @GetMapping("/reissue")
//  public TokenReIssueResponse reissue(HttpServletRequest request) {
//  }
//
//  private String getTokenAndValidateRequest(HttpServletRequest request) {
//    String jwtHeader = request.getHeader("Authorization");
//    if (!StringUtils.hasText(jwtHeader) || !jwtHeader.startsWith("Bearer ")) {
//      throw new CustomException(ErrorCode.JWT_INVALID);
//    }
//    return jwtHeader.replace("Bearer ", "");
//  }
}
