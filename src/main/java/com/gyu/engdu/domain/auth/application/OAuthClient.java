package com.gyu.engdu.domain.auth.application;

import com.gyu.engdu.domain.auth.application.dto.response.OAuthToken;
import com.gyu.engdu.domain.auth.application.dto.response.OAuthUserInfo;

public interface OAuthClient {

  OAuthToken exchangeCodeToOAuthToken(String code);

  OAuthUserInfo exchangeAccessTokenToUserInfo(String accessToken);
}
