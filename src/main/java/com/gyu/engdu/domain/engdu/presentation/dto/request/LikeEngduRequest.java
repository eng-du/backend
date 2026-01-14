package com.gyu.engdu.domain.engdu.presentation.dto.request;

import com.gyu.engdu.domain.engdu.domain.enums.LikeStatus;
import jakarta.validation.constraints.NotNull;

public record LikeEngduRequest(
    @NotNull(message = "좋아요 상태값은 필수입니다.") LikeStatus likeStatus) {

}
