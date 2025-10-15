package com.gyu.engdu.domain.auth.infra;

import com.gyu.engdu.domain.auth.application.TokenParser;
import com.gyu.engdu.domain.user.domain.Role;
import com.gyu.engdu.exception.CustomException;
import com.gyu.engdu.exception.ErrorCode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import java.security.Key;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class JwtTokenParser implements TokenParser {

  private final Key signingKey;

  @Override
  public Role parseRoleFromAccessToken(String token) {
    Claims claims = getAllClaimsFromToken(getSigningKey(), token);
    String role = claims.get("role", String.class);
    return Role.valueOf(role);
  }

  @Override
  public Long parseUserIdFromToken(String token) {
    return Long.valueOf(getAllClaimsFromToken(getSigningKey(), token).getSubject());
  }

  //jwt 토큰의 유효시간이 지났거나 유효하지 않은 형식이라면 에러를 던진다.
  private Claims getAllClaimsFromToken(Key signingKey, String token) {
    try {
      return Jwts.parserBuilder()
          .setSigningKey(signingKey)
          .build()
          .parseClaimsJws(token)
          .getBody();
    } catch (ExpiredJwtException e) {
      throw new CustomException(ErrorCode.JWT_EXPIRED);
    } catch (JwtException e) {
      throw new CustomException(ErrorCode.JWT_INVALID);
    }
  }
}
