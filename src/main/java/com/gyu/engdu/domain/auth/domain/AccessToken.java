package com.gyu.engdu.domain.auth.domain;

import lombok.Getter;

@Getter
public class AccessToken extends Token{

  private AccessToken(String rawToken) {
    super(rawToken);
  }

  public static AccessToken of(String rawToken) {
    return new AccessToken(rawToken);
  }
}
