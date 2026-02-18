package com.gyu.engdu.domain.user.application.dto.response;

import com.gyu.engdu.domain.user.domain.User;

public record UserDetailResponse(
        String email,
        String name) {

    public static UserDetailResponse fromEntity(User user) {
        return new UserDetailResponse(user.getEmail(), user.getName());
    }
}
