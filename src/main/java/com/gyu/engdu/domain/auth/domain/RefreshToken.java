package com.gyu.engdu.domain.auth.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshToken extends Token {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "refresh_token_id")
  Long id;

  Long userId;

  private RefreshToken(Long userId, String rawToken) {
    this.userId = userId;
    this.rawToken = rawToken;
  }

  public static RefreshToken of(Long userId, String rawToken) {
    return new RefreshToken(userId, rawToken);
  }
}
