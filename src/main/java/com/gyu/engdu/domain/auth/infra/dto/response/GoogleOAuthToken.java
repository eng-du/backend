package com.gyu.engdu.domain.auth.infra.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.gyu.engdu.domain.auth.application.dto.response.OAuthToken;
import lombok.Getter;

@Getter
public class GoogleOAuthToken extends OAuthToken {

  @JsonProperty("expires_in")
  private int expiresIn;

  @JsonProperty("refresh_token")
  private String refreshToken;

  @JsonProperty("scope")
  private String scope;

  @JsonProperty("token_type")
  private String tokenType;
}