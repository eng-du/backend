package com.gyu.engdu.domain.auth.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gyu.engdu.domain.auth.application.dto.response.AuthTokenServiceResponse;
import com.gyu.engdu.domain.auth.application.dto.response.OAuthToken;
import com.gyu.engdu.domain.auth.application.dto.response.OAuthUserInfo;
import com.gyu.engdu.domain.user.application.CreateUserService;
import com.gyu.engdu.domain.user.application.UserQueryService;
import com.gyu.engdu.domain.user.domain.User;
import com.gyu.engdu.exception.CustomException;
import com.gyu.engdu.exception.ErrorCode;
import java.util.Base64;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GoogleOAuthService {

  private final CreateTokenService createTokenService;
  private final ObjectMapper jacksonObjectMapper;
  private final OAuthClient oAuthClient;
  private final CreateUserService createUserService;
  private final UserQueryService userQueryService;

  public AuthTokenServiceResponse signUp(String code) {

    //sub 받아오고
    OAuthToken oAuthToken = oAuthClient.exchangeCodeToOAuthToken(code);
    String sub = extractSubFromIdToken(oAuthToken.getIdToken());

    //유저 Sub으로 조회
    User user = userQueryService.findUserBySub(sub)
        .orElseGet(() -> {
          //기존의 회원이 존재하지 않는다면 구글 sub 기반으로 신규 회원 가입을 진행한다.
          OAuthUserInfo userInfo = oAuthClient.exchangeAccessTokenToUserInfo(
              oAuthToken.getAccessToken());
          return createUserService.create(sub, userInfo.getEmail());
        });

    //TODO: 리프레시 토큰 DB 저장
    return createTokenService.createAuthToken(user.getId(), user.getRole(), new Date());
  }

  /**
   * 구글의 idToken으로부터 유저의 sub를 추출한다.
   */
  private String extractSubFromIdToken(String idToken) {
    try {
      String[] parts = idToken.split("\\.");
      String payload = parts[1];
      String json = new String(Base64.getUrlDecoder().decode(payload));
      JsonNode node = jacksonObjectMapper.readTree(json);

      return node.get("sub").asText();
    } catch (JsonProcessingException e) {
      throw new CustomException(ErrorCode.INVALID_ID_TOKEN);
    }
  }
}
