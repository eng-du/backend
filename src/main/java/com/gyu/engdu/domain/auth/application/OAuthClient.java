package com.gyu.engdu.domain.auth.application;

import com.gyu.engdu.domain.auth.application.dto.response.OAuthToken;
import com.gyu.engdu.domain.auth.application.dto.response.OAuthUserInfo;

public interface OAuthClient {

  OAuthToken exchangeCodeToOAuthToken(String code, String redirectUri);

  OAuthUserInfo exchangeAccessTokenToUserInfo(String accessToken);
}
