package com.gyu.engdu.domain.auth.domain;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@MappedSuperclass
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Token {

  @Column(nullable = false)
  String rawToken;

  public Token(String rawToken) {
    this.rawToken = rawToken;
  }
}
