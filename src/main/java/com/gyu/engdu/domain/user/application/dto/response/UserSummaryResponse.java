package com.gyu.engdu.domain.user.application.dto.response;

import com.gyu.engdu.domain.user.domain.User;

public record UserSummaryResponse(
    Long userId,
    String name) {

  public static UserSummaryResponse fromEntity(User user) {
    return new UserSummaryResponse(user.getId(), user.getName());
  }
}
