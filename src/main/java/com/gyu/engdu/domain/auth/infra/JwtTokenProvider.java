package com.gyu.engdu.domain.auth.infra;


import com.gyu.engdu.domain.auth.application.TokenProvider;
import com.gyu.engdu.domain.user.domain.Role;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.security.Key;
import java.time.Duration;
import java.util.Date;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class JwtTokenProvider implements TokenProvider {

  private final Key signingKey;

  @Override
  public String createRawAccessToken(Long userId, Role role, Date issuedAt) {
    return Jwts.builder()
        .setSubject(userId.toString())
        .claim("role", role.name())
        .setIssuedAt(issuedAt)
        .setExpiration(getExpirationDateAfter1hour(issuedAt))
        .signWith(getSigningKey(), SignatureAlgorithm.HS256)
        .compact();
  }

  @Override
  public String createRawRefreshToken(Long userId, Date issuedAt) {
    return Jwts.builder()
        .setSubject(userId.toString())
        .setIssuedAt(issuedAt)
        .setExpiration(getExpirationDateAfter14Days(issuedAt))
        .signWith(getSigningKey(), SignatureAlgorithm.HS256)
        .compact();
  }

  private Date getExpirationDateAfter1hour(Date issuedAt) {
    return Date.from(issuedAt.toInstant().plus(Duration.ofHours(1L)));
  }

  private Date getExpirationDateAfter14Days(Date issuedAt) {
    return Date.from(issuedAt.toInstant().plus(Duration.ofDays(14)));
  }
}