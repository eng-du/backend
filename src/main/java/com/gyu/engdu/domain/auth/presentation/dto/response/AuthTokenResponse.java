package com.gyu.engdu.domain.auth.presentation.dto.response;

import com.gyu.engdu.domain.auth.application.dto.response.AuthTokenServiceResponse;

public record AuthTokenResponse(
    String accessToken,
    String refreshToken
) {

  public static AuthTokenResponse from(AuthTokenServiceResponse authTokenServiceResponse) {

    return new AuthTokenResponse(
        authTokenServiceResponse.accessToken().getRawToken(),
        authTokenServiceResponse.refreshToken().getRawToken()
    );
  }
}
