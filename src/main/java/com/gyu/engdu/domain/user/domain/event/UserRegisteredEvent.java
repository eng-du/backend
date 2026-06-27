package com.gyu.engdu.domain.user.domain.event;

public record UserRegisteredEvent(Long userId, String nickname) {
}
