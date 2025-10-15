package com.gyu.engdu.domain.auth.application;

import com.gyu.engdu.domain.user.domain.Role;

public interface TokenParser {

  public Role parseRoleFromAccessToken(String accessToken);

  public Long parseUserIdFromToken(String token);
}
