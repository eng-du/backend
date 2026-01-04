package com.gyu.engdu.domain.user.application.dto.reponse;

import com.gyu.engdu.domain.user.domain.User;

public record UserSummaryResponse(
    String name
) {

  public static UserSummaryResponse fromEntity(User user) {
    return new UserSummaryResponse(user.getName());
  }
}
