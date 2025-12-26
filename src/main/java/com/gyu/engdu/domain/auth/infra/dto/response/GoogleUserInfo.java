package com.gyu.engdu.domain.auth.infra.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.gyu.engdu.domain.auth.application.dto.response.OAuthUserInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class GoogleUserInfo extends OAuthUserInfo {

  private String id;

  @JsonProperty("verified_email")
  private boolean verifiedEmail;

  @JsonProperty("given_name")
  private String givenName;

  @JsonProperty("family_name")
  private String familyName;

  private String picture;
  private String locale;
}