package com.gyu.engdu.domain.auth.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gyu.engdu.domain.auth.application.dto.response.OAuthToken;
import com.gyu.engdu.domain.auth.application.dto.response.OAuthUserInfo;
import com.gyu.engdu.domain.auth.application.dto.response.OAuthUserProfile;
import com.gyu.engdu.domain.auth.exception.InvalidIdTokenException;
import java.util.Base64;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component("googleOAuthClient")
@RequiredArgsConstructor
public class GoogleOAuthClient implements OAuthClientStrategy {


  private final OAuthClient oAuthClient;
  private final ObjectMapper jacksonObjectMapper;

  @Override
  public OAuthUserProfile fetchUserInfo(String code, String redirectUri) {
    OAuthToken oAuthToken = oAuthClient.exchangeCodeToOAuthToken(code, redirectUri);
    String sub = extractSubFromIdToken(oAuthToken.getIdToken());
    
    OAuthUserInfo userInfo = oAuthClient.exchangeAccessTokenToUserInfo(oAuthToken.getAccessToken());
    
    return new OAuthUserProfile(sub, userInfo.getEmail());
  }

  private String extractSubFromIdToken(String idToken) {
    try {
      String[] parts = idToken.split("\\.");
      String payload = parts[1];
      String json = new String(Base64.getUrlDecoder().decode(payload));
      JsonNode node = jacksonObjectMapper.readTree(json);

      return node.get("sub").asText();
    } catch (JsonProcessingException e) {
      throw new InvalidIdTokenException(e.getMessage());
    }
  }
}
