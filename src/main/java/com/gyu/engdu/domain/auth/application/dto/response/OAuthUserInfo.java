package com.gyu.engdu.domain.auth.application.dto.response;

import lombok.Getter;

@Getter
public abstract class OAuthUserInfo {

  private String name;
  private String email;
}
