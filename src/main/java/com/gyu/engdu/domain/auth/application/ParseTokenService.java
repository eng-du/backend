package com.gyu.engdu.domain.auth.application;

import com.gyu.engdu.domain.auth.domain.AccessToken;
import com.gyu.engdu.domain.auth.domain.Token;
import com.gyu.engdu.domain.user.domain.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ParseTokenService {

  private final TokenParser tokenParser;

  public Role extractRole(AccessToken accessToken) {
    return tokenParser.parseRoleFromAccessToken(accessToken.getRawToken());
  }

  public Long extractUserId(Token token) {
    return tokenParser.parseUserIdFromToken(token.getRawToken());
  }
}
