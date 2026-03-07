package com.gyu.engdu.domain.auth.infra;

import com.gyu.engdu.domain.auth.application.OAuthClient;
import com.gyu.engdu.domain.auth.exception.GoogleOAuth4xxException;
import com.gyu.engdu.domain.auth.exception.GoogleOAuth5xxException;
import com.gyu.engdu.domain.auth.infra.dto.response.GoogleOAuthToken;
import com.gyu.engdu.domain.auth.infra.dto.response.GoogleUserInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

@Component
@Slf4j
public class GoogleOauthClient implements OAuthClient {

  private final RestClient googleCodeClient;
  private final RestClient googleAccessTokenClient;

  @Value("${oauth.google.client-id}")
  private String clientId;

  @Value("${oauth.google.client-secret}")
  private String clientSecret;

  public GoogleOauthClient(
      @Qualifier("googleCodeClient") RestClient googleCodeClient,
      @Qualifier("googleAccessTokenClient") RestClient googleAccessTokenClient) {
    this.googleCodeClient = googleCodeClient;
    this.googleAccessTokenClient = googleAccessTokenClient;
  }

  @Override
  public GoogleOAuthToken exchangeCodeToOAuthToken(String code, String redirectUri) {
    log.info("구글에 OAuthToken 발급 요청 code: {}", code);
    GoogleOAuthToken googleOAuthToken = requestOAuthTokenFromGoogle(buildTokenRequestMap(code, redirectUri));
    log.info("구글에 OAuthToken 발급 완료 accessToken: {}", googleOAuthToken.getAccessToken());
    return googleOAuthToken;
  }

  @Override
  public GoogleUserInfo exchangeAccessTokenToUserInfo(String accessToken) {
    return requestUserInfoFromGoogle(accessToken);
  }

  // OAuthToken을 받아오기 위해 정해진 Api 스펙대로 request body를 작성한다.
  private MultiValueMap<String, String> buildTokenRequestMap(String code, String redirectUri) {
    MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
    map.add("grant_type", "authorization_code");
    map.add("code", code);
    map.add("client_id", clientId);
    map.add("redirect_uri", redirectUri);
    map.add("client_secret", clientSecret);
    return map;
  }

  /**
   * Google에 사용자의 accessToken을 기반으로 사용자 정보를 요청한다.
   */
  private GoogleUserInfo requestUserInfoFromGoogle(String accessToken) {
    return googleAccessTokenClient.get()
        .uri(uriBuilder -> uriBuilder
            .path("/oauth2/v3/userinfo")
            .queryParam("access_token", accessToken)
            .build())
        .retrieve()
        .body(GoogleUserInfo.class);
  }

  /**
   * Google에 OAuthToken을 요청한다. OAuthToken에는 accessToken, idToken이 존재한다.
   */
  private GoogleOAuthToken requestOAuthTokenFromGoogle(MultiValueMap<String, String> map) {

    return googleCodeClient.post()
        .uri("/token")
        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        .body(map)
        .retrieve()
        .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
          log.error("Google OAuth 4XX error: {}", res.getStatusCode());
          throw new GoogleOAuth4xxException(res.getStatusCode().value());
        })
        .onStatus(HttpStatusCode::is5xxServerError, (req, res) -> {
          log.error("Google OAuth 5XX error: {}", res.getStatusCode());
          throw new GoogleOAuth5xxException(res.getStatusCode().value());
        })
        .body(GoogleOAuthToken.class);
  }
}
