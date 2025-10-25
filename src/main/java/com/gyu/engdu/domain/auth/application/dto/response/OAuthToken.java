package com.gyu.engdu.domain.auth.application.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public abstract class OAuthToken {

  @JsonProperty("access_token")
  private String accessToken;

  @JsonProperty("id_token")
  private String idToken;
}