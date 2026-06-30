package com.gyu.engdu.domain.auth.application;

import com.gyu.engdu.domain.auth.application.dto.response.OAuthUserProfile;

public interface OAuthClientStrategy {

    OAuthUserProfile fetchUserInfo(String code, String redirectUri);
}
