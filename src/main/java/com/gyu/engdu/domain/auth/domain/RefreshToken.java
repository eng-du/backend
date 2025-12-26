package com.gyu.engdu.domain.auth.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.util.Date;
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

  @Column(name = "created_at")
  Date createdAt;

  private RefreshToken(Long userId, String rawToken, Date createdAt) {
    this.userId = userId;
    this.rawToken = rawToken;
    this.createdAt = createdAt;
  }

  public static RefreshToken of(Long userId, String rawToken, Date createdAt) {
    return new RefreshToken(userId, rawToken, createdAt);
  }
}
