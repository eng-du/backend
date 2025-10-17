package com.gyu.engdu.domain.auth.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Token {

  String rawToken;

  public Token(String rawToken) {
    this.rawToken = rawToken;
  }
}
