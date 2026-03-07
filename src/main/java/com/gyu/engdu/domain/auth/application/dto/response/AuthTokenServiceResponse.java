package com.gyu.engdu.domain.auth.application.dto.response;

import com.gyu.engdu.domain.auth.domain.AccessToken;
import com.gyu.engdu.domain.auth.domain.RefreshToken;

public record AuthTokenServiceResponse(
    AccessToken accessToken,
    RefreshToken refreshToken
) {
  public static AuthTokenServiceResponse of (AccessToken accessToken, RefreshToken refreshToken){
    return new AuthTokenServiceResponse(accessToken, refreshToken);
  }
}
