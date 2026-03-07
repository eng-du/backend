package com.gyu.engdu.domain.user.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangeUserNameRequest(
    @NotBlank(message = "이름은 필수입니다.")
    @Size(max = 30, message = "이름은 30자를 초과할 수 없습니다.")
    String name
) {

}
