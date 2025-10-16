package com.gyu.engdu.domain.auth.application;

import com.gyu.engdu.domain.user.domain.Role;

public interface TokenParser {

  Role parseRoleFromAccessToken(String accessToken);

  Long parseUserIdFromToken(String token);
}
